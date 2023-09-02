package com.real.matcher;

import com.real.service.IntegrationDataService;
import com.real.service.VodDataServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class MatcherImpl implements Matcher {

    private VodDataServiceImpl vodService;

    public MatcherImpl(CsvStream movieDb, CsvStream actorAndDirectorDb) {
        log.info("importing database");
        initializeInternalDatabase(movieDb, actorAndDirectorDb);

        log.info("database imported");
    }

    @Override
    public List<IdMapping> match(DatabaseType databaseType, CsvStream externalDb) {
        List<IntegrationDataService.ExternalDbRecord> records = DATA_SERVICE_FACTORY.get(databaseType).populateExternalData(externalDb);
        return vodService.match(records);
    }

    private void initializeInternalDatabase(CsvStream movieDb, CsvStream actorAndDirectorDb) {
        vodService = new VodDataServiceImpl();
        vodService.populateDatabase(movieDb, actorAndDirectorDb);
    }
}
