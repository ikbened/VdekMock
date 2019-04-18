package com.spronq.mbt.VdekMock.api;

import com.spronq.mbt.VdekMock.model.User;
import com.spronq.mbt.VdekMock.repository.UsersRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UsersApi {

    private UsersRepository repository;
    private static final Logger LOG = getLogger(UsersApi.class);

    @Autowired
    public UsersApi(UsersRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<User>> getUserById(@PathVariable(value = "id") String id) {
        LOG.debug("getUserById");
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping(params = "email")
    public Flux<User> getUsersByEmail(@RequestParam(value = "email") String email) {
        LOG.debug("getUsersByEmail");
        return repository.findAllByEmail(email);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public Mono<User> createUser(@Valid @RequestBody User user) {
        LOG.debug("createUser");
        return repository.save(user);
    }


}
