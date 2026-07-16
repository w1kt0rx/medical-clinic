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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository repository;

    public PatientDto create(CreatePatientCommand command) {
        if (repository.existsByEmail(command.email())) {
            throw new EmailAlreadyInUseException("Email jest już zajęty");
        }

        Patient patient = PatientMapper.toEntity(command);

        return PatientMapper.toDto(repository.save(patient));
    }

    public void delete(String email) {
        repository.delete(getPatientByEmail(email));
    }

    public List<PatientDto> findAll() {
        return repository.findAll().stream()
                .map(PatientMapper::toDto)
                .toList();
    }

    public PatientDto findByEmail(String email) {
        return PatientMapper.toDto(getPatientByEmail(email));
    }

    public PatientDto update(String email, UpdatePatientCommand command) {
        Patient patient = getPatientByEmail(email);

        return PatientMapper.toDto(patient.update(command));
    }

    private Patient getPatientByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException("Nie znaleziono pacjenta"));
    }
}
