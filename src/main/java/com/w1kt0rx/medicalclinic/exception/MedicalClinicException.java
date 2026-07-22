package com.w1kt0rx.medicalclinic.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public abstract class MedicalClinicException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final LocalDateTime createdAt;

    public MedicalClinicException(String message, HttpStatus status) {
        super(message);
        this.httpStatus = status;
        this.createdAt = LocalDateTime.now();
    }
}
