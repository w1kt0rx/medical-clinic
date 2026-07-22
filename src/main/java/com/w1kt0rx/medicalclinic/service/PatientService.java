package com.w1kt0rx.medicalclinic.service;

import com.w1kt0rx.medicalclinic.command.CreatePatientCommand;
import com.w1kt0rx.medicalclinic.command.UpdatePatientCommand;
import com.w1kt0rx.medicalclinic.dto.PatientDto;
import com.w1kt0rx.medicalclinic.exception.EmailAlreadyInUseException;
import com.w1kt0rx.medicalclinic.exception.PatientNotFoundException;
import com.w1kt0rx.medicalclinic.mapper.PatientMapper;
import com.w1kt0rx.medicalclinic.model.Patient;
import com.w1kt0rx.medicalclinic.repository.PatientRepository;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository repository;
    private final PatientMapper mapper;

    public PatientDto create(CreatePatientCommand command) {
        if (repository.existsByEmail(command.email())) {
            throw new EmailAlreadyInUseException(String.format("Email - %s - jest już w uzyciu", command.email()), HttpStatus.CONFLICT);
        }
        Patient patient = mapper.toEntity(command);
        return mapper.toDto(repository.save(patient));
    }

    public void delete(String email) {
        repository.delete(getPatientByEmail(email));
    }

    public List<PatientDto> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    public PatientDto findByEmail(String email) {
        return mapper.toDto(getPatientByEmail(email));
    }

    public PatientDto update(String email, UpdatePatientCommand command) {
        Patient patient = getPatientByEmail(email);
        return mapper.toDto(patient.update(command));
    }

    public void updatePassword(String email, String password) {
        Patient patient = getPatientByEmail(email);
        patient.updatePassword(password);
    }

    private Patient getPatientByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(String.format("Nie znaleziono pacjeta o emailu: %s", email), HttpStatus.NOT_FOUND) );
    }
}
