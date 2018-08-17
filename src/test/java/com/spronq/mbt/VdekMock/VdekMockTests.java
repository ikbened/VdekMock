package com.spronq.mbt.VdekMock;

import com.spronq.mbt.VdekMock.model.User;
import com.spronq.mbt.VdekMock.model.UserClaim;
import com.spronq.mbt.VdekMock.repository.ExtendedShipmentRepository;
import io.restassured.RestAssured;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.isEmptyOrNullString;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class VdekMockTests {

    @Autowired
    ExtendedShipmentRepository shipmentRepository;

    private JSONObject extShipment;

    @Before
    public void initPath() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Before
    public void initShipment() {
        String testId = newTestId();

        try {
            extShipment = new JSONObject();
            extShipment
                    .put("customerNumber", testId)
                    .put("ean", "9789034506801")
                    .put("orderId", java.util.UUID.randomUUID())
                    .put("orderLine", "1")
                    .put("schoolId", "1641")
                    .put("sessionId", "")
                    .put("emailAddress", "cust" + testId + "@mailinator.com")
                    .put("label", "VDE")
                    .put("postalCode", "2323ab")
                    .put("firstName", "Bokito")
                    .put("middleName", "de")
                    .put("lastName", "Aap")
                    .put("groupName", "")
                    .put("administration", "Dynamics")
                    .put("address", "SomeStreet")
                    .put("addressNumber", "1")
                    .put("addressAdjunct", "")
                    .put("city", "SomeCity")
                    .put("country", "SomeCountry")
                    .put("gender", "M")
                    .put("birthDate", "2004-02-01")
                    .put("amount", "1")
                    .put("startDate", "2018-04-27")
                    .put("displayName", "SomeDisplayName")
                    .put("emailUser", "user" + testId + "@mailinator.com");

        } catch (JSONException e) {
            //some exception handler code.
        }

    }


    private String newTestId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyymmddhhmmssSSS");
        String d = sdf.format(new Date());

        return d;
    }

    @Test
    public void customerNumberIsNotUnique() {
        String custNumber = "";

        try {
            custNumber = extShipment.get("customerNumber").toString();
        } catch (JSONException e) {
            //Catch something
        }

        //Create two users with same customerNumber.
        String[] emails = {"aap", "noot"};
        for (String email: emails){
            User u = new User();
            u.setEmail(email + newTestId() + "@mailinator.com");
            u.setLabel("LearnId");

            given()
                    .log().everything()
                    .contentType("application/json")
                    .body(u)
                    .when()
                    .post("/users")
                    .then()
                    .statusCode(202);

            UserClaim uc = new UserClaim();
            uc.setUserId(u.getId());
            uc.setClaimType("CustomerNumber");
            uc.setClaimValue(custNumber);

            given()
                    .log().everything()
                    .contentType("application/json")
                    .body(uc)
                    .when()
                    .post("/userclaims")
                    .then()
                    .assertThat()
                    .statusCode(202);
        }

        given()
                .log().everything()
                .contentType("application/json")
                .body(extShipment.toString())
                .when()
                .post("/shipments")
                .then()
                .assertThat()
                .log().body()
                .statusCode(202)
                .body("errorMessage", equalTo("CustomerNumber is not unique."));

    }

    @Test
    public void customerEmailIsNotUnique() {
        String email = "";
        try {
            email = extShipment.get("emailAddress").toString();
        } catch (JSONException e) {
            //some exception handler code.
        }

        for (int i=0; i<2; i++) {
            User u = new User();
            u.setEmail(email);
            u.setLabel("LearnId");

            given()
                    .log().everything()
                    .contentType("application/json")
                    .body(u)
                    .when()
                    .post("/users")
                    .then()
                    .log().body()
                    .statusCode(202);
        }

        given()
                .log().everything()
                .contentType("application/json")
                .body(extShipment.toString())
                .when()
                .post("/shipments")
                .then()
                .log().body()
                .statusCode(202)
                .body("errorMessage", equalTo("Customer email is not unique within LearnId"));
    }


    @Test
    public void customerEmailIsUniqueWithinLearnId() {
        String email = "";
        try {
            email = extShipment.get("emailAddress").toString();
        } catch (JSONException e) {
            //some exception handler code.
        }

        String[] labels = {"LearnId", "VDE"};
        for (String label: labels) {
            User u = new User();
            u.setEmail(email);
            u.setLabel(label);

            given()
                    .log().everything()
                    .contentType("application/json")
                    .body(u)
                    .when()
                    .post("/users")
                    .then()
                    .log().body()
                    .statusCode(202);
        }

        given()
                .log().everything()
                .contentType("application/json")
                .body(extShipment.toString())
                .when()
                .post("/shipments")
                .then()
                .log().body()
                .statusCode(202)
                .body("errorMessage", isEmptyOrNullString());
    }



    @Test
    public void customerWithoutEmailWithZeroEmailOccurrences() {
        String custEmail = "";
        String custNumber = "";

        try {
            extShipment.remove("emailAddress");
            custNumber = extShipment.get("customerNumber").toString();
            custEmail = custNumber + "@thelearningnetwork.nl";
        } catch (JSONException e) {
            //some exception handler code.
        }

        given()
                .log().everything()
                .contentType("application/json")
                .body(extShipment.toString())
                .when()
                .post("/shipments")
                .then()
                .log().body()
                .statusCode(202)
                .body("errorMessage", isEmptyOrNullString())
                .body("processedByTask", equalTo(true))
                .body("customerNumber", equalTo(custNumber))
                .body("emailAddress", equalTo(custEmail));

        given()
                .log().all()
                .queryParam("email", custEmail)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .log().body()
                .body("[0].email", equalTo(custEmail))
                .body("[0].label", equalTo("LearnId"));
    }


    @Test
    public void customerWithoutEmailWithOneEmailOccurrences() {
        String custEmail;
        String custNumber = "";

        try {
            extShipment.remove("emailAddress");
            custNumber = extShipment.get("customerNumber").toString();
        } catch (JSONException e) {
            //some exception handler code.
        }

        custEmail = custNumber + "@thelearningnetwork.nl";
        User u = new User();
        u.setEmail(custEmail);
        u.setLabel("LearnId");

        given()
                .log().everything()
                .contentType("application/json")
                .body(u)
                .when()
                .post("/users")
                .then()
                .statusCode(202);

        given()
                .log().everything()
                .contentType("application/json")
                .body(extShipment.toString())
                .when()
                .post("/shipments")
                .then()
                .log().body()
                .statusCode(202)
                .body("errorMessage", isEmptyOrNullString())
                .body("processedByTask", equalTo(true))
                .body("customerNumber", equalTo(custNumber))
                .body("emailAddress", equalTo(custEmail));

        given()
                .log().all()
                .queryParam("email", custEmail)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .log().body()
                .body("[0].email", equalTo(custEmail))
                .body("[0].label", equalTo("LearnId"));
    }


    @Test
    public void customerWithoutEmailWithTwoEmailOccurrences() {
        String custEmail;
        String custNumber = "";

        try {
            extShipment.remove("emailAddress");
            custNumber = extShipment.get("customerNumber").toString();
        } catch (JSONException e) {
            //some exception handler code.
        }

        custEmail = custNumber + "@thelearningnetwork.nl";

        for (int i=0; i<2; i++) {
            User u = new User();
            u.setEmail(custEmail);
            u.setLabel("LearnId");

            given()
                    .log().everything()
                    .contentType("application/json")
                    .body(u)
                    .when()
                    .post("/users")
                    .then()
                    .statusCode(202);
        }

        given()
                .log().everything()
                .contentType("application/json")
                .body(extShipment.toString())
                .when()
                .post("/shipments")
                .then()
                .log().body()
                .statusCode(202)
                .body("errorMessage", equalTo("Customer email is not unique within LearnId"))
                .body("processedByTask", equalTo(false))
                .body("customerNumber", equalTo(custNumber))
                .body("emailAddress", isEmptyOrNullString());
    }


    @Test
    public void customerWithEmailWithZeroEmailOccurrences() {
        String custEmail = "";
        String custNumber = "";

        try {
            custEmail = extShipment.get("emailAddress").toString();
            custNumber = extShipment.get("customerNumber").toString();
        } catch (JSONException e) {
            //some exception handler code.
        }

        given()
                .log().everything()
                .contentType("application/json")
                .body(extShipment.toString())
                .when()
                .post("/shipments")
                .then()
                .assertThat()
                .statusCode(202)
                .body("errorMessage", equalTo(null))
                .body("processedByTask", equalTo(true))
                .log().body();

        String userId = given()
                .log().all()
                .queryParam("email", custEmail)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .log().body()
                .body("[0].email", equalTo(custEmail))
                .body("[0].label", equalTo("LearnId"))
                .extract().jsonPath().getString("[0].id");

        given()
                .log().everything()
                .queryParam("userId", userId)
                .when()
                .get("/userclaims")
                .then()
                .log().body()
                .assertThat()
                .statusCode(200)
                .body("$.", hasSize(1))
                .body("[0].claimType", equalTo("CustomerNumber"))
                .body("[0].claimValue", equalTo(custNumber));
    }

    @Test
    public void customerWithEmailWithOneEmailOccurrences() {
        String custNumber = "";
        String custEmail = "";
        String postalCode = "";

        try {
            custNumber = extShipment.get("customerNumber").toString();
            custEmail = extShipment.get("emailAddress").toString();
            postalCode = extShipment.get("postalCode").toString();
        } catch (JSONException e) {
            //some exception handler code.
        }

        //Create the customer
        User u = new User();
        u.setEmail(custEmail);
        u.setLabel("LearnId");
        u.setPostalCode("1111AA");

        given()
                .log().everything()
                .contentType("application/json")
                .body(u)
                .when()
                .post("/users")
                .then()
                .statusCode(202);

        //Send shipment to VDEK
        given()
                .log().everything()
                .contentType("application/json")
                .body(extShipment.toString())
                .when()
                .post("/shipments")
                .then()
                .assertThat()
                .statusCode(202)
                .body("errorMessage", equalTo(null))
                .body("processedByTask", equalTo(true))
                .log().body();

        //Check the customer, postalcode must be updated and has CustomerNumber userClaim.
        given()
                .log().all()
                .queryParam("email", custEmail)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .log().body()
                .body("$.", hasSize(1))
                .body("[0].email", equalTo(custEmail))
                .body("[0].label", equalTo("LearnId"))
                .body("[0].postalCode", equalTo(postalCode));

        given()
                .log().everything()
                .queryParam("userId", u.getId())
                .when()
                .get("/userclaims")
                .then()
                .log().body()
                .assertThat()
                .statusCode(200)
                .body("$.", hasSize(1))
                .body("[0].claimType", equalTo("CustomerNumber"))
                .body("[0].claimValue", equalTo(custNumber));
    }

    @Test
    public void customerWithEmailWithTwoEmailOccurrences() {
        String custEmail = "";
        ArrayList<String> userIds = new ArrayList<>();

        try {
            custEmail = extShipment.get("emailAddress").toString();
        } catch (JSONException e) {
            //some exception handler code.
        }

        //Lets create two users with identical email
        for (int i=0; i<2; i++) {
            User u = new User();
            u.setEmail(custEmail);
            u.setLabel("LearnId");
            userIds.add(u.getId());

            given()
                    .log().everything()
                    .contentType("application/json")
                    .body(u)
                    .when()
                    .post("/users")
                    .then()
                    .statusCode(202);
        }

        //Send shipment to VDEK
        given()
                .log().everything()
                .contentType("application/json")
                .body(extShipment.toString())
                .when()
                .post("/shipments")
                .then()
                .assertThat()
                .statusCode(202)
                .body("errorMessage", equalTo("Customer email is not unique within LearnId"))
                .body("processedByTask", equalTo(false))
                .log().body();

        //All created users must remain unchanged (user and userclaims)
        for (int i=0; i<2; i++){
            given()
                    .log().everything()
                    .pathParam("userId", userIds.get(i))
                    .when()
                    .get("/users/{userId}")
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .log().body()
                    .body("email", equalTo(custEmail))
                    .body("label", equalTo("LearnId"));

            given()
                    .log().everything()
                    .queryParam("userId", userIds.get(i))
                    .when()
                    .get("/userclaims")
                    .then()
                    .log().body()
                    .assertThat()
                    .statusCode(200)
                    .body("$.", hasSize(0));
        }
    }

    @Test
    public void CustomerAlreadyHasCustomerNumber() {
        String custEmail = "";
        String custNumber = "";

        try {
            custEmail = extShipment.get("emailAddress").toString();
            custNumber = extShipment.get("customerNumber").toString();
        } catch (JSONException e) {
            //some exception handler code.
        }

        //Lets create one user with an customernumber
        User u = new User();
        u.setEmail(custEmail);
        u.setLabel("LearnId");

        given()
                .log().everything()
                .contentType("application/json")
                .body(u)
                .when()
                .post("/users")
                .then()
                .statusCode(202);

        UserClaim uc = new UserClaim();
        uc.setUserId(u.getId());
        uc.setClaimType("CustomerNumber");
        uc.setClaimValue(custNumber);

        given()
                .log().everything()
                .contentType("application/json")
                .body(uc)
                .when()
                .post("/userclaims")
                .then()
                .assertThat()
                .statusCode(202);

        //Send shipment to VDEK
        given()
                .log().everything()
                .contentType("application/json")
                .body(extShipment.toString())
                .when()
                .post("/shipments")
                .then()
                .assertThat()
                .statusCode(202)
                .body("errorMessage", isEmptyOrNullString())
                .body("processedByTask", equalTo(true))
                .log().body();

        //The created user and userclaim must remain unchanged
        given()
                .log().everything()
                .pathParam("userId", u.getId())
                .when()
                .get("/users/{userId}")
                .then()
                .assertThat()
                .statusCode(200)
                .log().body()
                .body("email", equalTo(custEmail))
                .body("label", equalTo("LearnId"));

        given()
                .log().everything()
                .queryParam("userId", u.getId())
                .when()
                .get("/userclaims")
                .then()
                .log().body()
                .assertThat()
                .statusCode(200)
                .body("$.", hasSize(1))
                .body("[0].claimType", equalToIgnoringCase("CustomerNumber"))
                .body("[0].claimValue", equalToIgnoringCase(custNumber));

        //Ensure that no other user got the same customer number userclaim.
        given()
                .log().everything()
                .queryParam("claimValue", custNumber)
                .when()
                .get("/userclaims")
                .then()
                .log().body()
                .assertThat()
                .statusCode(200)
                .body("$.", hasSize(1));
    }


    @Test
    public void CustomerAlreadyHasCustomerNumberInAccountSet() {
        String custEmail = "";
        String custNumber = "";
        String postalCode = "";

        try {
            custEmail = extShipment.get("emailAddress").toString();
            custNumber = extShipment.get("customerNumber").toString();
            postalCode = extShipment.get("postalCode").toString();
        } catch (JSONException e) {
            //some exception handler code.
        }

        //First user has the customernumber
        User u1 = new User();
        u1.setEmail("WithCn" + custEmail);
        u1.setLabel("LearnId");

        given()
                .log().everything()
                .contentType("application/json")
                .body(u1)
                .when()
                .post("/users")
                .then()
                .statusCode(202);

        UserClaim uc = new UserClaim();
        uc.setUserId(u1.getId());
        uc.setClaimType("CustomerNumber");
        uc.setClaimValue(custNumber);

        given()
                .log().everything()
                .contentType("application/json")
                .body(uc)
                .when()
                .post("/userclaims")
                .then()
                .assertThat()
                .statusCode(202);

        //The second user .....
        User u2 = new User();
        u2.setEmail(custEmail);
        u2.setLabel("LearnId");

        given()
                .log().everything()
                .contentType("application/json")
                .body(u2)
                .when()
                .post("/users")
                .then()
                .statusCode(202);

        //Now link both users together
        uc = new UserClaim();
        uc.setUserId(u1.getId());
        uc.setClaimType("AccountSet");
        uc.setClaimValue(u2.getId());

        given()
                .log().everything()
                .contentType("application/json")
                .body(uc)
                .when()
                .post("/userclaims")
                .then()
                .assertThat()
                .statusCode(202);

        uc = new UserClaim();
        uc.setUserId(u2.getId());
        uc.setClaimType("AccountSet");
        uc.setClaimValue(u1.getId());

        given()
                .log().everything()
                .contentType("application/json")
                .body(uc)
                .when()
                .post("/userclaims")
                .then()
                .assertThat()
                .statusCode(202);

        //Send shipment to VDEK
        given()
                .log().everything()
                .contentType("application/json")
                .body(extShipment.toString())
                .when()
                .post("/shipments")
                .then()
                .assertThat()
                .statusCode(202)
                .body("errorMessage", isEmptyOrNullString())
                .body("processedByTask", equalTo(true))
                .log().body();

        //The first user + userclaim must remain unchanged
        given()
                .log().everything()
                .pathParam("userId", u1.getId())
                .when()
                .get("/users/{userId}")
                .then()
                .assertThat()
                .statusCode(200)
                .log().body()
                .body("email", equalTo("WithCn" + custEmail))
                .body("label", equalTo("LearnId"))
                .body("$", not(hasKey("postalCode")));

        System.out.println("First user - userclaims");
        given()
                .log().everything()
                .queryParam("userId", u1.getId())
                .when()
                .get("/userclaims")
                .then()
                .log().body()
                .assertThat()
                .statusCode(200)
                .body("$.", hasSize(2))
                .body("[0].claimType", equalToIgnoringCase("CustomerNumber"))
                .body("[0].claimValue", equalToIgnoringCase(custNumber))
                .body("[1].claimType", equalToIgnoringCase("AccountSet"))
                .body("[1].claimValue", equalToIgnoringCase(u2.getId()));

        //The second user is also unchanged, except for de postal code
        given()
                .log().everything()
                .pathParam("userId", u2.getId())
                .when()
                .get("/users/{userId}")
                .then()
                .assertThat()
                .statusCode(200)
                .log().body()
                .body("email", equalTo(custEmail))
                .body("label", equalTo("LearnId"))
                .body("postalCode", equalTo(postalCode));

        System.out.println("Second user - userclaims");
        given()
                .log().everything()
                .queryParam("userId", u2.getId())
                .when()
                .get("/userclaims")
                .then()
                .log().body()
                .assertThat()
                .statusCode(200)
                .body("$.", hasSize(1))
                .body("[0].claimType", equalToIgnoringCase("AccountSet"))
                .body("[0].claimValue", equalTo(u1.getId()));
    }


    @Test
    public void CustomerNumberNotInAccountSetAndUnknown() {
        String custEmail = "";
        String custNumber = "";
        String postalCode = "";

        try {
            custEmail = extShipment.get("emailAddress").toString();
            custNumber = extShipment.get("customerNumber").toString();
            postalCode = extShipment.get("postalCode").toString();
        } catch (JSONException e) {
            //some exception handler code.
        }

        //First user
        User u1 = new User();
        u1.setEmail("First" + custEmail);
        u1.setLabel("LearnId");

        given()
                .log().everything()
                .contentType("application/json")
                .body(u1)
                .when()
                .post("/users")
                .then()
                .statusCode(202);

        //The second user .....
        User u2 = new User();
        u2.setEmail(custEmail);
        u2.setLabel("LearnId");

        given()
                .log().everything()
                .contentType("application/json")
                .body(u2)
                .when()
                .post("/users")
                .then()
                .statusCode(202);

        //Now link both users
        UserClaim uc = new UserClaim();
        uc.setUserId(u1.getId());
        uc.setClaimType("AccountSet");
        uc.setClaimValue(u2.getId());

        given()
                .log().everything()
                .contentType("application/json")
                .body(uc)
                .when()
                .post("/userclaims")
                .then()
                .assertThat()
                .statusCode(202);

        uc = new UserClaim();
        uc.setUserId(u2.getId());
        uc.setClaimType("AccountSet");
        uc.setClaimValue(u1.getId());

        given()
                .log().everything()
                .contentType("application/json")
                .body(uc)
                .when()
                .post("/userclaims")
                .then()
                .assertThat()
                .statusCode(202);

        //Send shipment to VDEK
        given()
                .log().everything()
                .contentType("application/json")
                .body(extShipment.toString())
                .when()
                .post("/shipments")
                .then()
                .assertThat()
                .statusCode(202)
                .body("errorMessage", isEmptyOrNullString())
                .body("processedByTask", equalTo(true))
                .log().body();

        //The first user + userclaim must remain unchanged
        given()
                .log().everything()
                .pathParam("userId", u1.getId())
                .when()
                .get("/users/{userId}")
                .then()
                .assertThat()
                .statusCode(200)
                .log().body()
                .body("email", equalTo("First" + custEmail))
                .body("label", equalTo("LearnId"))
                .body("$", not(hasKey("postalCode")));

        System.out.println("First user - userclaims");
        given()
                .log().everything()
                .queryParam("userId", u1.getId())
                .when()
                .get("/userclaims")
                .then()
                .log().body()
                .assertThat()
                .statusCode(200)
                .body("$.", hasSize(1))
                .body("[0].claimType", equalToIgnoringCase("AccountSet"))
                .body("[0].claimValue", equalToIgnoringCase(u2.getId()));

        //The second user got the postalcode and the customernumber userclaim
        given()
                .log().everything()
                .pathParam("userId", u2.getId())
                .when()
                .get("/users/{userId}")
                .then()
                .assertThat()
                .statusCode(200)
                .log().body()
                .body("email", equalTo(custEmail))
                .body("label", equalTo("LearnId"))
                .body("postalCode", equalTo(postalCode));

        System.out.println("Second user - userclaims");
        given()
                .log().everything()
                .queryParam("userId", u2.getId())
                .when()
                .get("/userclaims")
                .then()
                .log().body()
                .assertThat()
                .statusCode(200)
                .body("$.", hasSize(2))
                .body("[0].claimType", equalToIgnoringCase("AccountSet"))
                .body("[0].claimValue", equalTo(u1.getId()))
                .body("[1].claimType", equalToIgnoringCase("CustomerNumber"))
                .body("[1].claimValue", equalTo(custNumber));
    }

    @Test
    public void CustomerNumberNotInAccountSetButKnown() {
        String custEmail = "";
        String custNumber = "";
        String postalCode = "";

        try {
            custEmail = extShipment.get("emailAddress").toString();
            custNumber = extShipment.get("customerNumber").toString();
            postalCode = extShipment.get("postalCode").toString();
        } catch (JSONException e) {
            //some exception handler code.
        }

        //First user
        User u1 = new User();
        u1.setEmail("First" + custEmail);
        u1.setLabel("LearnId");

        given()
                .log().everything()
                .contentType("application/json")
                .body(u1)
                .when()
                .post("/users")
                .then()
                .statusCode(202);

        //The second user .....
        User u2 = new User();
        u2.setEmail(custEmail);
        u2.setLabel("LearnId");

        given()
                .log().everything()
                .contentType("application/json")
                .body(u2)
                .when()
                .post("/users")
                .then()
                .statusCode(202);

        //The third user .....
        User u3 = new User();
        u3.setEmail("third" + custEmail);
        u3.setLabel("LearnId");

        given()
                .log().everything()
                .contentType("application/json")
                .body(u3)
                .when()
                .post("/users")
                .then()
                .statusCode(202);

        //Now link first and second users
        UserClaim uc = new UserClaim();
        uc.setUserId(u1.getId());
        uc.setClaimType("AccountSet");
        uc.setClaimValue(u2.getId());

        given()
                .log().everything()
                .contentType("application/json")
                .body(uc)
                .when()
                .post("/userclaims")
                .then()
                .assertThat()
                .statusCode(202);

        uc = new UserClaim();
        uc.setUserId(u2.getId());
        uc.setClaimType("AccountSet");
        uc.setClaimValue(u1.getId());

        given()
                .log().everything()
                .contentType("application/json")
                .body(uc)
                .when()
                .post("/userclaims")
                .then()
                .assertThat()
                .statusCode(202);

        //Add customerNumber to third user
        uc = new UserClaim();
        uc.setUserId(u3.getId());
        uc.setClaimType("CustomerNumber");
        uc.setClaimValue(custNumber);

        given()
                .log().everything()
                .contentType("application/json")
                .body(uc)
                .when()
                .post("/userclaims")
                .then()
                .assertThat()
                .statusCode(202);

        //Send shipment to VDEK
        given()
                .log().everything()
                .contentType("application/json")
                .body(extShipment.toString())
                .when()
                .post("/shipments")
                .then()
                .assertThat()
                .statusCode(202)
                .body("errorMessage", isEmptyOrNullString())
                .body("processedByTask", equalTo(true))
                .log().body();

        //The first user + userclaim must remain unchanged
        given()
                .log().everything()
                .pathParam("userId", u1.getId())
                .when()
                .get("/users/{userId}")
                .then()
                .assertThat()
                .statusCode(200)
                .log().body()
                .body("email", equalTo("First" + custEmail))
                .body("label", equalTo("LearnId"))
                .body("$", not(hasKey("postalCode")));

        System.out.println("First user - userclaims");
        given()
                .log().everything()
                .queryParam("userId", u1.getId())
                .when()
                .get("/userclaims")
                .then()
                .log().body()
                .assertThat()
                .statusCode(200)
                .body("$.", hasSize(1))
                .body("[0].claimType", equalToIgnoringCase("AccountSet"))
                .body("[0].claimValue", equalToIgnoringCase(u2.getId()));

        //The second user got the postalcode and a link to the third user
        given()
                .log().everything()
                .pathParam("userId", u2.getId())
                .when()
                .get("/users/{userId}")
                .then()
                .assertThat()
                .statusCode(200)
                .log().body()
                .body("email", equalTo(custEmail))
                .body("label", equalTo("LearnId"))
                .body("postalCode", equalTo(postalCode));

        System.out.println("Second user - userclaims");
        given()
                .log().everything()
                .queryParam("userId", u2.getId())
                .when()
                .get("/userclaims")
                .then()
                .log().body()
                .assertThat()
                .statusCode(200)
                .body("$.", hasSize(2))
                .body("[0].claimType", equalToIgnoringCase("AccountSet"))
                .body("[0].claimValue", equalTo(u1.getId()))
                .body("[1].claimType", equalToIgnoringCase("AccountSet"))
                .body("[1].claimValue", equalTo(u3.getId()));

        //The third user is now linked to the second user
        given()
                .log().everything()
                .pathParam("userId", u3.getId())
                .when()
                .get("/users/{userId}")
                .then()
                .assertThat()
                .statusCode(200)
                .log().body()
                .body("email", equalTo("third" + custEmail))
                .body("label", equalTo("LearnId"))
                .body("$", not(hasKey("postalCode")));

        given()
                .log().everything()
                .queryParam("userId", u3.getId())
                .when()
                .get("/userclaims")
                .then()
                .log().body()
                .assertThat()
                .statusCode(200)
                .body("$.", hasSize(2))
                .body("[0].claimType", equalToIgnoringCase("CustomerNumber"))
                .body("[0].claimValue", equalTo(custNumber))
                .body("[1].claimType", equalToIgnoringCase("AccountSet"))
                .body("[1].claimValue", equalTo(u2.getId()));
    }

    @Test
    public void adminIsNotDynamics() {

        try {
            extShipment.remove("administration");
            extShipment.put("administration", "Baan");
        } catch (JSONException e) {
            //some exception handler code.
        }

        given()
                .log().everything()
                .contentType("application/json")
                .body(extShipment.toString())
                .when()
                .post("/shipments")
                .then()
                .assertThat()
                .statusCode(202)
                .body("errorMessage", equalTo("Unknown administration"))
                .body("processedByTask", equalTo(false))
                .log().body();

    }

    @Test
    public void newCustomerIsAlsoUserWithoutEmail() {
        String email = "";
        String custNumber = "";
        String postalCode = "";

        try {
            email = extShipment.get("emailAddress").toString();
            custNumber = extShipment.get("customerNumber").toString();
            postalCode = extShipment.get("postalCode").toString();
            extShipment.remove("emailUser");
        } catch (JSONException e) {
            //some exception handler code.
        }

        //Post the shipment
        given()
                .log().everything()
                .contentType("application/json")
                .body(extShipment.toString())
                .when()
                .post("/shipments")
                .then()
                .assertThat()
                .statusCode(202)
                .body("errorMessage", equalTo(null))
                .body("processedByTask", equalTo(true))
                .body("emailUser", equalTo(email))
                .log().body();

        String userId = given()
                .log().all()
                .queryParam("email", email)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .log().body()
                .body("$", hasSize(1))
                .body("[0].email", equalTo(email))
                .body("[0].label", equalTo("LearnId"))
                .body("[0].postalCode", equalTo(postalCode))
                .extract().jsonPath().getString("[0].id");

        given()
                .log().everything()
                .queryParam("userId", userId)
                .when()
                .get("/userclaims")
                .then()
                .log().body()
                .assertThat()
                .statusCode(200)
                .body("$.", hasSize(1))
                .body("[0].claimType", equalToIgnoringCase("CustomerNumber"))
                .body("[0].claimValue", equalTo(custNumber));
    }


    @Test
    public void userEmailIsNotUnique() {
        String email = "";
        try {
            email = extShipment.get("emailUser").toString();
        } catch (JSONException e) {
            //some exception handler code.
        }

        //Create two users with same email
        for(int i=0; i< 2; i++) {
            User u = new User();
            u.setEmail(email);
            u.setLabel("LearnId");

            given()
                    .log().everything()
                    .contentType("application/json")
                    .body(u)
                    .when()
                    .post("/users")
                    .then()
                    .statusCode(202);
        }

        //Send shipment to VDEK
        given()
                .log().everything()
                .contentType("application/json")
                .body(extShipment.toString())
                .when()
                .post("/shipments")
                .then()
                .assertThat()
                .statusCode(202)
                .body("errorMessage", equalTo("User email is not unique within LearnId"));
    }


    @Test
    public void existingUserEmail() {
        String emailUser = "";
        String emailCust = "";
        String postalCode = "";
        String custNumber = "";

        try {
            emailUser = extShipment.get("emailUser").toString();
            emailCust = extShipment.get("emailAddress").toString();
            postalCode = extShipment.get("postalCode").toString();
            custNumber = extShipment.get("customerNumber").toString();
        } catch (JSONException e) {
            //some exception handler code.
        }

        //Create the user
        User user1 = new User();
        user1.setEmail(emailUser);
        user1.setLabel("LearnId");

        given()
                .log().everything()
                .contentType("application/json")
                .body(user1)
                .when()
                .post("/users")
                .then()
                .statusCode(202);

        //Send shipment to VDEK
        given()
                .log().everything()
                .contentType("application/json")
                .body(extShipment.toString())
                .when()
                .post("/shipments")
                .then()
                .assertThat()
                .statusCode(202)
                .body("errorMessage", equalTo(null))
                .body("processedByTask", equalTo(true))
                .log().body();

        //Check the customer first
        String userId = given()
                .log().all()
                .queryParam("email", emailCust)
                .when()
                .get("/users")
                .then()
                .log().body()
                .statusCode(200)
                .body("$.", hasSize(1))
                .body("[0].email", equalTo(emailCust))
                .body("[0].label", equalTo("LearnId"))
                .body("[0].postalCode", equalTo(postalCode))
                .extract().jsonPath().getString("[0].id");

        given()
                .log().everything()
                .queryParam("userId", userId)
                .when()
                .get("/userclaims")
                .then()
                .log().body()
                .assertThat()
                .statusCode(200)
                .body("$.", hasSize(1))
                .body("[0].claimType", equalToIgnoringCase("CustomerNumber"))
                .body("[0].claimValue", equalTo(custNumber));

        //Now check the user
        userId = given()
                .log().all()
                .queryParam("email", emailUser)
                .when()
                .get("/users")
                .then()
                .log().body()
                .statusCode(200)
                .body("$.", hasSize(1))
                .body("[0].email", equalTo(emailUser))
                .body("[0].label", equalTo("LearnId"))
                .body("$", not(hasKey("postalCode")))
                .extract().jsonPath().getString("[0].id");

        given()
                .log().everything()
                .queryParam("userId", userId)
                .when()
                .get("/userclaims")
                .then()
                .log().body()
                .assertThat()
                .statusCode(200)
                .body("$.", hasSize(0));

    }

    @Test
    public void newUserEmail() {
        String emailUser = "";
        String emailCust = "";
        String postalCode = "";
        String custNumber = "";

        try {
            emailUser = extShipment.get("emailUser").toString();
            emailCust = extShipment.get("emailAddress").toString();
            postalCode = extShipment.get("postalCode").toString();
            custNumber = extShipment.get("customerNumber").toString();
        } catch (JSONException e) {
            //some exception handler code.
        }

        //Send shipment to VDEK
        given()
                .log().everything()
                .contentType("application/json")
                .body(extShipment.toString())
                .when()
                .post("/shipments")
                .then()
                .assertThat()
                .statusCode(202)
                .body("errorMessage", equalTo(null))
                .body("processedByTask", equalTo(true))
                .log().body();

        //Check the customer first
        String userId = given()
                .log().all()
                .queryParam("email", emailCust)
                .when()
                .get("/users")
                .then()
                .log().body()
                .statusCode(200)
                .body("$.", hasSize(1))
                .body("[0].email", equalTo(emailCust))
                .body("[0].label", equalTo("LearnId"))
                .body("[0].postalCode", equalTo(postalCode))
                .extract().jsonPath().getString("[0].id");

        given()
                .log().everything()
                .queryParam("userId", userId)
                .when()
                .get("/userclaims")
                .then()
                .log().body()
                .assertThat()
                .statusCode(200)
                .body("$.", hasSize(1))
                .body("[0].claimType", equalToIgnoringCase("CustomerNumber"))
                .body("[0].claimValue", equalTo(custNumber));

        //Now check the user
        userId = given()
                .log().all()
                .queryParam("email", emailUser)
                .when()
                .get("/users")
                .then()
                .log().body()
                .statusCode(200)
                .body("$.", hasSize(1))
                .body("[0].email", equalTo(emailUser))
                .body("[0].label", equalTo("LearnId"))
                .body("$", not(hasKey("postalCode")))
                .extract().jsonPath().getString("[0].id");

        given()
                .log().everything()
                .queryParam("userId", userId)
                .when()
                .get("/userclaims")
                .then()
                .log().body()
                .assertThat()
                .statusCode(200)
                .body("$.", hasSize(0));
    }

}
