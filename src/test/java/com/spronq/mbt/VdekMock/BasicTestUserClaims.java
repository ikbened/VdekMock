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
import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class BasicTestUserClaims {

    @Autowired
    ExtendedShipmentRepository shipmentRepository;

    private JSONObject extShipment;

    @Before
    public void initPath() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Test
    public void PostUserClaim() {
        User user = new User();
        user.setEmail("aap@aap.nl");
        user.setLabel("LearnId");

        String userId = given()
                .contentType("application/json")
                .body(user)
                .when()
                .post("/users")
                .then()
                .assertThat()
                .statusCode(202)
                .log().body()
                .extract().jsonPath().getString("id");

        UserClaim uc = new UserClaim();
        uc.setUserId(userId);
        uc.setClaimType("CustomerNumber");
        uc.setClaimValue("1819");

        given()
                .log().everything()
                .contentType("application/json")
                .body(uc)
                .when()
                .post("/userclaims")
                .then()
                .assertThat()
                .statusCode(202)
                .body("userId", equalTo(userId))
                .body("claimType", equalTo("CustomerNumber"))
                .body("claimValue", equalTo("1819"))
                .log().body();
    }

    @Test
    public void GetUserClaimById() {
        User user = new User();
        user.setEmail("aap@aap.nl");
        user.setLabel("LearnId");

        String userId = given()
                .contentType("application/json")
                .body(user)
                .when()
                .post("/users")
                .then()
                .assertThat()
                .statusCode(202)
                .log().body()
                .extract().jsonPath().getString("id");

        UserClaim uc = new UserClaim();
        uc.setUserId(userId);
        uc.setClaimType("CustomerNumber");
        uc.setClaimValue("1819");

        String ucId = given()
                .log().everything()
                .contentType("application/json")
                .body(uc)
                .when()
                .post("/userclaims")
                .then()
                .assertThat()
                .statusCode(202)
                .extract().jsonPath().getString("id");

        given()
                .log().everything()
                .pathParam("ucId", ucId)
                .when()
                .get("/userclaims/{ucId}")
                .then()
                .assertThat()
                .statusCode(200)
                .log().body();

    }


    @Test
    public void GetAllUserClaimsByUserId() {
        User user = new User();
        user.setEmail("aap@aap.nl");
        user.setLabel("LearnId");

        String userId = given()
                .contentType("application/json")
                .body(user)
                .when()
                .post("/users")
                .then()
                .assertThat()
                .statusCode(202)
                .log().body()
                .extract().jsonPath().getString("id");

        UserClaim uc = new UserClaim();
        uc.setUserId(userId);
        uc.setClaimType("CustomerNumber");
        uc.setClaimValue("1819");

        given()
                .log().everything()
                .contentType("application/json")
                .body(uc)
                .when()
                .post("/userclaims")
                .then()
                .log().body()
                .assertThat()
                .statusCode(202);

        uc = new UserClaim();
        uc.setUserId(userId);
        uc.setClaimType("accountLink");
        uc.setClaimValue("1");

        given()
                .log().everything()
                .contentType("application/json")
                .body(uc)
                .when()
                .post("/userclaims")
                .then()
                .assertThat()
                .statusCode(202);

        given()
                .log().everything()
                .queryParam("userId", userId)
                .when()
                .get("/userclaims")
                .then()
                .log().body()
                .assertThat()
                .statusCode(200)
                .body("$.", hasSize(2))
                .body("[0].claimType", equalTo("CustomerNumber"))
                .body("[1].claimType", equalTo("accountLink"));
    }


}
