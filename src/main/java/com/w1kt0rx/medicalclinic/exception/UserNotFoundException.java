package com.w1kt0rx.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends MedicalClinicException {

    public UserNotFoundException(Long id) {
        super("Couldn't find user with id: " + id, HttpStatus.NOT_FOUND);
    }
}
