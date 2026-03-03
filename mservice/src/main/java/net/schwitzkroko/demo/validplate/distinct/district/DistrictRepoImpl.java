package net.schwitzkroko.demo.validplate.distinct.district;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.schwitzkroko.demo.validplate.distinct.DistinctId;

class DistrictRepoImpl implements DistrictRepo {

  private final Map<String, DistinctId.DistrictRecord> byCode;

  DistrictRepoImpl(List<DistinctId.DistrictRecord> records) {
    this.byCode = records.stream().collect(Collectors.toMap(DistinctId.DistrictRecord::code, Function.identity()));
  }

  @Override
  public Optional<DistinctId.DistrictRecord> findByCode(String code) {
    return Optional.ofNullable(byCode.get(code));
  }
}
