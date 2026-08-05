package com.w1kt0rx.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class IllegalDateException extends MedicalClinicException {
    public IllegalDateException(String message, HttpStatus status) {
        super(message, status);
    }
}
