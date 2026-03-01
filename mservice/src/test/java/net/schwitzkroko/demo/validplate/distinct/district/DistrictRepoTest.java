package net.schwitzkroko.demo.validplate.distinct.district;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@QuarkusTest
@Slf4j
class DistrictRepoTest {

    @Inject
    DistrictRepo districtRepo;

    @Test
    void findByCode_knownCode_returnsRecord() {
        DistrictRecord result = districtRepo.findByCode("A");

        log.debug("findByCode(\"A\"): {}", result);

        assertThat(result, is(notNullValue()));
        assertThat(result.code(), equalTo("A"));
        assertThat(result.cityOrDistrict(), equalTo("Augsburg"));
        assertThat(result.landName(), equalTo("Bayern"));
        assertThat(result.landIso(), equalTo("DE-BY"));
    }

    @ParameterizedTest
    @CsvSource({
        "A,   Augsburg",
        "AA,  Ostalbkreis",
        "AB,  Aschaffenburg",
        "B,   Berlin",
        "HH,  Freie und Hansestadt Hamburg",
        "M,   München",
        "HDL, Börde",
        "UNKNOWN, "
    })
    void findByCode_knownCode_returnsCityOrDistrict(String code, String expectedCityOrDistrict) {
        DistrictRecord result = districtRepo.findByCode(code);

        log.debug("findByCode(\"{}\"): {}", code, result);

        if (StringUtils.isBlank(expectedCityOrDistrict)) {
            assertThat(result, is(nullValue()));
        } else {
            assertThat(result, is(notNullValue()));
            assertThat(result.cityOrDistrict(), equalTo(expectedCityOrDistrict));
        }
    }

    @Test
    void findByCode_unknownCode_returnsNull() {
        DistrictRecord result = districtRepo.findByCode("UNKNOWN");

        assertThat(result, is(nullValue()));
    }
}
