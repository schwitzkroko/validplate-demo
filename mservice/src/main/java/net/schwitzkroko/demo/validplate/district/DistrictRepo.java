package net.schwitzkroko.demo.validplate.district;

public interface DistrictRepo {

	DistrictRecord findByCode(String code);

}