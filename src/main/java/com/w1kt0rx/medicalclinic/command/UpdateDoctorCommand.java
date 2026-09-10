package com.w1kt0rx.medicalclinic.command;

import java.util.Set;

public record UpdateDoctorCommand(
        String specialization,
        Set<Long> clinicIds
) {
}
