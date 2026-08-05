package com.w1kt0rx.medicalclinic.dto;

import java.time.LocalDateTime;

public record VisitDto(
        Long id,
        LocalDateTime visitDate,
        DoctorDto doctor,
        PatientDto patient
) {
}
