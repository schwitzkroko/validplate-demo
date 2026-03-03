package net.schwitzkroko.demo.validplate.distinct;

import java.util.List;

public interface DistinctIdService {

  /**
   * Looks up a code across both district and special plate registries. Returns
   * all matching {@link DistinctId} entries, or an empty list if not found in
   * either.
   */
  List<DistinctId> find(String code);

  /**
   * Looks up all matching {@link DistinctId} entries for any of the given codes,
   * across both district and special plate registries.
   */
  List<DistinctId> findForAny(String... codes);
}
