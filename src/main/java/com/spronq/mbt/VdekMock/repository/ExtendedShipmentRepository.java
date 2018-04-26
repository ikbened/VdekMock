package com.spronq.mbt.VdekMock.repository;

import com.spronq.mbt.VdekMock.model.ExtendedShipment;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExtendedShipmentRepository extends ReactiveMongoRepository<ExtendedShipment, String> {
}
