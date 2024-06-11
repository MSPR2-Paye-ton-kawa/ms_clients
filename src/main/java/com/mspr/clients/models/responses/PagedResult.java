package com.mspr.clients.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PagedResult<T>(
        Iterable<T> data,
        long totalElements,
        int pageNumber,
        int totalPages,
        @JsonProperty("isFirst") boolean isFirst,
        @JsonProperty("isLast") boolean isLast,
        @JsonProperty("hasNext") boolean hasNext,
        @JsonProperty("hasPrevious") boolean hasPrevious
) {
}
