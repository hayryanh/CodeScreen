package com.real.service;

import com.real.matcher.Matcher;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

public interface IntegrationDataService<T> {

    List<T> populateExternalData(Matcher.CsvStream externalDb);

    @Data
    @AllArgsConstructor
    class ExternalDbRecord {
        private String externalId;
        private String director;
        private String complexKey;
    }
}
