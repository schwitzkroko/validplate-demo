package net.schwitzkroko.demo.validplate.plate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PlateTestCaseUtil {

  public static List<PlateTestData> parse(String resource) throws IOException {
    CsvMapper mapper = new CsvMapper();
    mapper.enable(CsvParser.Feature.TRIM_SPACES);
    mapper.enable(CsvParser.Feature.ALLOW_TRAILING_COMMA);

    CsvSchema schema = CsvSchema.emptySchema().withHeader().withColumnSeparator(',').withNullValue("");

    try (InputStream is = PlateTestCaseUtil.class.getResourceAsStream(resource)) {
      return mapper.readerFor(PlateTestData.class).with(schema).<PlateTestData>readValues(is).readAll();
    }
  }

  public record PlateTestData(@JsonProperty("input") String input, @JsonProperty("httpCode") Integer httpCode,
      @JsonProperty("output") String output, @JsonProperty("remark") String remark) {
  }

}
