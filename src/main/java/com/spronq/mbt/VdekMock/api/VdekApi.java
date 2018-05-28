package com.spronq.mbt.VdekMock.api;

import com.spronq.mbt.VdekMock.model.ExtendedShipment;
import com.spronq.mbt.VdekMock.model.User;
import com.spronq.mbt.VdekMock.repository.ExtendedShipmentRepository;
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
import java.util.Optional;

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
            shipment.setErrorMessage("ERROR - customer number is missing");
            shipment.setProcessedByTask(false);
        }

        return repository.save(shipment);
    }

    private String resolveCustomer(ExtendedShipment shipment) {

        String errMsg = IsCustomerNumberNullOrEmpty(shipment.getCustomerNumber());
        String email = "";

        if (StringUtils.isEmpty(errMsg)) {
            if (StringUtils.isEmpty(shipment.getEmailAddress())) {
                email = shipment.getCustomerNumber();
            } else {
                email = shipment.getEmailAddress();
            }
        }

        if (StringUtils.isEmpty(errMsg)) {
            errMsg = IsEmailUniqueForLearnIdAccount(email);
        }


        return errMsg;
    }

    private String IsEmailUniqueForLearnIdAccount(String email) {
        String errMsg;
        Integer i = 0;

        for (User user : userRepository.findAllByEmail(email)) {
            if (user.getLabel().equals("LearnId"))
                i++;
        }

        if (i>1)
            errMsg = "Email is not unique.";
        else
            errMsg = "";

        return errMsg;
    }

    private String IsCustomerNumberNullOrEmpty(String customerNumber) {
        String errMsg;

        if (!Optional.ofNullable(customerNumber()).isPresent()) {
            errMsg = "Customer number is missing.";
        } else if(userRepository.findAllByCustomerNumber(customerNumber).count().block() > 1) {
            errMsg = "Customer number is not unique.";
        } else {
            errMsg = "";
        }

        return errMsg;
    }


    private String resolveUser(ExtendedShipment shipment) {

    }
}
