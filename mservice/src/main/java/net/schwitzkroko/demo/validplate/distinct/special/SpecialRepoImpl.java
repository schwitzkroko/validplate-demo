package net.schwitzkroko.demo.validplate.distinct.special;

import net.schwitzkroko.demo.validplate.distinct.DistinctId;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

class SpecialRepoImpl implements SpecialRepo {

  private final Map<String, List<DistinctId.SpecialRecord>> byCode;

  SpecialRepoImpl(List<DistinctId.SpecialRecord> records) {
    this.byCode = records.stream().collect(Collectors.groupingBy(DistinctId.SpecialRecord::code));
  }

  @Override
  public Optional<DistinctId.SpecialRecord> findByCode(String code) {
    List<DistinctId.SpecialRecord> hits = byCode.get(code);
    return Optional.ofNullable(hits).map(l -> l.get(0));
  }

  @Override
  public List<DistinctId.SpecialRecord> findAllByCode(String code) {
    return byCode.getOrDefault(code, List.of());
  }
}
