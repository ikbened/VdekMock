package com.spronq.mbt.VdekMock.repository;

import com.spronq.mbt.VdekMock.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UsersRepository extends ReactiveMongoRepository<User, String> {

    Flux<User> findAllByEmail(String email);
    //Flux<User> findAllByCustomerNumber(String customerNumber);
    //Flux<User> findAllByAccountSetId(String accountSetId);
    //Mono<Long> countAllByCustomerNumber(String customerNumber);

}
