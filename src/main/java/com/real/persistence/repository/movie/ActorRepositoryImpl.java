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

/**
 * Actor Repository Implementation  class
 */
public class ActorRepositoryImpl implements ActorRepository {

    private final PersistenceManager manager;

    /**
     * Non parameterized constructor.
     * Loading PersistenceManager instance and initializing actor Table instance
     */
    public ActorRepositoryImpl() {
        this.manager = PersistenceManager.getInstance();

        final Map<String, TableMetadata> actorTableMetaMap = new HashMap<>();
        actorTableMetaMap.put(CsvMetadata.MOVIE_ID.name(), TableMetadata.LONG);
        actorTableMetaMap.put(CsvMetadata.NAME.name(), TableMetadata.STRING);
        actorTableMetaMap.put(CsvMetadata.ROLE.name(), TableMetadata.STRING);

        manager.createTable(ACTOR_TABLE, actorTableMetaMap);
    }

    /**
     * Save entity record in table
     *
     * @param entity - Row record
     */
    @Override
    public void save(Row entity) {
        manager.addTableRow(ACTOR_TABLE, entity);
        populateIndexes(manager.loadTable(ACTOR_TABLE), entity);
    }

    /**
     * Save iterable entities in batch mode
     *
     * @param entities - Iterable<Row> entities
     */
    @Override
    public void saveAll(Iterable<Row> entities) {
        saveBatch(entities);
    }

    /**
     * Load available records count in table
     */
    @Override
    public long count() {
        return manager.loadTable(ACTOR_TABLE).count();
    }

    /**
     * Remove record from table by record ID
     *
     * @param id - Record ID
     */
    @Override
    public void deleteById(@NonNull final String id) {
        manager.loadTable(ACTOR_TABLE).deleteById(id);
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
        manager.loadTable(ACTOR_TABLE).deleteAll();
    }

    /**
     * Find table record by ID
     *
     * @param id - record ID
     * @return Optional<Row> - record instance
     */
    @Override
    public Optional<Row> findById(@NonNull final String id) {
        return Optional.ofNullable(manager.loadTable(ACTOR_TABLE).findById(id));
    }

    /**
     * Check availability of record in table by rowId
     *
     * @param id - String record ID
     * @return boolean - true if record exists in table by record ID
     */
    @Override
    public boolean existsById(@NonNull final String id) {
        return manager.loadTable(ACTOR_TABLE).existsById(id);
    }

    /**
     * Load all records in table
     *
     * @return Iterable<Row> - all records in table
     */
    @Override
    public Iterable<Row> findAll() {
        return manager.loadTable(ACTOR_TABLE).findAll();
    }

    /**
     * Search records by ID
     *
     * @param ids - iterable IDs
     * @return List<Row> - filtered records by ID
     */
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
     * Finding record's ID by table indexes
     *
     * @param index - String index key
     * @return Optional<String>
     */
    @Override
    public Optional<String> findIdByIndex(@NonNull String index) {
        return manager.loadTable(ACTOR_TABLE).findIdByIndex(index);
    }

    /**
     * Search records by query specification based on column -> value (C,V)
     *
     * @param querySpec - Specification Map with table column name as KEY and expected VALUE
     * @return List<Row> - filtered records by specified columns values
     */
    @Override
    public List<Row> findByQuerySpec(@NonNull Map<String, String> querySpec) {
        return manager.loadTable(ACTOR_TABLE).findByQuerySpec(querySpec);
    }

    /*
     * Populate table index by Director Name and record ID
     *
     * @param table - Table instance
     * @param row   - Row record
     */
    private void populateIndexes(@NonNull Table table, @NonNull Row row) {
        row.getRowValues().forEach(entity -> {
            final String role = entity.get(CsvMetadata.ROLE.name());
            if (role.equalsIgnoreCase(CsvMetadata.DIRECTOR.name())) {
                final String directorFullName = entity.get(CsvMetadata.NAME.name());
                if(directorFullName != null && directorFullName.isEmpty()) {
                    table.populateIndex(entity.get(CsvMetadata.NAME.name()), entity.get(CsvMetadata.MOVIE_ID.name()));
                }
            }
        });
    }
}
