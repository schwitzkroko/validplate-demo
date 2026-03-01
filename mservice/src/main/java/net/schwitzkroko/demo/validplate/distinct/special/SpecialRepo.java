package net.schwitzkroko.demo.validplate.distinct.special;

import java.util.List;

public interface SpecialRepo {

  /**
   * Returns the first matching record for the given code, or {@code null} if
   * none.
   */
  SpecialRecord findByCode(String code);

  /**
   * Returns all records for the given code (a code can appear under multiple
   * Typen).
   */
  List<SpecialRecord> findAllByCode(String code);
}
