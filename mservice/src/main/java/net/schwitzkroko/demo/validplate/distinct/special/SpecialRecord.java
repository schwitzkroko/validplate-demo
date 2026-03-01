package net.schwitzkroko.demo.validplate.distinct.special;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Immutable record representing a "Sonderkennzeichen" entry from the CSV.
 */
public record SpecialRecord(
        @JsonProperty("Nationalitätszeichen")    String national,
        @JsonProperty("Unterscheidungszeichen")  String code,
        @JsonProperty("Typ")                     String type,
        @JsonProperty("Bedeutung")               String meaning,
        @JsonProperty("Zulassungsbehörde")       String authority
) {}
