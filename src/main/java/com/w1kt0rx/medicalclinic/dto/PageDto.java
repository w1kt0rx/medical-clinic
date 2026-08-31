package com.w1kt0rx.medicalclinic.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageDto<T>(
        List<T> content,
        Integer page,
        Integer size,
        Long totalElements,
        Integer totalPages
) {
    public static <T> PageDto<T> from(Page<T> page) {
        return new PageDto<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
