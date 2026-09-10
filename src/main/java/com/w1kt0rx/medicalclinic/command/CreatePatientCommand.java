package com.w1kt0rx.medicalclinic.command;

import java.time.LocalDate;

public record CreatePatientCommand(
        Long userId,
        String idCardNo,
        LocalDate birthday
) {
}
