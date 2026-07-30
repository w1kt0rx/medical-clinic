package com.w1kt0rx.medicalclinic.command;

import java.time.LocalDate;

public record UpdatePatientCommand(
        String idCardNo,
        LocalDate birthday
) {
}
