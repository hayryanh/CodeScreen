package com.real.persistence;

import com.real.persistence.core.Row;
import com.real.persistence.core.Schema;
import com.real.persistence.core.Table;
import com.real.persistence.core.TableMetadata;
import lombok.NonNull;

import java.util.Map;

/**
 * Singleton class for Persistence Sessions and Operations Manager
 */
public final class PersistenceManager {

    private final Schema SCHEMA;

    private static PersistenceManager instance;

    /**
     * Non parameterized constructor.
     * Instantiating the singleton and creating a new schema named MOVIE_DATA.
     */
    private PersistenceManager() {
        SCHEMA = new Schema("MOVIE_DATA");
    }

    /**
     * Load persistence manager singleton instance
     *
     * @return PersistenceManager - singleton instance
     */
    public static PersistenceManager getInstance() {
        if (instance == null) {
            instance = new PersistenceManager();
        }

        return instance;
    }

    public void createTable(@NonNull final String tableName, @NonNull final Map<String, TableMetadata> tableMeta) {
        SCHEMA.createTable(tableName, tableMeta);
    }

    public void dropTable(@NonNull final String tableName) {
        SCHEMA.dropTable(tableName);
    }

    public void addTableRow(@NonNull final String tableName, @NonNull final Row row) {
        if (SCHEMA.getTableMap().containsKey(tableName)) {
            Table table = SCHEMA.getTableMap().get(tableName);
            table.insertRow(row);
        }
    }

    public Table loadTable(@NonNull final String tableName) {
        return SCHEMA.getTableMap().get(tableName);
    }

    public long count(@NonNull final String tableName) {
        return SCHEMA.getTableMap().get(tableName).count();
    }
}
