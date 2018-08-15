package com.spronq.mbt.VdekMock.api;

import com.spronq.mbt.VdekMock.model.User;
import com.spronq.mbt.VdekMock.model.UserClaim;
import com.spronq.mbt.VdekMock.repository.UserClaimsRepository;
import com.spronq.mbt.VdekMock.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/userclaims", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserClaimsApi {

    private UserClaimsRepository repository;

    @Autowired
    public UserClaimsApi(UserClaimsRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserClaim>> getUserClaimById(@PathVariable(value = "id") String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping(params = "userId")
    public Flux<UserClaim> getUserClaimsByUserId(@RequestParam(value = "userId") String userId) {
        return repository.findAllByUserId(userId);
    }

    @GetMapping(params = "claimType")
    public Flux<UserClaim> getUserClaimsByClaimType(@RequestParam(value = "claimVType") String claimType) {
        return repository.findAllByClaimType(claimType);
    }

    @GetMapping(params = "claimValue")
    public Flux<UserClaim> getUserClaimsByClaimValue(@RequestParam(value = "claimValue") String claimValue) {
        return repository.findAllByClaimValue(claimValue);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public Mono<UserClaim> createUserClaim(@Valid @RequestBody UserClaim userClaim) {
        return repository.save(userClaim);
    }


}
