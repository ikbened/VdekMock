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
        User u = new User();
        u.setEmail("aap@aap.nl");
        u.setLabel("LearnId");

        given()
                .contentType("application/json")
                .body(u)
                .when()
                .post("/users")
                .then()
                .assertThat()
                .statusCode(202)
                .log().body();

        UserClaim uc = new UserClaim();
        uc.setUserId(u.getId());
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
                .body("userId", equalTo(u.getId()))
                .body("claimType", equalTo("CustomerNumber"))
                .body("claimValue", equalTo("1819"))
                .log().body();
    }

    @Test
    public void GetUserClaimById() {
        User u = new User();
        u.setEmail("aap@aap.nl");
        u.setLabel("LearnId");

        given()
                .contentType("application/json")
                .body(u)
                .when()
                .post("/users")
                .then()
                .assertThat()
                .statusCode(202)
                .log().body();

        UserClaim uc = new UserClaim();
        uc.setUserId(u.getId());
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
                .statusCode(202);

        given()
                .log().everything()
                .pathParam("ucId", uc.getId())
                .when()
                .get("/userclaims/{ucId}")
                .then()
                .assertThat()
                .statusCode(200)
                .log().body();

    }


    @Test
    public void GetAllUserClaimsByUserId() {
        User u = new User();
        u.setEmail("aap@aap.nl");
        u.setLabel("LearnId");

        given()
                .contentType("application/json")
                .body(u)
                .when()
                .post("/users")
                .then()
                .assertThat()
                .statusCode(202)
                .log().body();

        UserClaim uc = new UserClaim();
        uc.setUserId(u.getId());
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
        uc.setUserId(u.getId());
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
                .queryParam("userId", u.getId())
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
