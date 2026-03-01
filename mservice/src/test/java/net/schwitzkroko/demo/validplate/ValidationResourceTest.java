package net.schwitzkroko.demo.validplate;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import lombok.extern.slf4j.Slf4j;
import net.schwitzkroko.demo.validplate.plate.PlateTestCaseUtil;
import net.schwitzkroko.demo.validplate.plate.PlateTestCaseUtil.PlateTestData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@Slf4j
@QuarkusTest
@TestHTTPEndpoint(ValidationResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ValidationResourceTest {

  static final String CSV_RESOURCE_ORIGINAL = "/aufgabenstellung/original_korrigiert.csv";
  static final String CSV_RESOURCE_DISTRICT = "/aufgabenstellung/custom_district.csv";
  static final String CSV_RESOURCE_SPECIAL = "/aufgabenstellung/custom_sonder.csv";

  private List<PlateTestData> testCasesOriginal;
  private List<PlateTestData> testCasesDistrictCustom;
  private List<PlateTestData> testCasesSpecialCustom;

  @BeforeAll
  void loadTestCases() throws IOException {
    testCasesOriginal = PlateTestCaseUtil.parse(CSV_RESOURCE_ORIGINAL);
    log.info("Loaded {} original test cases from CSV", testCasesOriginal.size());

    testCasesDistrictCustom = PlateTestCaseUtil.parse(CSV_RESOURCE_DISTRICT);
    log.info("Loaded {} custom district test cases from CSV", testCasesDistrictCustom.size());

    testCasesSpecialCustom = PlateTestCaseUtil.parse(CSV_RESOURCE_SPECIAL);
    log.info("Loaded {} custom district test cases from CSV", testCasesSpecialCustom.size());
  }

  Stream<PlateTestData> testCasesOriginal() {
    return testCasesOriginal.stream();
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("testCasesOriginal")
  void tesWithTestCasesOriginal(PlateTestData tc) {
    log.debug("validate: input='{}' expected success={} output='{}'", tc.input(), tc.success(), tc.output());

    var spec = given().when().get("/validate/" + tc.input()).then();

    if (Boolean.TRUE.equals(tc.success())) {
      spec.statusCode(200).body(is(tc.output()));
    } else {
      spec.statusCode(422);
    }
  }

  Stream<PlateTestData> testCasesDistrictCustom() {
    return testCasesDistrictCustom.stream();
  }

  // @Disabled("Custom district test cases are currently not supported by the
  // implementation")
  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("testCasesDistrictCustom")
  void tesWithTestCasesDistrictCustom(PlateTestData tc) {
    log.debug("validate: input='{}' expected success={} output='{}'", tc.input(), tc.success(), tc.output());

    var spec = given().when().get("/validate/" + tc.input()).then();

    if (Boolean.TRUE.equals(tc.success())) {
      spec.statusCode(200).body(is(tc.output()));
    } else {
      spec.statusCode(422);
    }
  }

  Stream<PlateTestData> testCasesSpecialCustom() {
    return testCasesSpecialCustom.stream();
  }

  // @Disabled("Custom special test cases are currently not supported by the
  // implementation")
  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("testCasesSpecialCustom")
  void tesWithTestCasesSpecialCustom(PlateTestData tc) {
    log.debug("validate: input='{}' expected success={} output='{}'", tc.input(), tc.success(), tc.output());

    var spec = given().when().get("/validate/" + tc.input()).then();

    if (Boolean.TRUE.equals(tc.success())) {
      spec.statusCode(200).body(is(tc.output()));
    } else {
      spec.statusCode(422);
    }
  }
}
