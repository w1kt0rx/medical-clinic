package com.w1kt0rx.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class VisitNotFoundException extends MedicalClinicException {
    public VisitNotFoundException(String message, HttpStatus status) {
        super(message, status);
    }
}
