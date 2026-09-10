package com.w1kt0rx.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class PatientNotFoundException extends MedicalClinicException {

    public PatientNotFoundException(Long id) {
        super("Couldn't find patient with id: " + id, HttpStatus.NOT_FOUND);
    }
}
