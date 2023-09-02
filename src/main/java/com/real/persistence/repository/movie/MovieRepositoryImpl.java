package com.real.persistence.repository.movie;

import com.real.model.CsvMetadata;
import com.real.persistence.PersistenceManager;
import com.real.persistence.core.Row;
import com.real.persistence.core.Table;
import com.real.persistence.core.TableMetadata;
import lombok.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.*;
import java.util.stream.Collectors;

public class MovieRepositoryImpl implements MovieRepository {

    private final PersistenceManager manager;

    /**
     * Non parameterized constructor.
     * Loading PersistenceManager instance and initializing movie Table instance
     */
    public MovieRepositoryImpl() {
        this.manager = PersistenceManager.getInstance();

        final Map<String, TableMetadata> movieTableMetaMap = new HashMap<>();
        movieTableMetaMap.put(CsvMetadata.ID.name(), TableMetadata.LONG);
        movieTableMetaMap.put(CsvMetadata.TITLE.name(), TableMetadata.STRING);
        movieTableMetaMap.put(CsvMetadata.YEAR.name(), TableMetadata.DATE);
        manager.createTable(MOVIE_TABLE, movieTableMetaMap);
    }

    /**
     * Save entity record in table
     *
     * @param entity - Row record
     */
    @Override
    public void save(@NonNull Row entity) {
        manager.addTableRow(MOVIE_TABLE, entity);
        populateIndexes(manager.loadTable(MOVIE_TABLE), entity);
    }

    /**
     * Save iterable entities in batch mode
     *
     * @param entities - Iterable<Row> entities
     */
    @Override
    public void saveAll(@NonNull Iterable<Row> entities) {
        saveBatch(entities);
    }

    /**
     * Load available records count in table
     */
    @Override
    public long count() {
        return manager.loadTable(MOVIE_TABLE).count();
    }

    /**
     * Remove record from table by record ID
     *
     * @param id - Record ID
     */
    @Override
    public void deleteById(@NonNull final String id) {
        manager.loadTable(MOVIE_TABLE).deleteById(id);
    }

    /**
     * Remove all records by iterable IDs
     *
     * @param ids - Iterable ids
     */
    @Override
    public void deleteAllById(@NonNull Iterable<String> ids) {
        deleteBatchByIds(ids);
    }

    /**
     * Remove all available records from table
     */
    @Override
    public void deleteAll() {
        manager.loadTable(MOVIE_TABLE).deleteAll();
    }

    /**
     * Find table record by ID
     *
     * @param id - record ID
     * @return Optional<Row> - record instance
     */
    @Override
    public Optional<Row> findById(@NonNull final String id) {
        return Optional.ofNullable(manager.loadTable(MOVIE_TABLE).findById(id));
    }

    /**
     * Check availability of record in table by rowId
     *
     * @param id - String record ID
     * @return boolean - true if record exists in table by record ID
     */
    @Override
    public boolean existsById(@NonNull final String id) {
        return manager.loadTable(MOVIE_TABLE).existsById(id);
    }

    /**
     * Load all records in table
     *
     * @return Iterable<Row> - all records in table
     */
    @Override
    public Iterable<Row> findAll() {
        return manager.loadTable(MOVIE_TABLE).findAll();
    }

    @Override
    public Iterable<Row> findAllById(@NonNull Iterable<String> ids) {
        return Flux.fromIterable(ids)
                .filter(Objects::isNull)
                .parallel()
                .runOn(Schedulers.immediate())
                .map(this::findById)
                .sequential()
                .toStream()
                .flatMap(Optional::stream)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Search records by query specification based on column -> value (C,V)
     *
     * @param querySpec - Specification Map with table column name as KEY and expected VALUE
     * @return List<Row> - filtered records by specified columns values
     */
    @Override
    public List<Row> findByQuerySpec(@NonNull Map<String, String> querySpec) {
        return manager.loadTable(MOVIE_TABLE).findByQuerySpec(querySpec);
    }

    /**
     * Finding record's ID by table indexes
     *
     * @param index - String index key
     * @return Optional<String>
     */
    @Override
    public Optional<String> findIdByIndex(@NonNull final String index) {
        return manager.loadTable(MOVIE_TABLE).findIdByIndex(index);
    }

    /*
     * Populate table index by Director Name and record ID
     *
     * @param table - Table instance
     * @param row   - Row record
     */
    private void populateIndexes(@NonNull Table table, @NonNull Row row) {
        row.getRowValues().forEach(entity -> table.populateIndex(String.format("%s_%s",
                entity.get(CsvMetadata.TITLE.name()), entity.get(CsvMetadata.YEAR.name())), entity.get(CsvMetadata.ID.name())));
    }
}
