package com.real.persistence.repository.movie;

import com.real.persistence.core.Row;
import com.real.persistence.repository.GenericRepository;

/**
 * Actor Repository interface of actor table
 */
public interface ActorRepository extends GenericRepository<Row, String> {
    String ACTOR_TABLE = "actor";
}
