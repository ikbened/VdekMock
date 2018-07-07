package com.spronq.mbt.VdekMock.api;

import com.spronq.mbt.VdekMock.model.Shipment;
import com.spronq.mbt.VdekMock.repository.ShipmentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ShipmentHandler {

    private ShipmentsRepository shipmentsRepository;

    @Autowired
    public ShipmentHandler(ShipmentsRepository shipmentsRepository) {
        this.shipmentsRepository = shipmentsRepository;
    }

    Mono<ServerResponse> allShipments(ServerRequest request) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(shipmentsRepository.findAll(), Shipment.class);
    }

    Mono<ServerResponse> getShipmentById(ServerRequest request) {
        String id = request.pathVariable("id");

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(shipmentsRepository.findById(id), Shipment.class);
    }

    Mono<ServerResponse> createShipment(ServerRequest request) {
        Mono<Shipment> shipment = request.bodyToMono(Shipment.class);

        return ServerResponse
                .accepted()
                .contentType(MediaType.APPLICATION_JSON)
                .body(shipmentsRepository.insert(shipment), Shipment.class);
    }
}
