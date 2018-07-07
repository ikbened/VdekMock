package com.spronq.mbt.VdekMock.api;

import com.spronq.mbt.VdekMock.model.Shipment;
import com.spronq.mbt.VdekMock.model.User;
import com.spronq.mbt.VdekMock.model.UserClaim;
import com.spronq.mbt.VdekMock.repository.ShipmentsRepository;
import com.spronq.mbt.VdekMock.repository.UserClaimsRepository;
import com.spronq.mbt.VdekMock.repository.UsersRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/shipments", produces = MediaType.APPLICATION_JSON_VALUE)
public class VdekApi {

    private ShipmentsRepository shipmentsRepository;
    private UsersRepository userRepository;
    private UserClaimsRepository userClaimsRepository;

    @Autowired
    public VdekApi(ShipmentsRepository repository, UsersRepository userRepository, UserClaimsRepository userClaims) {
        this.shipmentsRepository = repository;
        this.userRepository = userRepository;
        this.userClaimsRepository = userClaims;
    }

    @GetMapping
    public Flux<Shipment> getAllShipments() {
        return shipmentsRepository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Shipment>> getShipmentById(@PathVariable(value = "id") String shipmentId) {
        return shipmentsRepository.findById(shipmentId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public Mono<Shipment> createShipments(@Valid @RequestBody Shipment shipment) {

        String errMsg = resolveCustomer(shipment);

        if (StringUtils.isEmpty(errMsg))
            errMsg = resolveUser(shipment);

        if (StringUtils.isEmpty(errMsg)) {
            shipment.setProcessedByTask(true);
        } else {
            shipment.setErrorMessage(errMsg);
            shipment.setProcessedByTask(false);
        }

        return shipmentsRepository.save(shipment);
    }

    private String resolveCustomer(Shipment shipment) {
        String email;
        User customer;

        if ( !IsCustomerNumberUnique(shipment.getCustomerNumber()) ) {
            return "CustomerNumber is not unique.";
        }

        if ( StringUtils.isEmpty(shipment.getEmailAddress()) ) {
            email = shipment.getCustomerNumber() + "@thelearningnetwork.nl";
        } else {
            email = shipment.getEmailAddress();
        }

        if ( !IsEmailUniqueForLearnIdAccounts(email) ) {
            return "Customer email is not unique within LearnId";
        }

        shipment.setEmailUser(email);
        shipmentsRepository.save(shipment).block();

        if ( IsLearnIdAccountWithEmailPresent(email) ) {
            // This assumes there's only one customer found for this email
            customer = userRepository.findAllByEmail(email).blockFirst();
        } else {
            customer = new User();
            customer.setEmail(email);
            customer.setLabel("LearnId");
            userRepository.save(customer).block();
        }


//        if ( IsCustomerNumberInAccountSet(shipment.getCustomerNumber(), customer.getAccountSet())) {
//            // do nothing
//        } if ( IsCustomerNumberFoundOnAnyLearnIdAccount(shipment.getCustomerNumber())) {
//            User c = userRepository.findAllByCustomerNumber(customer.getAccountSet()).blockFirst();
//            linkUsers(customer, c);
//        } else {
//            //customer.setCustomerNumber(shipment.getCustomerNumber());
//            userRepository.save(customer).block();
//        }
//
//        customer.setPostalCode(shipment.getPostalCode());
//        userRepository.save(customer).block();

        return "";
    }

    private boolean IsCustomerNumberFoundOnAnyLearnIdAccount(String customerNumber) {
        //return userRepository.findAllByCustomerNumber(customerNumber) <> null;
        return true;
    }

    private void linkUsers(User u1, User u2) {
//        String accountSetId;
//        if (StringUtils.isEmpty(u1.getAccountSet()) && StringUtils.isEmpty(u1.getAccountSet())) {
//            accountSetId = java.util.UUID.randomUUID().toString();
//            u1.setAccountSet(accountSetId);
//            u2.setAccountSet(accountSetId);
//        } else if (StringUtils.isEmpty(u1.getAccountSet()) && !StringUtils.isEmpty(u1.getAccountSet())) {
//            u1.setAccountSet(u2.getAccountSet());
//        } else {
//            u2.setAccountSet(u1.getAccountSet());
//        }
//        userRepository.save(u1).block();
//        userRepository.save(u2).block();
    }

    private boolean IsCustomerNumberInAccountSet(String customerNumber, String accountSetId) {
        if (StringUtils.isEmpty(accountSetId)) {
            return false;
        } else {
//            User customer = userRepository.findAllByCustomerNumber(customerNumber).blockFirst();
//            return accountSetId.equals(customer.getAccountSet());
            return false;
        }
    }


    private Boolean IsLearnIdAccountWithEmailPresent(String email) {
        return CountLearnIdAccountsByEmail(email) == 1;
    }

    private Boolean IsEmailUniqueForLearnIdAccounts(String email) {
        return CountLearnIdAccountsByEmail(email) <= 1;
    }

    private Long CountLearnIdAccountsByEmail(String email) {
        return userRepository.findAllByEmail(email).toStream().filter(u -> u.getLabel().equalsIgnoreCase("LearnId")).count();

        // Directly filter and count flux, and not converting it to a stream.
        // long count = userRepository.findAllByEmail(email).filter(user -> user.getLabel().equalsIgnoreCase("LearnId")).count().block();
    }

    private Boolean IsCustomerNumberUnique(String customerNumber) {
        //return userClaimsRepository.findAllByClaimType("customerNumber").filter(userClaim -> userClaim.getClaimType().equalsIgnoreCase("CustomerNumber") ).toStream().count() <= 1;
        return userClaimsRepository.findAll({"claimType": "customerNumber", "claimValue": customerNumber}).   toStream().filter(userClaim -> userClaim.getClaimType().equalsIgnoreCase("CustomerNumber") ).toStream().count() <= 1;

    }


    private String resolveUser(Shipment shipment) {
        String email = "";
        User user;

        if (!shipment.getAdministration().equalsIgnoreCase("Dynamics")) {
            return  "Unknown administration";
        }

        if (StringUtils.isEmpty(shipment.getEmailUser())) {
            shipment.setEmailUser(shipment.getEmailAddress());
            shipmentsRepository.save(shipment).block();
            return "";
        }

        if ( !IsEmailUniqueForLearnIdAccounts(shipment.getEmailUser()) ) {
                return "User email is not unique within LearnId";
        }

        if ( !IsLearnIdAccountWithEmailPresent(shipment.getEmailUser()) ) {
            user = new User();
            user.setLabel("LearnId");
            user.setEmail(email);
            userRepository.save(user).block();
        } else {
            //Do nothing
        }

        return "";
    }
}
