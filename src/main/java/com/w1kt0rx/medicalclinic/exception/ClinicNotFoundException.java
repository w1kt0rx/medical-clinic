package com.w1kt0rx.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class ClinicNotFoundException extends MedicalClinicException {
    public ClinicNotFoundException(String message, HttpStatus status) {
        super(message, status);
    }
}
