package com.w1kt0rx.medicalclinic.service;

import com.w1kt0rx.medicalclinic.command.CreatePatientCommand;
import com.w1kt0rx.medicalclinic.command.UpdatePatientCommand;
import com.w1kt0rx.medicalclinic.dto.PatientDto;
import com.w1kt0rx.medicalclinic.exception.PatientNotFoundException;
import com.w1kt0rx.medicalclinic.exception.UserNotFoundException;
import com.w1kt0rx.medicalclinic.mapper.PatientMapper;
import com.w1kt0rx.medicalclinic.model.Patient;
import com.w1kt0rx.medicalclinic.model.User;
import com.w1kt0rx.medicalclinic.repository.PatientRepository;

import java.util.List;

import com.w1kt0rx.medicalclinic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final PatientMapper mapper;

    public PatientDto create(CreatePatientCommand command) {

        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException("Nie znaleziono użytkownika", HttpStatus.NOT_FOUND));

        Patient patient = Patient.builder()
                .idCardNo(command.idCardNo())
                .birthday(command.birthday())
                .user(user)
                .build();

        return mapper.toDto(patientRepository.save(patient));
    }

    public void delete(Long id) {
        patientRepository.delete(getPatientById(id));
    }

    public List<PatientDto> findAll() {
        return patientRepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    public PatientDto findById(Long id) {
        return mapper.toDto(getPatientById(id));
    }

    public PatientDto update(Long id, UpdatePatientCommand command) {
        Patient patient = getPatientById(id);
        return mapper.toDto(patientRepository.save(patient.update(command)));
    }

    private Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(String.format("Nie znaleziono pacjeta o id: %s", id), HttpStatus.NOT_FOUND));
    }
}
