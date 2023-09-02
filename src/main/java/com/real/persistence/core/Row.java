package com.real.persistence.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  The Row class represents as Table record
 */
@Data
@Builder
@AllArgsConstructor
public class Row {
    @NonNull
    private String rowId;
    @NonNull
    private LinkedList<ConcurrentHashMap<String, String>> rowValues;
    @NonNull
    private LocalDateTime createdAt;
    @NonNull
    private LocalDateTime updatedAt;
}
