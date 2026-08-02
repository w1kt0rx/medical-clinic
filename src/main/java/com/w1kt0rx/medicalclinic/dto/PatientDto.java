package com.w1kt0rx.medicalclinic.dto;

import java.time.LocalDate;

public record PatientDto(
        Long id,
        LocalDate birthday
) {
}
