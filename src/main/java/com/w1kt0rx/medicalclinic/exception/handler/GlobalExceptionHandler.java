package com.w1kt0rx.medicalclinic.exception.handler;

import com.w1kt0rx.medicalclinic.dto.ErrorMessageDto;
import com.w1kt0rx.medicalclinic.exception.MedicalClinicException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MedicalClinicException.class)
    ResponseEntity<ErrorMessageDto> handleMedicalClinicException(MedicalClinicException ex) {
        log.warn("Handled business exception: {} - {}", ex.getClass().getSimpleName(), ex.getMessage());
        String message = LocalDateTime.now() + ": " + ex.getMessage();
        return ResponseEntity.status(ex.getHttpStatus()).body(new ErrorMessageDto(message));
    }
    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorMessageDto> handleUnexpectedException(Exception ex) {
        String message = LocalDateTime.now() + ": Internal server error";
        return ResponseEntity.internalServerError().body(new ErrorMessageDto(message));
    }
}
