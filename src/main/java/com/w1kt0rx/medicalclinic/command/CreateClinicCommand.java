package com.w1kt0rx.medicalclinic.command;

public record CreateClinicCommand(
        String name,
        String city,
        String postalCode,
        String street,
        String houseNumber
) {
}
