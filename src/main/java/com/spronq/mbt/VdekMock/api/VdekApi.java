package com.spronq.mbt.VdekMock.api;

import com.spronq.mbt.VdekMock.model.ExtendedShipment;
import com.spronq.mbt.VdekMock.model.User;
import com.spronq.mbt.VdekMock.repository.ExtendedShipmentRepository;
import com.spronq.mbt.VdekMock.repository.UsersRepository;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/shipments", produces = MediaType.APPLICATION_JSON_VALUE)
public class VdekApi {

    private ExtendedShipmentRepository repository;
    private UsersRepository userRepository;

    @Autowired
    public VdekApi(ExtendedShipmentRepository repository, UsersRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
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
        String custNumber = shipment.getCustomerNumber();

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
                customer = new User();
                //List<User> customers = userRepository.findAllByEmail(email).collectList();
                //Mono<User>
                //customers.ta
                //customer = customers.get(0);
            } else {
                customer = new User();
                customer.setEmail(shipment.getEmailAddress());
                customer.setCustomerNumber(shipment.getCustomerNumber());
                customer.setLabel("LearnId");

                userRepository.save(customer).block();
            }

            List<User> users=null;
            //Map<Integer, User> users = userRepository.findAllByCustomerNumber(custNumber).map();

            if (CollectionUtils.isEmpty(users)) { //users.size() == 1) {
//                User user0 = users.get(0);
//                if (String.isEmpty(user0.getAccountSetId())) {
//                    user0.setAccountSetId(java.util.UUID.randomUUID());
//                    userRepository.save(user0).block();
//                }
//                customer.setCustomerNumber(user0.getAccountSetId());
            } else {
                customer.setCustomerNumber(custNumber);
            }


            customer.setPostalCode(shipment.getPostalCode());
            userRepository.save(customer).block();
         }

        return errMsg;
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
        return userRepository.findAllByCustomerNumber(customerNumber).toStream().count() <= 1;
    }


    private String resolveUser(ExtendedShipment shipment) {
        String errMsg = "";
        String email = "";
        User user;

        if (!shipment.getAdministration().equalsIgnoreCase("Dynamics")) {
            errMsg = "Unknown administration";
        }

        if (StringUtils.isEmpty(errMsg)) {
            if (StringUtils.isEmpty(shipment.getEmailUser())) {
                shipment.setEmailUser(shipment.getEmailAddress());
                repository.save(shipment).block();
            }
        }

        if (StringUtils.isEmpty(errMsg)) {
            email = shipment.getEmailUser();
            if ( !IsEmailUniqueForLearnIdAccounts(email) ) {
                errMsg = "User email is not unique within LearnId";
            }
        }

        if (StringUtils.isEmpty(errMsg)) {
            if ( !IsLearnIdAccountWithEmailPresent(email) ) {
                user = new User();
                user.setLabel("LearnId");
                user.setEmail(email);
                userRepository.save(user).block();
            } else {
                //Do nothing
            }
        }

        return errMsg;

    }
}
