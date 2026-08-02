package com.w1kt0rx.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class DoctorNotFoundException extends MedicalClinicException {
    public DoctorNotFoundException(String message, HttpStatus status) {
        super(message, status);
    }
}
