package com.w1kt0rx.medicalclinic.mapper;

import com.w1kt0rx.medicalclinic.dto.PageRequestDto;
import org.mapstruct.Mapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Mapper(componentModel = "spring")
public interface PageRequestMapper {

    default Pageable toPageable(PageRequestDto dto) {
        int page = dto.page() != null ? dto.page() : PageRequestDto.DEFAULT_PAGE;
        int size = dto.size() != null ? dto.size() : PageRequestDto.DEFAULT_SIZE;
        String sortBy = dto.sortBy() != null ? dto.sortBy() : PageRequestDto.DEFAULT_SORT_BY;
        Sort.Direction direction = "desc".equalsIgnoreCase(dto.direction())
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }
}