package com.real.persistence.core;

import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Database Schema class
 *
 * @author Hayk Hayryan
 */
@Data
@Slf4j
public class Schema {
    private String name;
    private Map<String, Table> tableMap;

    /**
     * Parameterised constructor for initializing schema with name
     *
     * @param name - Schema name
     */
    public Schema(String name) {
        this.name = name;
        this.tableMap = new ConcurrentHashMap<>();
    }

    /**
     * Table initialization as database schema member
     *
     * @param tableName - unique name of table
     * @param tableMetadata - table columns types metadata information
     */
    public void createTable(@NonNull final String tableName, Map<String, TableMetadata> tableMetadata) {
        if (tableMap.containsKey(tableName)) {
            log.error("A table already exists with the given name {}", tableName);
        } else {
            tableMap.put(tableName, Table.builder()
                    .tableName(tableName)
                    .tableMetadata(tableMetadata)
                    .rows(new ConcurrentHashMap<>())
                    .indexes(new HashMap<>())
                    .build());
            log.debug("Table {} successfully created", tableName);
        }
    }

    /**
     * Removing table from schema
     *
     * @param tableName - unique name of table
     */
    public void dropTable(@NonNull final String tableName) {
        if (tableMap.containsKey(tableName)) {
            tableMap.remove(tableName);
            log.debug("Table {} successfully dropped", tableMap);
        } else {
            log.debug("Table {} not exists", tableName);
        }
    }
}
