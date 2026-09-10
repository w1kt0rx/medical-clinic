package com.w1kt0rx.medicalclinic.dto;

public record PageRequestDto(
        Integer page,
        Integer size,
        String sortBy,
        String direction
) {
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;
    public static final String DEFAULT_SORT_BY = "id";
    public static final String DEFAULT_DIRECTION = "asc";
}