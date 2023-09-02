package com.real.service;

import com.real.matcher.Matcher;

import java.util.List;

public interface PersistenceService<T> {
    List<Matcher.IdMapping> match(List<T> records);
}
