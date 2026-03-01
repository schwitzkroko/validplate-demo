package net.schwitzkroko.demo.validplate.distinct;

/**
 * Common contract for all German vehicle identification codes
 * ("Unterscheidungszeichen"), regardless of whether they are
 * regular district plates or special plates.
 */
public interface DistinctId {

    /** Nationality sign, always {@code "D"} for German plates. */
    String national();

    /** The plate code itself, e.g. {@code "A"}, {@code "BP"}, {@code "THW"}. */
    String code();
}
