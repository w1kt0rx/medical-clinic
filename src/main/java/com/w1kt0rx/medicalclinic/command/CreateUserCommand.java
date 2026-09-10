package com.w1kt0rx.medicalclinic.command;

public record CreateUserCommand(
        String email,
        String password,
        String firstName,
        String lastName,
        String phoneNumber
) {
}
