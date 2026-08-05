package com.w1kt0rx.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class ClinicNotAssignedException extends MedicalClinicException {
    public ClinicNotAssignedException(String message, HttpStatus status) {
        super(message, status);
    }
}
