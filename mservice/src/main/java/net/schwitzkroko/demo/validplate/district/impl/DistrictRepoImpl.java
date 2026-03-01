package net.schwitzkroko.demo.validplate.district.impl;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.schwitzkroko.demo.validplate.district.DistrictRecord;
import net.schwitzkroko.demo.validplate.district.DistrictRepo;

class DistrictRepoImpl implements DistrictRepo {

    private final Map<String, DistrictRecord> byCode;

    DistrictRepoImpl(List<DistrictRecord> records) {
        this.byCode = records.stream()
                .collect(Collectors.toMap(DistrictRecord::code, Function.identity()));
    }

    @Override
    public DistrictRecord findByCode(String code) {
        return byCode.get(code);
    }
}
