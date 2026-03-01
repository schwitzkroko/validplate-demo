package net.schwitzkroko.demo.validplate.distinct.special;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Slf4j
@QuarkusTest
class SpecialRepoTest {

  @Inject
  SpecialRepo specialRepo;

  @Test
  void findByCode_multipleEntries_returnsFirst() {
    // 'B' appears as both Länder and Diplo — findByCode returns the first
    SpecialRecord result = specialRepo.findByCode("B");

    log.debug("findByCode(\"B\"): {}", result);

    assertThat(result, is(notNullValue()));
    assertThat(result.code(), equalTo("B"));
  }

  @Test
  void findAllByCode_multipleEntries_returnsAll() {
    // 'B' has entries under Länder and Diplo
    List<SpecialRecord> results = specialRepo.findAllByCode("B");

    log.debug("findAllByCode(\"B\"): {}", results);

    assertThat(results, hasSize(greaterThan(1)));

    // assertThat(results, everyItem(hasProperty("code", equalTo("B"))));

    results.forEach(record -> {
      log.debug("Record: {}", record);
      assertThat(record.code(), equalTo("B"));
    });
  }

  @ParameterizedTest
  @CsvSource({"BD,  Bund", "BG,  Bund", "BP,  Bund", "BW,  Bund", "THW, Bund", "Y,   Bund", "X,   Bund", "B,   Länder",
      "BBL, Länder", "NRW, Länder"})
  void findByCode_knownCode_returnsExpectedType(String code, String expectedType) {
    SpecialRecord result = specialRepo.findByCode(code);

    log.debug("findByCode(\"{}\"): {}", code, result);

    assertThat(result, is(notNullValue()));
    assertThat(result.type(), equalTo(expectedType));
  }

  @Test
  void findByCode_unknownCode_returnsNull() {
    assertThat(specialRepo.findByCode("UNKNOWN"), is(nullValue()));
  }

  @Test
  void findAllByCode_unknownCode_returnsEmptyList() {
    assertThat(specialRepo.findAllByCode("UNKNOWN"), is(empty()));
  }
}
