package org.acme.vertx;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.startsWith;

@QuarkusTest
class GreetingResourceTest {

    @Test
    void testGreeter() {
        given()
                .when().get("/hello/Quarkus")
                .then()
                .statusCode(200)
                .body(startsWith("Hello Quarkus!"));
    }
}
