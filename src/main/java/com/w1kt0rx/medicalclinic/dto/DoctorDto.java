package com.w1kt0rx.medicalclinic.dto;


import java.util.Set;

public record DoctorDto(
        Long id,
        String specialization,
        UserDto user,
        Set<ClinicDto> clinics
) {
}
