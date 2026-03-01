package net.schwitzkroko.demo.validplate.distinct;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.schwitzkroko.demo.validplate.distinct.district.DistrictRepo;
import net.schwitzkroko.demo.validplate.distinct.special.SpecialRepo;

@Slf4j
@ApplicationScoped
class DistinctIdServiceImpl implements DistinctIdService {

    @Inject
    DistrictRepo districtRepo;

    @Inject
    SpecialRepo specialRepo;

    @Override
    public DistinctId find(String code) {
        log.debug("find: code='{}'", code);

        DistinctId result = districtRepo.findByCode(code);
        if (result != null) {
            log.debug("find: found in district repo -> {}", result);
            return result;
        }

        result = specialRepo.findByCode(code);
        if (result != null) {
            log.debug("find: found in special repo -> {}", result);
        } else {
            log.debug("find: code='{}' not found in any repo", code);
        }
        return result;
    }
}