package com.w1kt0rx.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class AddressNotFoundException extends MedicalClinicException {
    public AddressNotFoundException(String message, HttpStatus status) {
        super(message, status);
    }
}
