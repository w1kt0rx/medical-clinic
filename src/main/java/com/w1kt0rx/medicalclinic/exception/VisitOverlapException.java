package com.w1kt0rx.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class VisitOverlapException extends MedicalClinicException {
    public VisitOverlapException(String message, HttpStatus status) {
        super(message, status);
    }
}
