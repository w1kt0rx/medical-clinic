package com.w1kt0rx.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class VisitAlreadyExistsException extends MedicalClinicException {
    public VisitAlreadyExistsException(String message, HttpStatus status) {
        super(message, status);
    }
}
