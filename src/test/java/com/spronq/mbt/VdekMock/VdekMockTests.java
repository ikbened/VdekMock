package com.spronq.mbt.VdekMock;

import com.spronq.mbt.VdekMock.model.User;
import com.spronq.mbt.VdekMock.repository.ExtendedShipmentRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

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

        User user1 = new User();
        user1.setEmail("aap" + newTestId() + "@mailinator.com");
        user1.setLabel("LearnId");
        user1.setCustomerNumber(custNumber);

        given()
                .log().everything()
                .contentType("application/json")
                .body(user1)
                .when()
                .post("/users")
                .then()
                .statusCode(202);

        User user2 = new User();
        user2.setEmail("noot" + newTestId() + "@mailinator.com");
        user2.setLabel("LearnId");
        user2.setCustomerNumber(custNumber);

        given()
                .log().everything()
                .contentType("application/json")
                .body(user2)
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

        User user1 = new User();
        user1.setEmail(email);
        user1.setLabel("LearnId");
        user1.setCustomerNumber(newTestId());

        given()
                .log().everything()
                .contentType("application/json")
                .body(user1)
                .when()
                .post("/users")
                .then()
                .log().body()
                .statusCode(202);

        User user2 = new User();
        user2.setEmail(email);
        user2.setLabel("LearnId");
        user2.setCustomerNumber(newTestId());

        given()
                .log().everything()
                .contentType("application/json")
                .body(user2)
                .when()
                .post("/users")
                .then()
                .log().body()
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
                .body("errorMessage", equalTo("Customer email is not unique within LearnId"));
    }


    @Test
    public void newCustomerWithoutEmail() {
        String custEmail = "";
        String custNumber = "";

        try {
            extShipment.remove("emailAddress");
            custNumber = extShipment.get("customerNumber").toString();
        } catch (JSONException e) {
            //some exception handler code.
        }

        custEmail = given()
                .log().everything()
                .contentType("application/json")
                .body(extShipment.toString())
                .when()
                .post("/shipments")
                .then()
                .log().body()
                //.assertThat()
                .statusCode(202)
                .body("errorMessage", equalTo(null))
                .body("processedByTask", equalTo(true))
                .body("emailAddress", contains(custNumber))
                .body("emailAddress", contains("@thelearningnetwork.nl"))
                .extract().jsonPath().getString("emailAddress");

        given()
                .log().all()
                .queryParam("email", custEmail)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .log().body()
                .body("[0].email", equalTo(custEmail))
                .body("[0].customerNumber", equalTo(custNumber))
                .body("[0].label", equalTo("LearnId"))
                .body("[0].accountSetId", equalTo(null));

    }


    @Test
    public void newCustomerWithEmail() {
        String custEmail = "";
        String userEmail = "";
        String custNumber = "";

        try {
            custEmail = extShipment.get("emailAddress").toString();
            userEmail = extShipment.get("emailUser").toString();
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

        given()
                .log().all()
                .queryParam("email", custEmail)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .log().body()
                .body("[0].email", equalTo(custEmail))
                .body("[0].customerNumber", equalTo(custNumber))
                .body("[0].label", equalTo("LearnId"))
                .body("[0].accountSetId", equalTo(null));

    }

    //existingCustomerWithCustomerNumberInAccountSet
    //existingCustomerWithoutCustmerNumberInAccountSet
    //newCustomerWithCustomerNumberInAccountSet
    //newCustomerWithoutCustmerNumberInAccountSet


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

        try {
            email = extShipment.get("emailAddress").toString();
            custNumber = extShipment.get("customerNumber").toString();
            extShipment.remove("emailUser");
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
                .body("emailUser", equalTo(email))
                .log().body();

        given()
                .log().all()
                .queryParam("email", email)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .log().body()
                .body("[0].email", equalTo(email))
                .body("[0].customerNumber", equalTo(custNumber))
                .body("[0].label", equalTo("LearnId"))
                .body("[0].accountSetId", equalTo(null));

        given()
                .log().all()
                .queryParam("email", email)
                .when()
                .get("/users")
                .then()
                .log().body()
                .statusCode(200)
                .body("$.", hasSize(1));

    }




    @Test
    public void userEmailIsNotUnique() {
        String email = "";
        try {
            email = extShipment.get("emailUser").toString();
        } catch (JSONException e) {
            //some exception handler code.
        }


        User user1 = new User();
        user1.setEmail(email);
        user1.setLabel("LearnId");
        user1.setCustomerNumber("1718");

        given()
                .log().everything()
                .contentType("application/json")
                .body(user1)
                .when()
                .post("/users")
                .then()
                .statusCode(202);

        User user2 = new User();
        user2.setEmail(email);
        user2.setLabel("LearnId");
        user2.setCustomerNumber("1719");

        given()
                .log().everything()
                .contentType("application/json")
                .body(user2)
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
                .assertThat()
                .statusCode(202)
                .body("errorMessage", equalTo("User email is not unique within LearnId"));
    }


    @Test
    public void existingUserEmail() {
        String email = "";

        try {
            email = extShipment.get("emailUser").toString();
        } catch (JSONException e) {
            //some exception handler code.
        }

        User user1 = new User();
        user1.setEmail(email);
        user1.setLabel("LearnId");
        user1.setCustomerNumber("1718");

        given()
                .log().everything()
                .contentType("application/json")
                .body(user1)
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
                .assertThat()
                .statusCode(202)
                .body("errorMessage", equalTo(null))
                .body("processedByTask", equalTo(true))
                .log().body();

        given()
                .log().all()
                .queryParam("email", email)
                .when()
                .get("/users")
                .then()
                .log().body()
                .statusCode(200)
                .body("$.", hasSize(1));

        given()
                .log().all()
                .queryParam("email", email)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .log().body()
                .body("[0].email", equalTo(email))
                .body("[0].customerNumber", equalTo("1718"))
                .body("[0].label", equalTo("LearnId"))
                .body("[0].accountSetId", equalTo(null));

    }

    @Test
    public void newUserEmail() {
        String email = "aap@mailinator.com";

        try {
            extShipment.remove("emailUser");
            extShipment.put("emailUser", email);
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

        given()
                .log().all()
                .queryParam("email", email)
                .when()
                .get("/users")
                .then()
                .log().body()
                .statusCode(200)
                .body("$.", hasSize(1));

        given()
                .log().all()
                .queryParam("email", email)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .log().body()
                .body("[0].email", equalTo(email))
                .body("[0].customerNumber", equalTo(null))
                .body("[0].label", equalTo("LearnId"))
                .body("[0].accountSetId", equalTo(null));
    }

}
