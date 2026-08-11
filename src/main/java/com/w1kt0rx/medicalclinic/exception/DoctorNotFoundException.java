package com.w1kt0rx.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class DoctorNotFoundException extends MedicalClinicException {
    public DoctorNotFoundException(Long id) {
        super("Couldn't find doctor with id: " + id, HttpStatus.NOT_FOUND);
    }
}
