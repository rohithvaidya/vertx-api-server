package com.example.starter;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle()).onComplete(testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void verticle_deployed(Vertx vertx, VertxTestContext testContext) throws Throwable {
    testContext.completeNow();
  }

  @BeforeAll
  public static void setup(){
    RestAssured.baseURI = "http://localhost:8888/";
  }

  @Test
  public void test_get_endpoint(){
    Response response = RestAssured.given().auth().preemptive().basic("rohith", "rohith").get("/view-device");
    //asserting whether status code is successful
    assertThat(response.getStatusCode(), equalTo(200));

  }

 // Test for Add Device
    @Test
    void testAddDevice_Success() {
        String requestBody = "{\n" +
                     "  \"deviceId\": \"123-asdasd-123\",\n" +
                     "  \"Domain\": \"smart-transport\",\n" +
                     "  \"state\": \"MH\",\n" +
                     "  \"city\": \"Pune\",\n" +
                     "  \"location\": {\n" +
                     "    \"Type\": \"point\",\n" +
                     "    \"Coordinates\": [34.56, 76.34]\n" +
                     "  },\n" +
                     "  \"deviceType\": \"smart-camera\"\n" +
                     "}";

        Response response = RestAssured.given()
            .auth().preemptive().basic("rohith", "rohith")
            .contentType("application/json")
            .body(requestBody)
            .post("/add-device");

        assertThat(response.getStatusCode(), equalTo(201));
    }

    @Test
    void testAddDevice_NoAuth() {
         String requestBody = "{\n" +
                     "  \"deviceId\": \"123-asdasd-123\",\n" +
                     "  \"Domain\": \"smart-transport\",\n" +
                     "  \"state\": \"MH\",\n" +
                     "  \"city\": \"Pune\",\n" +
                     "  \"location\": {\n" +
                     "    \"Type\": \"point\",\n" +
                     "    \"Coordinates\": [34.56, 76.34]\n" +
                     "  },\n" +
                     "  \"deviceType\": \"smart-camera\"\n" +
                     "}";

        Response response = RestAssured.given()
            .contentType("application/json")
            .body(requestBody)
            .post("/add-device");

        assertThat(response.getStatusCode(), equalTo(401));
    }

  @Test
void testUpdateDevice_Success() {
   //Updating city and state
    String requestBody = "{\n" +
                     "  \"deviceId\": \"123-asdasd-123\",\n" +
                     "  \"Domain\": \"smart-transport\",\n" +
                     "  \"state\": \"KA\",\n" +
                     "  \"city\": \"Bangalore\",\n" +
                     "  \"location\": {\n" +
                     "    \"Type\": \"point\",\n" +
                     "    \"Coordinates\": [34.56, 76.34]\n" +
                     "  },\n" +
                     "  \"deviceType\": \"smart-camera\"\n" +
                     "}";

    Response response = RestAssured.given()
        .auth().preemptive().basic("rohith", "rohith")
        .contentType("application/json")
        .body(requestBody)
        .put("/update-device/123-asdasd-123");

    assertThat(response.getStatusCode(), equalTo(200));
}

@Test
void testDeleteDevice_DeviceNotFound() {
    Response response = RestAssured.given()
        .auth().preemptive().basic("rohith", "rohith")
        .delete("/delete-device/123-asdasd-1241242");

    assertThat(response.getStatusCode(), equalTo(404));
}

}
