package com.real.persistence.repository.movie;

import com.real.persistence.core.Row;
import com.real.persistence.repository.GenericRepository;

public interface MovieRepository extends GenericRepository<Row, String> {
    String MOVIE_TABLE = "movie";
}
