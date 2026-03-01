package com.udacity.tdd.studentmanagmnt.dto;

import java.util.List;

public record PaginatedResponse<T>(
    List<T> content,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages,
    boolean isFirstPage,
    boolean isLastPage,
    boolean hasNextPage,
    boolean hasPreviousPage
) {}