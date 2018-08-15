package com.spronq.mbt.VdekMock;

import com.spronq.mbt.VdekMock.model.User;
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
import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class BasicTestShipments {

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
    public void PostShipment() {

        given()
				.contentType("application/json")
				.body(extShipment.toString())
				.when()
				.post("/shipments")
				.then()
				.assertThat()
				.statusCode(202)
                .log().body();
	}

    @Test
    public void GetShipment() {

        String shipmentId = given()
                .log().everything()
                .contentType("application/json")
                .body(extShipment.toString())
                .when()
                .post("/shipments")
                .then()
                .log().body()
                .extract()
                .jsonPath().getString("shipmentId");

        given()
                .log().everything()
                .pathParam("ShipmentId", shipmentId)
                .when()
                .get("/shipments/{ShipmentId}")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("firstName", equalTo("Bokito"))
                .log().body();

    }



    @Test
    public void GetUnknownShipmentById() {

        given()
                .pathParam("ShipmentId", "aap")
                .when()
                .get("/shipments/{ShipmentId}")
                .then()
                .assertThat()
                .statusCode(404)
                .log().body();
    }



}
