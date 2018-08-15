package com.spronq.mbt.VdekMock.api;

import com.spronq.mbt.VdekMock.model.ExtendedShipment;
import com.spronq.mbt.VdekMock.model.User;
import com.spronq.mbt.VdekMock.repository.ExtendedShipmentRepository;
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

    private ExtendedShipmentRepository repository;
    private UsersRepository userRepository;
    private UserClaimsRepository userClaimsRepository;

    @Autowired
    public VdekApi(ExtendedShipmentRepository repository, UsersRepository userRepository, UserClaimsRepository userClaimsRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.userClaimsRepository = userClaimsRepository;
    }

    @GetMapping
    public Flux<ExtendedShipment> getAllShipments() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ExtendedShipment>> getShipmentById(@PathVariable(value = "id") String shipmentId) {
        return repository.findById(shipmentId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public Mono<ExtendedShipment> createShipments(@Valid @RequestBody ExtendedShipment shipment) {

        String errMsg = resolveCustomer(shipment);
        if (StringUtils.isEmpty(errMsg))
            errMsg = resolveUser(shipment);

        if (StringUtils.isEmpty(errMsg)) {
            shipment.setProcessedByTask(true);
        } else {
            shipment.setErrorMessage(errMsg);
            shipment.setProcessedByTask(false);
        }

        return repository.save(shipment);
    }

    private String resolveCustomer(ExtendedShipment shipment) {
        String errMsg = "";
        String email = "";
        User customer;

        if ( !IsCustomerNumberUnique(shipment.getCustomerNumber()) ) {
            errMsg = "CustomerNumber is not unique.";
        }

        if (StringUtils.isEmpty(errMsg)) {
            if (StringUtils.isEmpty(shipment.getEmailAddress())) {
                email = shipment.getCustomerNumber() + "@thelearningnetwork.nl";
            } else {
                email = shipment.getEmailAddress();
            }
        }

        if (StringUtils.isEmpty(errMsg)) {
            if ( !IsEmailUniqueForLearnIdAccounts(email) ) {
                errMsg = "Customer email is not unique within LearnId";
            }
        }

        if (StringUtils.isEmpty(errMsg)) {
            shipment.setEmailUser(email);
            repository.save(shipment).block();

            if ( IsLearnIdAccountWithEmailPresent(email) ) {
                // This assumes there's only one customer found for this email
                customer = userRepository.findAllByEmail(email).blockFirst();
            } else {
                customer = new User();
                customer.setEmail(email);
                customer.setLabel("LearnId");
                userRepository.save(customer).block();
            }

            if (IsCustomerNumberInAccountSet(shipment.getCustomerNumber(), customer.getAccountSetId())) {
                // do nothing
            } else {
                User c = userRepository.findAllByCustomerNumber(customer.getAccountSetId()).blockFirst();

                if (c.equals(null)) {
                    customer.setCustomerNumber(shipment.getCustomerNumber());
                    userRepository.save(customer).block();
                } else {
                    linkUsers(customer, c);
                }
            }

            customer.setPostalCode(shipment.getPostalCode());
            userRepository.save(customer).block();
         }

        return errMsg;
    }

    private void linkUsers(User u1, User u2) {
        String accountSetId;
        if (StringUtils.isEmpty(u1.getAccountSetId()) && StringUtils.isEmpty(u1.getAccountSetId())) {
            accountSetId = java.util.UUID.randomUUID().toString();
            u1.setAccountSetId(accountSetId);
            u2.setAccountSetId(accountSetId);
        } else if (StringUtils.isEmpty(u1.getAccountSetId()) && !StringUtils.isEmpty(u1.getAccountSetId())) {
            u1.setAccountSetId(u2.getAccountSetId());
        } else {
            u2.setAccountSetId(u1.getAccountSetId());
        }
        userRepository.save(u1).block();
        userRepository.save(u2).block();
    }

    private boolean IsCustomerNumberInAccountSet(String customerNumber, String accountSetId) {
        if (StringUtils.isEmpty(accountSetId)) {
            return false;
        } else {
            User customer = userRepository.findAllByCustomerNumber(customerNumber).blockFirst();
            return accountSetId.equals(customer.getAccountSetId());
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
        //Is distinct on UserId necessary?
        return userClaimsRepository.findAllByClaimValue(customerNumber).toStream().filter(u -> u.getClaimType().equalsIgnoreCase("CustomerNumber")).count() <= 1;
    }


    private String resolveUser(ExtendedShipment shipment) {
        String email = "";
        User user;

        if (!shipment.getAdministration().equalsIgnoreCase("Dynamics")) {
            return  "Unknown administration";
        }

        if (StringUtils.isEmpty(shipment.getEmailUser())) {
            shipment.setEmailUser(shipment.getEmailAddress());
            repository.save(shipment).block();
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
