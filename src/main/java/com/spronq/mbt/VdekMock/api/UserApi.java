package com.spronq.mbt.VdekMock.api;

import com.spronq.mbt.VdekMock.model.ExtendedShipment;
import com.spronq.mbt.VdekMock.model.User;
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
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserApi {

    private UserRepository repository;

    @Autowired
    public UserApi(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<User>> getUserById(@PathVariable(value = "id") String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Flux<User> getUsersByEmail(@RequestParam(value = "email") String email) {
        return repository.findAllByEmail(email);
    }

    @GetMapping
    public Flux<User> getUsersByCustomerNumber(@RequestParam(value = "customerNumber") String customerNumber) {
        return repository.findAllByCustomerNumber(customerNumber);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public Mono<User> createUser(@Valid @RequestBody User user) {
        return repository.save(user);
    }



}
