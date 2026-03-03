package net.schwitzkroko.demo.validplate.distinct.district;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.IOException;
import java.util.List;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.schwitzkroko.demo.validplate.distinct.DistinctId;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@QuarkusTest
@Slf4j
class DistrictRepoTest {

  @Inject
  DistrictRepo districtRepo;

  @Test
  void testFindByCode_knownCode_returnsRecord() {
    var oResult = districtRepo.findByCode("A");

    DistinctId.DistrictRecord result = assertDoesNotThrow(oResult::get,
        "findByCode(\"A\") should return a non-empty Optional");

    log.debug("findByCode(\"A\"): {}", result);

    assertThat(result, is(notNullValue()));
    assertThat(result.code(), equalTo("A"));
    assertThat(result.cityOrDistrict(), equalTo("Augsburg"));
    assertThat(result.landName(), equalTo("Bayern"));
    assertThat(result.landIso(), equalTo("DE-BY"));
  }

  @ParameterizedTest
  @CsvSource({"A,   Augsburg", "AA,  Ostalbkreis", "AB,  Aschaffenburg", "B,   Berlin",
      "HH,  Freie und Hansestadt Hamburg", "M,   München", "HDL, Börde", "UNKNOWN, "})
  void testFindByCode_knownCode_returnsCityOrDistrict(String code, String expectedCityOrDistrict) {
    var oResult = districtRepo.findByCode(code);

    log.debug("findByCode(\"{}\"): {}", code, oResult);

    if (StringUtils.isBlank(expectedCityOrDistrict)) {

      assertThat(oResult.isEmpty(), is(true));

    } else {

      DistinctId.DistrictRecord result = assertDoesNotThrow(oResult::get,
          "findByCode(\"" + code + "\") should return a non-empty Optional");

      assertThat(result, is(notNullValue()));
      assertThat(result.cityOrDistrict(), equalTo(expectedCityOrDistrict));
    }
  }

  @Test
  void testFindByCode_unknownCode_returnsEmpty() {
    var result = districtRepo.findByCode("UNKNOWN");

    assertThat(result.isEmpty(), is(true));
  }

  @Test
  void testFindByCode_toDisplayString() {

    String codeAng = "ANG";
    var oResultAng = districtRepo.findByCode(codeAng);

    DistinctId.DistrictRecord resultAng = assertDoesNotThrow(oResultAng::get,
        "findByCode(\"" + oResultAng + "\") should return a non-empty Optional");

    assertThat(resultAng.toDisplayString(), is("D/ANG/ANGermünde"));
  }

  @Test
  void testParse() throws IOException {
    List<DistinctId.DistrictRecord> districts = DistrictRepoConfig.parse(DistrictRepoConfig.CSV_RESOURCE);

    assertThat(districts, is(not(empty())));
    assertThat(districts, hasSize(711)); // 713 lines minus header

    DistinctId.DistrictRecord first = districts.get(0);
    log.debug("First record: {}", first);

    assertThat(first.national(), equalTo("D"));
    assertThat(first.code(), equalTo("A"));
    assertThat(first.cityOrDistrict(), equalTo("Augsburg"));
    assertThat(first.landName(), equalTo("Bayern"));
    assertThat(first.landIso(), equalTo("DE-BY"));
  }
}
