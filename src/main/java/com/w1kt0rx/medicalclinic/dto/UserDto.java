package com.w1kt0rx.medicalclinic.dto;

public record UserDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        String phoneNumber
) {
}

