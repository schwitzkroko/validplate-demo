package net.schwitzkroko.demo.validplate.distinct.special;

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
public class SpecialRepoConfig {

  static final String CSV_RESOURCE = "/distinct/sonderkennzeichen.csv";

  @Produces
  @ApplicationScoped
  @Startup
  SpecialRepo specialRepo() throws IOException {
    log.info("Loading special plate data from CSV...");
    var records = parse(CSV_RESOURCE);
    log.info("Loaded {} special records.", records.size());
    return new SpecialRepoImpl(records);
  }

  static List<DistinctId.SpecialRecord> parse(String csvResource) throws IOException {
    CsvMapper mapper = new CsvMapper();
    CsvSchema schema = CsvSchema.emptySchema().withHeader().withColumnSeparator(',').withQuoteChar('"');

    try (InputStream is = SpecialRepoConfig.class.getResourceAsStream(csvResource)) {
      return mapper.readerFor(DistinctId.SpecialRecord.class).with(schema).<DistinctId.SpecialRecord>readValues(is)
          .readAll();
    }
  }
}
