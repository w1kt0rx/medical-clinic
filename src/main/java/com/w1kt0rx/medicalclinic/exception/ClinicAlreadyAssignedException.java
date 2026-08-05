package com.w1kt0rx.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class ClinicAlreadyAssignedException extends MedicalClinicException {
    public ClinicAlreadyAssignedException(String message, HttpStatus status) {
        super(message, status);
    }
}
