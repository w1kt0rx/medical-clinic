package com.w1kt0rx.medicalclinic.command;

import java.time.LocalDate;

public record CreatePatientCommand(
    String email,
    String password,
    String idCardNo,
    String firstName,
    String lastName,
    String phoneNumber,
    LocalDate birthday
) {}
