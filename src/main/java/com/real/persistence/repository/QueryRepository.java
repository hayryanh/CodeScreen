package com.real.persistence.repository;

import lombok.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Interface for Table Records Read Operations
 */
public interface QueryRepository<T, ID> {

    Optional<T> findById(ID id);

    boolean existsById(ID id);

    Iterable<T> findAll();

    Iterable<T> findAllById(Iterable<ID> ids);

    List<T> findByQuerySpec(Map<ID, ID> querySpec);

    Optional<ID> findIdByIndex(@NonNull final ID index);
}
