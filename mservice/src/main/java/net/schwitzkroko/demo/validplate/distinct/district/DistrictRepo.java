package net.schwitzkroko.demo.validplate.distinct.district;

public interface DistrictRepo {

    DistrictRecord findByCode(String code);
}
