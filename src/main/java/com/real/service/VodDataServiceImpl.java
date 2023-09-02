package com.real.service;

import com.real.matcher.Matcher;
import com.real.model.CsvMetadata;
import com.real.persistence.core.Row;
import com.real.persistence.repository.movie.ActorRepository;
import com.real.persistence.repository.movie.ActorRepositoryImpl;
import com.real.persistence.repository.movie.MovieRepository;
import com.real.persistence.repository.movie.MovieRepositoryImpl;
import com.real.util.CsvUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

import static com.real.util.CsvUtil.tableColumnsIndexMap;


@Slf4j
public class VodDataServiceImpl implements PersistenceService<IntegrationDataService.ExternalDbRecord> {

    /*
     * Function with two arguments for parsing movie csv records and building Row record of Movie table
     */
    private static final BiFunction<String, Map<CsvMetadata, Integer>, Optional<Row>> MOVIE_ROW_BUILDER = (row, columns) ->
    {
        try {
            return buildMovieRow(columns, row);
        } catch (IOException e) {
            log.error("Malformed CSV Row [ " + row + " ]");
        }
        return Optional.empty();
    };

    /*
     * Function with two arguments for parsing actors_and_directors csv records and building Row record of Movie table
     */
    private static final BiFunction<String, Map<CsvMetadata, Integer>, Optional<Row>> ACTOR_ROW_BUILDER = (row, columns) ->
    {
        try {
            return buildActorRow(columns, row);
        } catch (IOException e) {
            log.error("Malformed CSV Row [ " + row + " ]");
        }
        return Optional.empty();
    };

    private final MovieRepository movieRepository;

    private final ActorRepository actorRepository;

    /**
     * Non parameterized constructor.
     * Initializing MovieRepository and ActorRepository implementations instances.
     */
    public VodDataServiceImpl() {
        movieRepository = new MovieRepositoryImpl();
        actorRepository = new ActorRepositoryImpl();

    }

    /**
     * Finding records from internal database according to external data and building matcher mapping
     *
     * @param records - external data records
     * @return List<Matcher.IdMapping> - list of mappings
     */
    @Override
    public List<Matcher.IdMapping> match(List<IntegrationDataService.ExternalDbRecord> records) {
        Set<Matcher.IdMapping> mappings = new HashSet<>();

        records.forEach(record -> {
            Optional<String> result = movieRepository.findIdByIndex(record.getComplexKey());
            result.ifPresent(rt -> mappings.add(new Matcher.IdMapping(Integer.parseInt(rt), record.getExternalId())));
        });

        return new ArrayList<>(mappings);
    }

    /**
     * Populate internal database from CSV stream records
     *
     * @param movieDb - CsvStream
     * @param actorAndDirectorDb - CsvStream
     */
    public void populateDatabase(Matcher.CsvStream movieDb, Matcher.CsvStream actorAndDirectorDb) {
        initializeMovieTable(movieDb);
        initializeActorTable(actorAndDirectorDb);
    }

    private static Optional<Row> buildMovieRow(Map<CsvMetadata, Integer> columns, String row) throws IOException {
        String[] values = CsvUtil.parse(row);
        ConcurrentHashMap<String, String> rowColumnValues = new ConcurrentHashMap<>();
        rowColumnValues.put(CsvMetadata.ID.name(), values[columns.get(CsvMetadata.ID)].trim());
        rowColumnValues.put(CsvMetadata.TITLE.name(), values[columns.get(CsvMetadata.TITLE)].trim());
        rowColumnValues.put(CsvMetadata.YEAR.name(), values[columns.get(CsvMetadata.YEAR)].trim());
        LinkedList<ConcurrentHashMap<String, String>> valueList = new LinkedList<>();
        valueList.add(rowColumnValues);

        return Optional.of(Row.builder()
                .rowId(rowColumnValues.get(CsvMetadata.ID.name()))
                .rowValues(valueList)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
    }

    private static Optional<Row> buildActorRow(Map<CsvMetadata, Integer> columns, String row) throws IOException {
        String[] values = CsvUtil.parse(row);
        ConcurrentHashMap<String, String> rowColumnValues = new ConcurrentHashMap<>();
        rowColumnValues.put(CsvMetadata.MOVIE_ID.name(), values[columns.get(CsvMetadata.MOVIE_ID)].trim());
        rowColumnValues.put(CsvMetadata.NAME.name(), values[columns.get(CsvMetadata.NAME)].trim());
        rowColumnValues.put(CsvMetadata.ROLE.name(), values[columns.get(CsvMetadata.ROLE)].trim());
        LinkedList<ConcurrentHashMap<String, String>> valueList = new LinkedList<>();
        valueList.add(rowColumnValues);

        return Optional.of(Row.builder()
                .rowId(rowColumnValues.get(CsvMetadata.MOVIE_ID.name()))
                .rowValues(valueList)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
    }

    private void initializeMovieTable(Matcher.CsvStream movieDb) {
        Map<CsvMetadata, Integer> moviesColumnIndexMap = tableColumnsIndexMap(movieDb);

        movieDb.getDataRows().forEach(row -> {
            Optional<Row> tableRow = MOVIE_ROW_BUILDER.apply(row, moviesColumnIndexMap);
            tableRow.ifPresent(movieRepository::save);
        });
    }

    private void initializeActorTable(Matcher.CsvStream actorAndDirectorDb) {
        Map<CsvMetadata, Integer> moviesColumnIndexMap = tableColumnsIndexMap(actorAndDirectorDb);

        actorAndDirectorDb.getDataRows().forEach(row -> {
            Optional<Row> tableRow = ACTOR_ROW_BUILDER.apply(row, moviesColumnIndexMap);
            tableRow.ifPresent(actorRepository::save);
        });
    }
}
