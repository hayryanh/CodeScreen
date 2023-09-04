package com.real.service;

import com.real.matcher.Matcher;
import com.real.model.CsvMetadata;
import com.real.util.CsvUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.real.util.CsvUtil.tableColumnsIndexMap;
import static com.real.util.DateUtil.getDateYear;

@Slf4j
public class XBoxDataService implements IntegrationDataService<IntegrationDataService.ExternalDbRecord> {

    /**
     * Parse Xbox CSV data and build ExternalDbRecord instance
     *
     * @param externalDb - CsvStream
     * @return List<ExternalDbRecord> - List of external DB records
     */
    @Override
    public Set<ExternalDbRecord> populateExternalData(Matcher.CsvStream externalDb) {
        Map<CsvMetadata, Integer> moviesColumnIndexMap = tableColumnsIndexMap(externalDb);

        return externalDb.getDataRows().map(row -> buildQuerySpec(row, moviesColumnIndexMap))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toUnmodifiableSet());
    }

    private Optional<ExternalDbRecord> buildQuerySpec(String row, Map<CsvMetadata, Integer> moviesColumnIndexMap) {
        try {
            String[] values = CsvUtil.parse(row);
            String complexKey = String.format("%s_%s", values[moviesColumnIndexMap.get(CsvMetadata.TITLE)],
                    getDateYear(values[moviesColumnIndexMap.get(CsvMetadata.ORIGINALRELEASEDATE)], "M/d/yyyy hh:mm:ss a"));

            return Optional.of(new ExternalDbRecord(values[moviesColumnIndexMap.get(CsvMetadata.MEDIAID)],
                    values[moviesColumnIndexMap.get(CsvMetadata.DIRECTOR)], complexKey));

        } catch (IOException e) {
            log.error("Malformed CSV Row [ " + row + " ]");
        }

        return Optional.empty();
    }
}
