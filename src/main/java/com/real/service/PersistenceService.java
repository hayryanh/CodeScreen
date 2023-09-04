package com.real.service;

import com.real.matcher.Matcher;

import java.util.List;
import java.util.Set;

public interface PersistenceService<T> {
    List<Matcher.IdMapping> match(Set<T> records);
}
