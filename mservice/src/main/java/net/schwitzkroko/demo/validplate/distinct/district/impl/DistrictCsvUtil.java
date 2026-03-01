package net.schwitzkroko.demo.validplate.distinct.district.impl;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import net.schwitzkroko.demo.validplate.distinct.district.DistrictRecord;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

class DistrictCsvUtil {

    static List<DistrictRecord> parse(String csvResource) throws IOException {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema()
                .withHeader()
                .withColumnSeparator(',');

        try (InputStream is = DistrictCsvUtil.class.getResourceAsStream(csvResource)) {
            return mapper
                    .readerFor(DistrictRecord.class)
                    .with(schema)
                    .<DistrictRecord>readValues(is)
                    .readAll();
        }
    }
}
