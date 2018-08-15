package com.spronq.mbt.VdekMock.repository;

import com.spronq.mbt.VdekMock.model.User;
import com.spronq.mbt.VdekMock.model.UserClaim;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserClaimsRepository extends ReactiveMongoRepository<UserClaim, String> {

    Flux<UserClaim> findAllByUserId(String userId);


}
