package net.schwitzkroko.demo.validplate.distinct.special.impl;

import net.schwitzkroko.demo.validplate.distinct.special.SpecialRecord;
import net.schwitzkroko.demo.validplate.distinct.special.SpecialRepo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class SpecialRepoImpl implements SpecialRepo {

    private final Map<String, List<SpecialRecord>> byCode;

    SpecialRepoImpl(List<SpecialRecord> records) {
        this.byCode = records.stream()
                .collect(Collectors.groupingBy(SpecialRecord::code));
    }

    @Override
    public SpecialRecord findByCode(String code) {
        List<SpecialRecord> hits = byCode.get(code);
        return hits != null ? hits.get(0) : null;
    }

    @Override
    public List<SpecialRecord> findAllByCode(String code) {
        return byCode.getOrDefault(code, List.of());
    }
}
