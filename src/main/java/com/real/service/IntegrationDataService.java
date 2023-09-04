package com.real.service;

import com.real.matcher.Matcher;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;
import java.util.Set;

public interface IntegrationDataService<T> {

    Set<T> populateExternalData(Matcher.CsvStream externalDb);

    @Data
    @AllArgsConstructor
    class ExternalDbRecord {
        private String externalId;
        private String director;
        private String complexKey;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ExternalDbRecord)) return false;
            ExternalDbRecord that = (ExternalDbRecord) o;
            return Objects.equals(externalId, that.externalId) && Objects.equals(director, that.director) && Objects.equals(complexKey, that.complexKey);
        }

        @Override
        public int hashCode() {
            return Objects.hash(externalId, director, complexKey);
        }
    }
}
