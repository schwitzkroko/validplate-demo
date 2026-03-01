package net.schwitzkroko.demo.validplate.distinct.district.impl;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;
import net.schwitzkroko.demo.validplate.distinct.district.DistrictRecord;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Slf4j
class DistrictCsvUtilTest {

    @Test
    void testParse() throws IOException {
        List<DistrictRecord> districts = DistrictCsvUtil.parse(DistrictRepoConfig.CSV_RESOURCE);

        assertThat(districts, is(not(empty())));
        assertThat(districts, hasSize(711)); // 713 lines minus header

        DistrictRecord first = districts.get(0);
        log.debug("First record: {}", first);

        assertThat(first.national(), equalTo("D"));
        assertThat(first.code(), equalTo("A"));
        assertThat(first.cityOrDistrict(), equalTo("Augsburg"));
        assertThat(first.landName(), equalTo("Bayern"));
        assertThat(first.landIso(), equalTo("DE-BY"));
    }
}
