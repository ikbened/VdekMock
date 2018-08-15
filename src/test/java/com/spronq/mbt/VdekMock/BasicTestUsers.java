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
public class BasicTestUsers {

    @Autowired
    ExtendedShipmentRepository shipmentRepository;

    private JSONObject extShipment;

    @Before
    public void initPath() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }


    @Test
    public void PostUser() {
        User user = new User();
        user.setEmail("aap@aap.nl");
        user.setLabel("LearnId");
        user.setCustomerNumber("1819");

        given()
                .contentType("application/json")
                .body(user)
                .when()
                .post("/users")
                .then()
                .assertThat()
                .statusCode(202)
                .log().body();
    }

    @Test
    public void GetUser() {
        User user = new User();
        user.setEmail("aap@aap.nl");
        user.setLabel("LearnId");
        user.setCustomerNumber("1819");

        given()
                .log().everything()
                .contentType("application/json")
                .body(user)
                .when()
                .post("/users")
                .then()
                .statusCode(202);

        given()
                .log().all()
                .queryParam("email", user.getEmail())
                .when()
                .get("/users")
                .then()
                .log().headers()
                .log().body()
                .statusCode(200)
                .body("[0].customerNumber", equalTo(user.getCustomerNumber()));

    }

    @Test
    public void GetTwoUsers() {
        User user = new User();
        user.setEmail("aap@mailinator.com");
        user.setLabel("LearnId");
        user.setCustomerNumber("1819");

        given()
                .log().everything()
                .contentType("application/json")
                .body(user)
                .when()
                .post("/users")
                .then()
                .statusCode(202);

        User user2 = new User();
        user2.setEmail(user.getEmail());
        user2.setLabel(user.getLabel());
        user2.setCustomerNumber("1820");

        given()
                .log().everything()
                .contentType("application/json")
                .body(user2)
                .when()
                .post("/users")
                .then()
                .statusCode(202);

        given()
                .log().all()
                .queryParam("email", user.getEmail())
                .when()
                .get("/users")
                .then()
                .log().body()
                .statusCode(200)
                .body("$.", hasSize(2));

    }

}
