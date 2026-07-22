package com.w1kt0rx.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyInUseException extends MedicalClinicException {

    public EmailAlreadyInUseException(String message, HttpStatus status) {
        super(message, status);
    }
}
