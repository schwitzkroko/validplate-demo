package net.schwitzkroko.demo.validplate.distinct.district;

import java.util.Optional;

import net.schwitzkroko.demo.validplate.distinct.DistinctId;

public interface DistrictRepo {

  Optional<DistinctId.DistrictRecord> findByCode(String code);
}
