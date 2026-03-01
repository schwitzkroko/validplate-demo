package net.schwitzkroko.demo.validplate.distinct;

import net.schwitzkroko.demo.validplate.distinct.district.DistrictRecord;

public interface DistinctIdService {

	DistrictRecord find(String code);
}
