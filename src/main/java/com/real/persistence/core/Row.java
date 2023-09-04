package com.real.persistence.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The Row class represents as Table record
 */
@Data
@Builder
@AllArgsConstructor
public class Row {
    @NonNull
    private String rowId;
    @NonNull
    private ConcurrentLinkedQueue<ConcurrentHashMap<String, String>> rowValues;
    @NonNull
    private LocalDateTime createdAt;
    @NonNull
    private LocalDateTime updatedAt;
}
