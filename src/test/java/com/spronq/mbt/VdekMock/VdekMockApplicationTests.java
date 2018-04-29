package com.spronq.mbt.VdekMock;

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
import static org.hamcrest.Matchers.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static io.restassured.RestAssured.given;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class VdekMockApplicationTests {

    @Autowired
    ExtendedShipmentRepository shipmentRepository;

    @Before
    public void initPath() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Test
    public void PostSimpleShipment() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyymmddhhmmss");
        String d = sdf.format(new Date());

        JSONObject extShipment = new JSONObject();
        try {
                extShipment
                    .put("Customernumber", "1718")
                    .put("EAN", "9789034506801")
                    .put("OrderId", "1718_" + d)
                    .put("OrderLine", "1")
                    .put("SchoolId", "1641")
                    .put("SessionId", "")
                    .put("EmailAddress", "cust" + d + "@mailinator.com")
                    .put("Label", "VDE")
                    .put("PostalCode", "2323ab")
                    .put("FirstName", "Bokito")
                    .put("MiddleName", "de")
                    .put("LastName", "Aap")
                    .put("GroupName", "")
                    .put("Administration", "Dynamics")
                    .put("Address", "SomeStreet")
                    .put("AddressNumber", "1")
                    .put("AddressAdjunct", "")
                    .put("City", "SomeCity")
                    .put("Country", "SomeCountry")
                    .put("Gender", "M")
                    .put("BirthDate", "2004-02-01")
                    .put("Amount", "1")
                    .put("StartDate", "2018-04-27")
                    .put("DisplayName", "SomeDisplayName")
                    .put("EmailUser", "user" + d + "@mailinator.com");

        } catch (JSONException e) {
            //some exception handler code.
        }


        given()
				.contentType("application/json")
				.body(extShipment.toString())
				.when()
				.post("/shipments")
				.then()
				.assertThat()
				.statusCode(202);
	}

    @Test
    public void ShipmentWithoutCustomerNumber() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyymmddhhmmss");
        String d = sdf.format(new Date());

        JSONObject extShipment = new JSONObject();
        try {
            extShipment
                    .put("Customernumber", "")
                    .put("EAN", "9789034506801")
                    .put("OrderId", "1718_" + d)
                    .put("OrderLine", "1")
                    .put("SchoolId", "1641")
                    .put("SessionId", "")
                    .put("EmailAddress", "cust" + d + "@mailinator.com")
                    .put("Label", "VDE")
                    .put("PostalCode", "2323ab")
                    .put("FirstName", "Bokito")
                    .put("MiddleName", "de")
                    .put("LastName", "Aap")
                    .put("GroupName", "")
                    .put("Administration", "Dynamics")
                    .put("Address", "SomeStreet")
                    .put("AddressNumber", "1")
                    .put("AddressAdjunct", "")
                    .put("City", "SomeCity")
                    .put("Country", "SomeCountry")
                    .put("Gender", "M")
                    .put("BirthDate", "2004-02-01")
                    .put("Amount", "1")
                    .put("StartDate", "2018-04-27")
                    .put("DisplayName", "SomeDisplayName")
                    .put("EmailUser", "user" + d + "@mailinator.com");

        } catch (JSONException e) {
            //some exception handler code.
        }

        Response response =
                given()
                        .contentType("application/json")
                        .body(extShipment.toString())
                        .when()
                        .post("/shipments")
                        .then()
                        .contentType(ContentType.JSON)  // check that the content type return from the API is JSON
                        .assertThat()
                        .statusCode(202)
                        .and()
                        .extract().response(); // extract the response

        JsonPath jsonPathEvaluator = response.jsonPath();
        String shipmentId = jsonPathEvaluator.get("$.ShipmentId");

        given()
                .pathParam("ShipmentId", shipmentId)
                .when()
                .get("shipments/{ShipmentId}")
                .then()
                .assertThat()
                .statusCode(202)
                .body("ErrorCode", equalTo("ERROR - Missing customer number"));
    }

    @Test
    public void GetUnknownShipmentById() {

        given()
                .pathParam("ShipmentId", 1)
                .when()
                .get("shipments/{ShipmentId}")
                .then()
                .assertThat()
                .statusCode(404);
    }


}
