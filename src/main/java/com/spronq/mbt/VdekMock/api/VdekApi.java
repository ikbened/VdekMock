package com.spronq.mbt.VdekMock.api;

import com.spronq.mbt.VdekMock.model.ExtendedShipment;
import com.spronq.mbt.VdekMock.repository.ExtendedShipmentRepository;
import com.spronq.mbt.VdekMock.repository.UserRepository;
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
    private UserRepository userRepository;

    @Autowired
    public VdekApi(ExtendedShipmentRepository repository, UserRepository userRepository) {
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

        if (!Optional.ofNullable(shipment.getCustomerNumber()).isPresent())
        {
            shipment.setErrorMessage("ERROR - customer number is missing");
            shipment.setProcessedByTask(false);
        }
        else if(userRepository.findAllByCustomerNumber(shipment.getCustomerNumber()).count() > 1)
        {
            shipment.setErrorMessage("ERROR - customer number is not unique");
            shipment.setProcessedByTask(false);
        }
        else {
            shipment.setProcessedByTask(true);
        }

        return repository.save(shipment);
    }


}
