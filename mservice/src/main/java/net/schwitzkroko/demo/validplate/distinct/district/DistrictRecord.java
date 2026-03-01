package net.schwitzkroko.demo.validplate.distinct.district;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.schwitzkroko.demo.validplate.distinct.DistinctId;

/**
 * Immutable record representing a district "Unterscheidungszeichen" entry from
 * the CSV.
 */
public record DistrictRecord(@JsonProperty("Nationalitätszeichen") String national,
    @JsonProperty("Unterscheidungszeichen") String code, @JsonProperty("StadtOderKreis") String cityOrDistrict,
    @JsonProperty("Herleitung") String derivation, @JsonProperty("Bundesland.Name") String landName,
    @JsonProperty("Bundesland.Iso3166-2") String landIso, @JsonProperty("Fußnoten") String footnotes,
    @JsonProperty("Bemerkung") String remark) implements DistinctId {
}
