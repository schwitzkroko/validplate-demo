package net.schwitzkroko.demo.validplate;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
@TestHTTPEndpoint(ValidationResource.class)
class ValidationResourceTest {
    @Test
    void testHelloEndpoint() {
        given()
          .when().get("/hello")
          .then()
             .statusCode(200)
             .body(is("Hello from Quarkus REST"));
    }

    @Test
    void testValidateEndpoint() {
        given()
          .when().get("/validate/ABC123")
          .then()
             .statusCode(200)
             .body(is("Validating plate: ABC123"));
    }
    
    @ParameterizedTest
    @CsvSource({
        "ABC123, Validating plate: ABC123",
        "XYZ789, Validating plate: XYZ789",
        "LIIT 100, Validating plate: LIIT 100",
        "HalloWelt!, Validating plate: HalloWelt!",
        "LI-IT 100, Validating plate: LI-IT 100",
        "LIT 100, Validating plate: LIT 100"
    })
    void testValidateEndpointWithData(String plate, String expectedResponse) {
        String responseBody = given()
          .when().get("/validate/" + plate)
          .then()
             .statusCode(200)
             .extract().body().asString();

        assertThat(responseBody, equalTo(expectedResponse));
    }

}