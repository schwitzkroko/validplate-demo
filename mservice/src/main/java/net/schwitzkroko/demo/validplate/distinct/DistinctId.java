package net.schwitzkroko.demo.validplate.distinct;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Common sealed contract for all German vehicle identification codes
 * ("Unterscheidungszeichen" - UKZ). Permitted subtypes are
 * {@link DistrictRecord} and {@link SpecialRecord}.
 */
public sealed interface DistinctId permits DistinctId.DistrictRecord, DistinctId.SpecialRecord {

  /** Nationality sign, always {@code "D"} for German plates. */
  String national();

  /** The plate code itself, e.g. {@code "A"}, {@code "BP"}, {@code "THW"}. */
  String code();

  /** The type of the plate, e.g. "Länder" for "B" or "Bund" for "THW". */
  String type();

  /**
   * Returns e.g. "D/ANG/ANGermünde" for the district plate "ANG". Returns e.g.
   * "D/THW/Bund" for the special plate "THW".
   *
   * @return display string for this UKZ
   */
  default String toDisplayString() {
    return national() + "/" + code() + "/" + type();
  }

  /** Regular district plate ("Unterscheidungszeichen") from kennzeichen.csv. */
  record DistrictRecord(@JsonProperty("Nationalitätszeichen") String national,
      @JsonProperty("Unterscheidungszeichen") String code, @JsonProperty("StadtOderKreis") String cityOrDistrict,
      @JsonProperty("Herleitung") String derivation, @JsonProperty("Bundesland.Name") String landName,
      @JsonProperty("Bundesland.Iso3166-2") String landIso, @JsonProperty("Fußnoten") String footnotes,
      @JsonProperty("Bemerkung") String remark) implements DistinctId {

    public String type() {
      return derivation;
    }

  }

  /** Special plate ("Sonderkennzeichen") from sonderkennzeichen.csv. */
  record SpecialRecord(@JsonProperty("Nationalitätszeichen") String national,
      @JsonProperty("Unterscheidungszeichen") String code, @JsonProperty("Typ") String type,
      @JsonProperty("Bedeutung") String meaning,
      @JsonProperty("Zulassungsbehörde") String authority) implements DistinctId {
  }
}
