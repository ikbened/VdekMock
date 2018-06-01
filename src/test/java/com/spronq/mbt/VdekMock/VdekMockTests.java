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
import static org.hamcrest.Matchers.equalTo;

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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyymmddhhmmss");
        String d = sdf.format(new Date());

        try {
            extShipment = new JSONObject();
            extShipment
                    .put("customerNumber", "1718")
                    .put("ean", "9789034506801")
                    .put("orderId", "1718_" + d)
                    .put("orderLine", "1")
                    .put("schoolId", "1641")
                    .put("sessionId", "")
                    .put("emailAddress", "cust" + d + "@mailinator.com")
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
                    .put("emailUser", "user" + d + "@mailinator.com");

        } catch (JSONException e) {
            //some exception handler code.
        }

    }


    @Test
    public void CustomerNumberIsMissing() {
        Response response =
                given()
                        .contentType("application/json")
                        .body(extShipment.toString())
                        .when()
                        .post("/shipments")
                        .then()
                        .contentType(ContentType.JSON)
                        .log().body()
                        .assertThat()
                        .statusCode(202)
                        .and()
                        .extract()
                        .response();

        JsonPath jsonPathEvaluator = response.jsonPath();
        String shipmentId = jsonPathEvaluator.getString("shipmentId");

        given()
                .pathParam("ShipmentId", shipmentId)
                .when()
                .get("/shipments/{ShipmentId}")
                .then()
                .log().body()
                .assertThat()
                .statusCode(200)
                .body("errorMessage", equalTo("ERROR - customer number is missing"));
    }


    @Test
    public void CustomerNumberisNotUnique() {
        String customerNumber = "1718";
        User user1 = new User();
        user1.setEmail("aap@mailinator.com");
        user1.setLabel("LearnId");
        user1.setCustomerNumber(customerNumber);

        given()
                .log().everything()
                .contentType("application/json")
                .body(user1)
                .when()
                .post("/users")
                .then()
                .statusCode(202);

        User user2 = new User();
        user2.setEmail("noot@mailinator.com");
        user2.setLabel("LearnId");
        user2.setCustomerNumber(customerNumber);

        given()
                .log().everything()
                .contentType("application/json")
                .body(user2)
                .when()
                .post("/users")
                .then()
                .statusCode(202);

        try {
            extShipment.remove("customerNumber");
            extShipment.put("customerNumber", customerNumber);
        } catch (JSONException e) {
            //some exception handler code.
        }

        String shipmentId = given()
                .log().everything()
                .contentType("application/json")
                .body(extShipment.toString())
                .when()
                .post("/shipments")
                .then()
                .contentType(ContentType.JSON)
                .statusCode(202)
                .extract().jsonPath().getString("shipmentId");

       given()
               .pathParam("ShipmentId", shipmentId)
               .when()
               .get("/shipments/{ShipmentId}")
               .then()
               .log().body()
               .assertThat()
               .statusCode(200)
               .body("errorMessage", equalTo("Customer number is not unique"));
    }

    @Test
    public void CustomerEmailIsNotUnique() {
        String email = "aap@mailinator.com";
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

        try {
            extShipment.remove("emailAddress");
            extShipment.put("emailAddress", email);
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
                .body("errorMessage", equalTo("Email is not unique."));
    }

    @Test
    public void NewCustomerNewUser() {
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

        given()
                .log().all()
                .queryParam("email", userEmail)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .log().body()
                .body("[0].email", equalTo(userEmail))
                .body("[0].customerNumber", equalTo(null))
                .body("[0].label", equalTo("LearnId"))
                .body("[0].accountSetId", equalTo(null));

    }

}
