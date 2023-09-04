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
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
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
    public List<Matcher.IdMapping> match(Set<IntegrationDataService.ExternalDbRecord> records) {
        List<Matcher.IdMapping> mappings = new ArrayList<>();

        records.forEach(record -> {
            Optional<String> movieId = movieRepository.findIdByIndex(record.getComplexKey());
            movieId.ifPresent(s -> actorRepository.findIdByIndex(s)
                    .filter(director -> record.getDirector().equalsIgnoreCase(director))
                    .map(director -> mappings.add(new Matcher.IdMapping(Integer.parseInt(s), record.getExternalId()))));
        });

        return new ArrayList<>(mappings);
    }

    /**
     * Populate internal database from CSV stream records
     *
     * @param movieDb            - CsvStream
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
        ConcurrentLinkedQueue<ConcurrentHashMap<String, String>> valueList = new ConcurrentLinkedQueue<>();
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
        ConcurrentLinkedQueue<ConcurrentHashMap<String, String>> valueList = new ConcurrentLinkedQueue<>();
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

        Flux.fromStream(movieDb.getDataRows())
                .parallel()
                .runOn(Schedulers.parallel())
                .map(row -> MOVIE_ROW_BUILDER.apply(row, moviesColumnIndexMap))
                .subscribe(tableRow -> tableRow.ifPresent(movieRepository::save));
    }

    private void initializeActorTable(Matcher.CsvStream actorAndDirectorDb) {
        Map<CsvMetadata, Integer> actorsColumnIndexMap = tableColumnsIndexMap(actorAndDirectorDb);

        Flux.fromStream(actorAndDirectorDb.getDataRows())
                .parallel()
                .runOn(Schedulers.parallel())
                .map(row -> ACTOR_ROW_BUILDER.apply(row, actorsColumnIndexMap))
                .subscribe(tableRow -> tableRow.ifPresent(actorRepository::save));
    }
}
