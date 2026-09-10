package com.w1kt0rx.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class VisitAlreadyReservedException extends MedicalClinicException {
    public VisitAlreadyReservedException(String message, HttpStatus status) {
        super(message, status);
    }
}
