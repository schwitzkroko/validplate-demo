package net.schwitzkroko.demo.validplate.distinct.special;

import java.util.List;
import java.util.Optional;

import net.schwitzkroko.demo.validplate.distinct.DistinctId;

public interface SpecialRepo {

  /**
   * Returns the first matching record for the given code, or {@code null} if
   * none.
   */
  Optional<DistinctId.SpecialRecord> findByCode(String code);

  /**
   * Returns all records for the given code (a code can appear under multiple
   * Typen).
   */
  List<DistinctId.SpecialRecord> findAllByCode(String code);
}
