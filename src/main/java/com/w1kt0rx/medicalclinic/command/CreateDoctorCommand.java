package com.w1kt0rx.medicalclinic.command;

import java.util.Set;

public record CreateDoctorCommand(
        Long userId,
        String specialization,
        Set<Long> clinicIds
) {
}
