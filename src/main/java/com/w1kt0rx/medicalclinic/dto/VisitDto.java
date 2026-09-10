package com.w1kt0rx.medicalclinic.dto;

import java.time.LocalDateTime;

public record VisitDto(
        Long id,
        LocalDateTime startDate,
        LocalDateTime finishDate,
        DoctorDto doctor,
        PatientDto patient
) {
}
