package com.spronq.mbt.VdekMock.api;

import com.spronq.mbt.VdekMock.model.ExtendedShipment;
import com.spronq.mbt.VdekMock.repository.ExtendedShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ShipmentHandler {

    private ExtendedShipmentRepository shipmentRepository;

    @Autowired
    public ShipmentHandler(ExtendedShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    Mono<ServerResponse> allShipments(ServerRequest request) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(shipmentRepository.findAll(), ExtendedShipment.class);
    }

    Mono<ServerResponse> getShipmentById(ServerRequest request) {
        String id = request.pathVariable("id");

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(shipmentRepository.findById(id), ExtendedShipment.class);
    }

    Mono<ServerResponse> createShipment(ServerRequest request) {
        Mono<ExtendedShipment> shipment = request.bodyToMono(ExtendedShipment.class);

        return ServerResponse
                .accepted()
                .contentType(MediaType.APPLICATION_JSON)
                .body(shipmentRepository.insert(shipment), ExtendedShipment.class);
    }
}
