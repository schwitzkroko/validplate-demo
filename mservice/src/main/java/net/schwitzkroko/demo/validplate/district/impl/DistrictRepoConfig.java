package net.schwitzkroko.demo.validplate.district.impl;

import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import lombok.extern.slf4j.Slf4j;
import net.schwitzkroko.demo.validplate.district.DistrictRepo;

import java.io.IOException;

@Slf4j
@ApplicationScoped
public class DistrictRepoConfig {

  static final String CSV_RESOURCE = "/distinct/kennzeichen.csv";

  @Produces
  @ApplicationScoped
  @Startup
  DistrictRepo districtRepo() throws IOException {
    log.info("Loading district data from CSV...");
    var records = DistrictCsvUtil.parse(CSV_RESOURCE);
    log.info("Loaded {} district records.", records.size());
    return new DistrictRepoImpl(records);
  }
}
