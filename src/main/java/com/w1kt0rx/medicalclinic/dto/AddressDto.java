package com.w1kt0rx.medicalclinic.dto;

public record AddressDto(
        Long id,
        String city,
        String postalCode,
        String street,
        String houseNumber
) {
}
