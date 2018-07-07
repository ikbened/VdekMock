package com.spronq.mbt.VdekMock.repository;

import com.spronq.mbt.VdekMock.model.Shipment;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentsRepository extends ReactiveMongoRepository<Shipment, String> {
}
