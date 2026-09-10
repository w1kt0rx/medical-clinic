package com.w1kt0rx.medicalclinic.dto;

public record ClinicDto(
        Long id,
        String name,
        AddressDto address
) {
}
