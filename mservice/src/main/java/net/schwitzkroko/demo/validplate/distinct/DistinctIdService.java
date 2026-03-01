package net.schwitzkroko.demo.validplate.distinct;

public interface DistinctIdService {

    /**
     * Looks up a code across both district and special plate registries.
     * Returns the matching {@link DistinctId}, or {@code null} if not found in either.
     */
    DistinctId find(String code);
}