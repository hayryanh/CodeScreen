package com.real.persistence.core;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

/**
 * The Table class represents as schema table and supports CRUD operations
 */
@Data
@Builder
@Slf4j
public class Table {
    /*
     * Represents a predicate boolean valued function of two arguments
     */
    private static BiPredicate<Row, Map<String, String>> QUERY_SPEC = (r, m) -> {
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        r.getRowValues().forEach(mp -> m.forEach((k, v) -> atomicBoolean.set(mp.get(k).equals(v))));

        return atomicBoolean.get();
    };
    private final String tableName;
    private final Map<String, TableMetadata> tableMetadata;
    private ConcurrentHashMap<String, Row> rows;
    private Map<String, String> indexes;

    /**
     * Add record in the table
     *
     * @param row - insertable record
     */
    public void insertRow(@NonNull final Row row) {
        String rowId = row.getRowId();
        if (rows.containsKey(rowId)) {
            Row tmpRow = rows.get(rowId);
            tmpRow.getRowValues().addAll(row.getRowValues());
        } else {
            rows.put(rowId, row);
        }

        log.debug("Successfully added a row with ID {}", rowId);
    }

    /**
     *  Remove record from the table by ID
     *
     * @param rowId - table's record ID
     */
    public void deleteById(@NonNull final String rowId) {
        rows.remove(rowId);
        log.debug("Row ID {} successfully deleted", rowId);
    }

    /**
     * cleanup table
     */
    public void deleteAll() {
        rows.clear();
    }

    /**
     *  return count of records in table
     *
     * @return long - records count in table
     */
    public long count() {
        AtomicLong count = new AtomicLong();
        //Atomically adds the given value to the current value
        rows.values().forEach(row -> count.addAndGet(row.getRowValues().size()));

        return count.get();
    }

    /**
     * Select records by recordId.
     *
     * @param rowId - String record ID
     * @return Row - record by id
     */
    public Row findById(@NonNull final String rowId) {
        return rows.get(rowId);
    }

    /**
     * Check availability of record in table by rowId
     *
     * @param rowId - String record ID
     * @return boolean - true if record exists in table by record ID
     */
    public boolean existsById(@NonNull final String rowId) {
        return rows.containsKey(rowId);
    }

    /**
     * Load all records in table
     *
     * @return Iterable<Row> - all records in table
     */
    public Iterable<Row> findAll() {
        return rows.values();
    }

    /**
     * Search records by query specification based on column -> value (C,V)
     *
     * @param querySpec - Specification Map with table column name as KEY and expected VALUE
     * @return List<Row> - filtered records by specified columns values
     */
    public List<Row> findByQuerySpec(Map<String, String> querySpec) {
        return Flux.fromIterable(rows.values())
                .filter(row -> QUERY_SPEC.test(row, querySpec))
                .collect(Collectors.toUnmodifiableList()).
                block();
    }

    /**
     * Finding record's ID by table indexes
     *
     * @param key - String index key
     * @return Optional<String>
     */
    public Optional<String> findIdByIndex(@NonNull final String key) {
        return Optional.ofNullable(indexes.get(key));
    }

    /**
     * Table record indexing
     *
     * @param key - index
     * @param value - associated value
     */
    public void populateIndex(@NonNull final String key, @NonNull final String value) {
        indexes.put(key, value);
    }
}
