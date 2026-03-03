package net.schwitzkroko.demo.validplate.distinct.special;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.schwitzkroko.demo.validplate.distinct.DistinctId;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Slf4j
@QuarkusTest
class SpecialRepoTest {

  @Inject
  SpecialRepo specialRepo;

  @Test
  void findByCode_multipleEntries_returnsFirst() {
    var oResult = specialRepo.findByCode("B");

    DistinctId.SpecialRecord result = assertDoesNotThrow(oResult::get,
        "findByCode(\"B\") should return a non-empty Optional");

    log.debug("findByCode(\"B\"): {}", result);

    assertThat(result.code(), equalTo("B"));
  }

  @Test
  void findAllByCode_multipleEntries_returnsAll() {
    List<DistinctId.SpecialRecord> results = specialRepo.findAllByCode("B");

    log.debug("findAllByCode(\"B\"): {}", results);

    assertThat(results, hasSize(greaterThan(1)));
    results.forEach(record -> {
      log.debug("Record: {}", record);
      assertThat(record.code(), equalTo("B"));
    });
  }

  @ParameterizedTest
  @CsvSource({"BD,  Bund", "BG,  Bund", "BP,  Bund", "BW,  Bund", "THW, Bund", "Y,   Bund", "X,   Bund", "B,   Länder",
      "BBL, Länder", "NRW, Länder"})
  void findByCode_knownCode_returnsExpectedType(String code, String expectedType) {
    var oResult = specialRepo.findByCode(code);

    DistinctId.SpecialRecord result = assertDoesNotThrow(oResult::get,
        "findByCode(\"" + code + "\") should return a non-empty Optional");

    log.debug("findByCode(\"{}\"): {}", code, result);

    assertThat(result.type(), equalTo(expectedType));
  }

  @Test
  void findByCode_unknownCode_returnsEmpty() {
    assertThat(specialRepo.findByCode("UNKNOWN").isEmpty(), is(true));
  }

  @Test
  void findAllByCode_unknownCode_returnsEmptyList() {
    assertThat(specialRepo.findAllByCode("UNKNOWN"), is(empty()));
  }

  @Test
  void testFindByCode_toDisplayString() {
    var oResult = specialRepo.findByCode("THW");

    DistinctId.SpecialRecord result = assertDoesNotThrow(oResult::get,
        "findByCode(\"THW\") should return a non-empty Optional");

    log.debug("findByCode(\"THW\"): {}", result.toDisplayString());

    assertThat(result.toDisplayString(), equalTo("D/THW/Bund"));
  }
}
