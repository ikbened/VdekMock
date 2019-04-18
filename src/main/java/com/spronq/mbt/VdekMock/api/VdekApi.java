package com.spronq.mbt.VdekMock.api;

import com.spronq.mbt.VdekMock.model.ExtendedShipment;
import com.spronq.mbt.VdekMock.model.User;
import com.spronq.mbt.VdekMock.model.UserClaim;
import com.spronq.mbt.VdekMock.repository.ExtendedShipmentRepository;
import com.spronq.mbt.VdekMock.repository.UserClaimsRepository;
import com.spronq.mbt.VdekMock.repository.UsersRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static org.slf4j.LoggerFactory.getLogger;
import javax.validation.Valid;
import java.util.ArrayList;

@RestController
@RequestMapping(value = "/shipments", produces = MediaType.APPLICATION_JSON_VALUE)
public class VdekApi {

    private static final Logger LOG = getLogger(VdekApi.class);
    private ExtendedShipmentRepository repository;
    private UsersRepository userRepository;
    private UserClaimsRepository userClaimsRepository;

    @Autowired
    public VdekApi(ExtendedShipmentRepository repository, UsersRepository userRepository, UserClaimsRepository userClaimsRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.userClaimsRepository = userClaimsRepository;
    }

    @GetMapping
    public Flux<ExtendedShipment> getAllShipments() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ExtendedShipment>> getShipmentById(@PathVariable(value = "id") String shipmentId) {
        return repository.findById(shipmentId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public Mono<ExtendedShipment> createShipments(@Valid @RequestBody ExtendedShipment shipment) {

        String errMsg = resolveCustomer(shipment);
        if (StringUtils.isEmpty(errMsg))
            errMsg = resolveUser(shipment);

        if (StringUtils.isEmpty(errMsg)) {
            shipment.setProcessedByTask(true);
        } else {
            shipment.setErrorMessage(errMsg);
            shipment.setProcessedByTask(false);
        }

        return repository.save(shipment);
    }

    private String resolveCustomer(ExtendedShipment shipment) {
        String email;
        User cust;
        String custNumber = shipment.getCustomerNumber();

        LOG.debug("resolveCustomer");
//        LOG.debug("This Will Be Printed On Debug");
//        LOG.info("This Will Be Printed On Info");
//        LOG.warn("This Will Be Printed On Warn");
//        LOG.error("This Will Be Printed On Error");
        //LOG.fatal("This Will Be Printed On Fatal");
        if ( !IsCustomerNumberUnique(custNumber) ) {
            return "CustomerNumber is not unique. ";
        }

        if (StringUtils.isEmpty(shipment.getEmailAddress())) {
            email = custNumber + "@thelearningnetwork.nl";
        } else {
            email = shipment.getEmailAddress();
        }
        LOG.debug("email: " + email);

        if ( !IsEmailUniqueForLearnIdAccounts(email) ) {
            LOG.debug("Customer email is not unique within LearnId...");
            return "Customer email is not unique within LearnId";
        }

        shipment.setEmailAddress(email);
        repository.save(shipment).block();

        if ( IsLearnIdAccountWithEmailPresent(email) ) {
            // This assumes there's only one customer found for this email
            LOG.debug("Customer found");
            cust = userRepository.findAllByEmail(email).blockFirst();
        } else {
            LOG.debug("Creating a new customer");
            cust = new User();
            cust.setEmail(email);
            cust.setLabel("LearnId");
            userRepository.save(cust).block();
        }

        if (! IsCustomerNumberInAccountSet(custNumber, cust.getId())){
            LOG.debug("CustomerNumber not found in AccountSet");
            User user = GetUserWithCustomerNumber(shipment.getCustomerNumber());
            if(user != null){
                LOG.debug("Linking users");
                LinkUsers(user, cust);
            } else {
                LOG.debug("Adding new userclaim");
                AddUserClaimToUser(cust, "CustomerNumber", custNumber);
            }
        }

        cust.setPostalCode(shipment.getPostalCode());
        userRepository.save(cust).block();
        return "";
    }

    private void AddUserClaimToUser(User user, String claimType, String claimValue) {
        UserClaim uc = new UserClaim();
        uc.setUserId(user.getId());
        uc.setClaimType(claimType);
        uc.setClaimValue(claimValue);
        userClaimsRepository.save(uc).block();
    }

    private User GetUserWithCustomerNumber(String customerNumber){
        UserClaim uc = userClaimsRepository.findAllByClaimType("CustomerNumber")
                .filter(c -> c.getClaimValue().equalsIgnoreCase(customerNumber)).blockFirst();

        if (uc == null) {
            return null;
        } else {
            return userRepository.findById(uc.getUserId()).block();
        }

    }

    private void LinkUsers(User u1, User u2) {
        UserClaim uc = new UserClaim();
        uc.setUserId(u1.getId());
        uc.setClaimType("AccountSet");
        uc.setClaimValue(u2.getId());
        userClaimsRepository.save(uc).block();

        uc = new UserClaim();
        uc.setUserId(u2.getId());
        uc.setClaimType("AccountSet");
        uc.setClaimValue(u1.getId());
        userClaimsRepository.save(uc).block();
    }

    private ArrayList<String> GetAllUserIdsInAccountSet(ArrayList<String> userIds, String userId) {
        if (! userIds.contains(userId) ) {
            userIds.add(userId);
        }

        for (UserClaim uc: userClaimsRepository.findAllByUserId(userId).filter(uc -> uc.getClaimType().equalsIgnoreCase("AccountSet")).toIterable()) {
            if (! userIds.contains(uc.getClaimValue()) ) {
                userIds = GetAllUserIdsInAccountSet(userIds, uc.getClaimValue());
            }
        }

        return userIds;
    }

    private boolean IsCustomerNumberInAccountSet(String customerNumber, String userId) {
        ArrayList<String> custNumbers = new ArrayList<>();
        ArrayList<String> userIds = new ArrayList<>();

        userIds = GetAllUserIdsInAccountSet(userIds, userId);

        for (String id: userIds) {
            for (UserClaim uc: userClaimsRepository.findAllByUserId(id).filter(uc -> uc.getClaimType().equalsIgnoreCase("CustomerNumber")).toIterable()) {
                if (! custNumbers.contains(uc.getClaimValue()) ) {
                    custNumbers.add(uc.getClaimValue());
                }
            }

        }

        return custNumbers.contains(customerNumber);
    }


    private Boolean IsLearnIdAccountWithEmailPresent(String email) {
        return CountLearnIdAccountsByEmail(email) == 1;
    }

    private Boolean IsEmailUniqueForLearnIdAccounts(String email) {
        return CountLearnIdAccountsByEmail(email) <= 1;
    }

    private Long CountLearnIdAccountsByEmail(String email) {
        return userRepository.findAllByEmail(email).toStream().filter(u -> u.getLabel().equalsIgnoreCase("LearnId")).count();

        // Directly filter and count flux, and not converting it to a stream.
        // long count = userRepository.findAllByEmail(email).filter(user -> user.getLabel().equalsIgnoreCase("LearnId")).count().block();
    }

    private Boolean IsCustomerNumberUnique(String customerNumber) {
        //Is distinct on UserId necessary?
        return userClaimsRepository.findAllByClaimValue(customerNumber).toStream().filter(u -> u.getClaimType().equalsIgnoreCase("CustomerNumber")).count() <= 1;
    }


    private String resolveUser(ExtendedShipment shipment) {
        LOG.debug("resolveCustomer");
        if (!shipment.getAdministration().equalsIgnoreCase("Dynamics")) {
            LOG.debug("Administration <> Dynamics");
            return  "Unknown administration";
        }

        if (StringUtils.isEmpty(shipment.getEmailUser())) {
            LOG.debug("User has no email");
            shipment.setEmailUser(shipment.getEmailAddress());
            repository.save(shipment).block();
            return "";
        }

        if ( !IsEmailUniqueForLearnIdAccounts(shipment.getEmailUser()) ) {
            LOG.debug("User email is not unique");
            return "User email is not unique within LearnId";
        }

        LOG.debug("user has email " + shipment.getEmailUser());
        if ( !IsLearnIdAccountWithEmailPresent(shipment.getEmailUser()) ) {
            LOG.debug("Creating account for user");
            User user = new User();
            user.setLabel("LearnId");
            user.setEmail(shipment.getEmailUser());
            userRepository.save(user).block();
        } else {
            //Do nothing
            LOG.debug("user found");
        }

        return "";
    }
}
