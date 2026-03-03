package net.schwitzkroko.demo.validplate.plate;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.schwitzkroko.demo.validplate.plate.PlateTestCaseUtil.PlateTestData;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Slf4j
@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PlateServiceTest {

  private static final String CSV_RESOURCE_ORIGINAL = "/aufgabenstellung/original.csv";

  @Inject
  PlateService plateService;

  private List<PlateTestData> testCasesOriginal;

  @BeforeAll
  void loadTestCases() throws IOException {
    testCasesOriginal = PlateTestCaseUtil.parse(CSV_RESOURCE_ORIGINAL);
    log.info("Loaded {} test cases from CSV", testCasesOriginal.size());
  }

  Stream<PlateTestData> testCasesOriginal() {
    return testCasesOriginal.stream();
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("testCasesOriginal")
  void testDigest(PlateTestData tc) {
    log.debug("digest: input='{}' expected httpCode={} output='{}'", tc.input(), tc.httpCode(), tc.output());

    PlateModel result = plateService.digest(tc.input());

    if (tc.httpCode() == 200) {

      assertThat(result, instanceOf(PlateModel.Valid.class));
      assertThat(tc.remark(), result.canonical(), equalTo(tc.output()));

    } else if (tc.httpCode() == 404) {

      assertThat(result, anyOf(instanceOf(PlateModel.Invalid.class), instanceOf(PlateModel.Ambiguous.class)));

      switch (result) {
        case PlateModel.Invalid i -> assertThat(tc.remark(), i.canonical(), equalTo("error"));
        case PlateModel.Ambiguous a -> assertThat(tc.remark(), a.canonical(), not(emptyOrNullString()));
        default -> throw new IllegalArgumentException("Unexpected value: " + result);
      }

    } else {
      assertThat(tc.remark(), result, instanceOf(PlateModel.Unparsable.class));
    }
  }
}
