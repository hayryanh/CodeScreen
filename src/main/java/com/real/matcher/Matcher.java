package com.real.matcher;

import com.real.service.IntegrationDataService;
import com.real.service.XBoxDataService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public interface Matcher {

  class IdMapping {

    private final int internalId;
    private final String externalId;

    public IdMapping(int internalId, String externalId) {
      this.internalId = internalId;
      this.externalId = externalId;
    }

    public int getInternalId() {
      return internalId;
    }

    public String getExternalId() {
      return externalId;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      IdMapping idMapping = (IdMapping) o;
      return internalId == idMapping.internalId && Objects.equals(externalId, idMapping.externalId);
    }

    @Override
    public int hashCode() {
      return Objects.hash(internalId, externalId);
    }
  }

  class CsvStream {

    private final String headerRow;
    private final Stream<String> dataRows;

    public CsvStream(String headerRow, Stream<String> dataRows) {
      this.headerRow = headerRow;
      this.dataRows = dataRows;
    }

    public String getHeaderRow() {
      return headerRow;
    }

    public Stream<String> getDataRows() {
      return dataRows;
    }
  }

  enum DatabaseType {
    XBOX, GOOGLE_PLAY, VUDU, AMAZON_INSTANT
  }

  Map<DatabaseType, IntegrationDataService<IntegrationDataService.ExternalDbRecord>> DATA_SERVICE_FACTORY = new HashMap<>(){{
    put(DatabaseType.XBOX, new XBoxDataService());
  }};

  List<IdMapping> match(DatabaseType databaseType, CsvStream externalDb);
}
