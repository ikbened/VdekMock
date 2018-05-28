package com.spronq.mbt.VdekMock.api;

import com.spronq.mbt.VdekMock.model.User;
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
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UsersApi {

    private UsersRepository repository;

    @Autowired
    public UsersApi(UsersRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<User>> getUserById(@PathVariable(value = "id") String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping(params = "email")
    public Flux<User> getUsersByEmail(@RequestParam(value = "email") String email) {
        return repository.findAllByEmail(email);
    }

    @GetMapping(params = "customerNumber")
    public Flux<User> getUsersByCustomerNumber(@RequestParam(value = "customerNumber") String customerNumber) {
        return repository.findAllByCustomerNumber(customerNumber);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public Mono<User> createUser(@Valid @RequestBody User user) {
        return repository.save(user);
    }



}
