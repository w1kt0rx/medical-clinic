package com.w1kt0rx.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class ClinicNotFoundException extends MedicalClinicException {
    public ClinicNotFoundException(Long id) {
        super("Couldn't find clinic with id: " + id, HttpStatus.NOT_FOUND);
    }
}
