package com.w1kt0rx.medicalclinic.exception.handler;

import com.w1kt0rx.medicalclinic.dto.ErrorMessageDto;
import com.w1kt0rx.medicalclinic.exception.MedicalClinicException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MedicalClinicException.class)
    ResponseEntity<ErrorMessageDto> handleMedicalClinicException(MedicalClinicException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(new ErrorMessageDto(ex.getCreatedAt() + ":" + ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorMessageDto> handleUnexpectedException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessageDto(LocalDateTime.now() + ": Wystapil nieoczekiwany blad serwera"));
    }
}
