package com.w1kt0rx.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class VisitNotFoundException extends MedicalClinicException {
    public VisitNotFoundException(Long id) {
        super("Couldn't find visit with id: " + id, HttpStatus.NOT_FOUND);
    }
}
