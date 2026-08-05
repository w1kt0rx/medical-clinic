package com.w1kt0rx.medicalclinic.command;

import java.time.LocalDateTime;

public record CreateVisitCommand(
        Long doctorId,
        LocalDateTime visitDate
) {
}
