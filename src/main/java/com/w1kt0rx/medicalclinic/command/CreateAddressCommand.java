package com.w1kt0rx.medicalclinic.command;

public record CreateAddressCommand(
        String city,
        String postalCode,
        String street,
        String houseNumber
) {
}
