package net.schwitzkroko.demo.validplate.plate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.regex.Matcher;
import java.util.stream.Stream;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class PlateMatcherUtilTest {

  static Stream<Arguments> matcherCases() {
    return Stream.of(Arguments.of(Named.of("separated: LI-IT 100", "LI-IT 100"), "LI", "IT"),
        Arguments.of(Named.of("separated: LI IT100", "LI IT100"), "LI", "IT"),
        Arguments.of(Named.of("unseparated: LIIT100", "LIIT100"), "LIIT", ""),
        Arguments.of(Named.of("unseparated: LIIT 100", "LIIT 100"), "LIIT", ""));
  }

  @ParameterizedTest(name = "[{index}] ''{0}'' -> ''{1}''")
  @CsvSource({
      // separator canonicalisation
      "LI-IT 100,  LI-IT100", "LI IT100,   LI-IT100", "LI IT 100,  LI-IT100", "LI-IT100,   LI-IT100",

      // no separator — ambiguous, passed through as-is
      "LIIT100,    LIIT100", "LIIT 100,   LIIT100",

      // trimming and uppercasing
      "  li-it100 , LI-IT100", "li it 100,   LI-IT100",

      // three-letter district codes
      "THW-AB26,   THW-AB26", "THW AB26,   THW-AB26", "THW AB 26,  THW-AB26",

      // single-letter district codes
      "B AB1234,   B-AB1234", "B-AB1234,   B-AB1234",})
  void testNormalize(String input, String expected) {

    String normalized = PlateMatcherUtil.normalize(input.strip());
    log.debug("normalized='{}' from input='{}'", normalized, input);

    assertThat(normalized, equalTo(expected.strip()));
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("matcherCases")
  void testNormalizeWithMatcher(String input, String expectedDistrictCode, String expectedLetters) {

    String normalized = PlateMatcherUtil.normalize(input);

    Matcher m = PlateMatcherUtil.PLATE_PATTERN_SEPARATED.matcher(normalized);
    if (!m.matches()) {
      log.debug("separated: '{}' does not match plate \"separated\" pattern", normalized);
      m = PlateMatcherUtil.PLATE_PATTERN_UNSEPARATED.matcher(normalized);
    }

    assertThat("'" + normalized + "' should match a plate pattern", m.matches(), is(true));

    String districtCode = m.group(1);
    String letters = m.group(2);
    String digits = m.group(3);

    log.debug("districtCode='{}', letters='{}', digits='{}'", districtCode, letters, digits);

    assertThat(districtCode, equalTo(expectedDistrictCode));
    assertThat(letters, equalTo(expectedLetters));
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("matcherCases")
  void testParse(String input, String expectedDistrictCode, String expectedLetters) {

    String normalized = PlateMatcherUtil.normalize(input);

    PlateModel plateModel = PlateMatcherUtil.parse(normalized);

    switch (plateModel) {
      case PlateModel.Unparsable x -> fail("'" + normalized + "' should match a plate pattern");
      case PlateModel.Invalid i -> fail("'" + normalized + "' should match a plate pattern");
      case PlateModel.Ambiguous a -> {
        log.debug("districtCode='{}', digits='{}'", a.distinctCode(), a.digits());
        assertThat(a.distinctCode(), equalTo(expectedDistrictCode));
        assertThat(expectedLetters, emptyString());
      }
      case PlateModel.Valid d -> {
        log.debug("districtCode='{}', letters='{}', digits='{}'", d.distinctCode(), d.letters(), d.digits());
        assertThat(d.distinctCode(), equalTo(expectedDistrictCode));
        assertThat(d.letters(), equalTo(expectedLetters));
        assertThat(d.digits(), is(not(emptyString())));
      }
    }
  }

}
