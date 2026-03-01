package net.schwitzkroko.demo.validplate.distinct;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.schwitzkroko.demo.validplate.distinct.district.DistrictRecord;
import net.schwitzkroko.demo.validplate.distinct.district.DistrictRepo;

@Slf4j
@ApplicationScoped
class DistinctIdServiceImpl implements DistinctIdService {

    @Inject
    DistrictRepo districtRepo;

    @Override
    public DistrictRecord find(String code) {
        log.debug("find: code='{}'", code);
        return districtRepo.findByCode(code);
    }
}
