package net.schwitzkroko.demo.validplate.distinct;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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
  public List<DistinctId> find(String code) {
    log.debug("find: code='{}'", code);

    List<DistinctId> result = Stream
        .concat(districtRepo.findByCode(code).stream(), specialRepo.findByCode(code).stream())
        .map(DistinctId.class::cast).toList();

    log.debug("find: code='{}' -> {} result(s)", code, result.size());
    return result;
  }

  @Override
  public List<DistinctId> findForAny(String... codes) {
    log.debug("findForAny: codes='{}'", Arrays.toString(codes));

    List<DistinctId> result = Stream.of(codes).flatMap(code -> find(code).stream()).toList();

    log.debug("findForAny: -> {} result(s)", result.size());
    return result;
  }

}
