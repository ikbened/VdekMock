package com.spronq.mbt.VdekMock;

import io.restassured.RestAssured;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

import static io.restassured.RestAssured.given;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VdekMockApplicationTests {

    @BeforeClass
    static public void initPath() {
        RestAssured.baseURI = "http://localhost:8080";
    }


	@Test
	public void contextLoads() {
    }

    @Test
    public void BasicTest() {
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
				.get(RestAssured.baseURI + "/shipments/")
				.then()
				.assertThat()
				.statusCode(200);
	}

}
