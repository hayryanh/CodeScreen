package com.real.persistence.repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * Generic Repository CRUD Interface
 */
public interface GenericRepository<T, ID> extends QueryRepository<T, ID> {

    void save(T entity);

    void saveAll(Iterable<T> entities);

    long count();

    void deleteById(ID id);

    void deleteAllById(Iterable<ID> ids);

    void deleteAll();

    /**
     * Records saving asynchronous batch operation
     *
     * @param entities - Iterable entities
     */
    default void saveBatch(Iterable<T> entities) {
        Flux.fromIterable(entities)
                .filter(Objects::nonNull)
                .parallel()
                .flatMap(entity -> Mono.fromRunnable(() -> save(entity)))
                .subscribe();
    }

    /**
     * Records deleting by ID asynchronous batch operation
     *
     * @param ids - Iterable ids
     */
    default void deleteBatchByIds(Iterable<ID> ids) {
        Flux.fromIterable(ids)
                .filter(Objects::nonNull)
                .parallel()
                .flatMap(id -> Mono.fromRunnable(() -> deleteById(id)))
                .subscribe();
    }
}
