package net.schwitzkroko.demo.validplate.plate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
class PlateMatcherUtil {

  /**
   * Unambiguous: explicit '-' separator present after normalisation. Group 1 =
   * district code (1–3 letters), group 2 = letters (1–2), group 3 = digits (1–4).
   */
  static final Pattern PLATE_PATTERN_SEPARATED = Pattern.compile("^([A-ZÄÖÜ]{1,3})-([A-Z]{1,2})(\\d{1,4})$");

  /**
   * Ambiguous: no separator. All leading letters go into group 1 so the whole
   * unsplit token is validated against the district repo (e.g. "LIIT" fails
   * lookup) rather than silently mis-splitting into a wrong code + letters. Group
   * 1 = all leading letters (1–6), group 2 = empty string, group 3 = digits
   * (1–4).
   */
  static final Pattern PLATE_PATTERN_UNSEPARATED = Pattern.compile("^([A-ZÄÖÜ]{1,6})()(\\d{1,4})$");

  /**
   * Normalizes the input plate string by trimming, uppercasing, and
   * canonicalizing
   *
   * Canonicalize by inserting explicit separator between district code and the
   * letter/digit part. A hyphen or whitespace between two letter-groups is
   * treated as the canonical '-' separator; all remaining whitespace is then
   * removed.
   *
   * Examples: "LI-IT 100" -> "LI-IT100" "LI IT100" -> "LI-IT100" (space as
   * separator) "LIIT 100" -> "LIIT100" (no separator — may be ambiguous)
   *
   * @param plate
   * @return
   */
  String normalize(String plate) {
    String normalised = plate.strip().toUpperCase().replaceAll("([A-ZÄÖÜ]{1,3})[\\s-]+([A-Z])", "$1-$2")
        .replaceAll("\\s+", "");
    log.debug("util: normalised='{}' from input='{}'", normalised, plate);
    return normalised;
  }

  PlateModel parse(String normalised) {

    Matcher separated = PLATE_PATTERN_SEPARATED.matcher(normalised);
    if (separated.matches()) {
      return new PlateModel.Valid(separated.group(1), separated.group(2), separated.group(3));
    }

    Matcher unseparated = PLATE_PATTERN_UNSEPARATED.matcher(normalised);
    if (unseparated.matches()) {
      log.debug("parse: '{}' matched unseparated pattern — ambiguous", normalised);
      return new PlateModel.Ambiguous(unseparated.group(1), unseparated.group(3));
    }

    log.debug("parse: '{}' does not match any plate pattern", normalised);
    return new PlateModel.Unparsable();
  }
}
