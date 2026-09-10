package com.w1kt0rx.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class ClinicAlreadyExistsException extends MedicalClinicException {
    public ClinicAlreadyExistsException(String message, HttpStatus status) {
        super(message, status);
    }
}
