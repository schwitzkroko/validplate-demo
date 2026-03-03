package net.schwitzkroko.demo.validplate.distinct.district;

import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import lombok.extern.slf4j.Slf4j;
import net.schwitzkroko.demo.validplate.distinct.DistinctId;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

@Slf4j
@ApplicationScoped
public class DistrictRepoConfig {

  static final String CSV_RESOURCE = "/distinct/kennzeichen.csv";

  @Produces
  @ApplicationScoped
  @Startup
  DistrictRepo districtRepo() throws IOException {
    log.info("Loading district data from CSV...");
    var records = parse(CSV_RESOURCE);
    log.info("Loaded {} district records.", records.size());
    return new DistrictRepoImpl(records);
  }

  static List<DistinctId.DistrictRecord> parse(String csvResource) throws IOException {
    CsvMapper mapper = new CsvMapper();
    CsvSchema schema = CsvSchema.emptySchema().withHeader().withColumnSeparator(',');

    try (InputStream is = DistrictRepoConfig.class.getResourceAsStream(csvResource)) {
      return mapper.readerFor(DistinctId.DistrictRecord.class).with(schema).<DistinctId.DistrictRecord>readValues(is)
          .readAll();
    }
  }
}
