package com.w1kt0rx.medicalclinic.command;

public record UpdateUserCommand(
        String firstName,
        String password,
        String lastName,
        String phoneNumber
) {
}
