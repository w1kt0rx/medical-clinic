package com.w1kt0rx.medicalclinic.service;

import com.w1kt0rx.medicalclinic.command.CreatePatientCommand;
import com.w1kt0rx.medicalclinic.command.UpdatePatientCommand;
import com.w1kt0rx.medicalclinic.dto.PageDto;
import com.w1kt0rx.medicalclinic.dto.PageRequestDto;
import com.w1kt0rx.medicalclinic.dto.PatientDto;
import com.w1kt0rx.medicalclinic.exception.PatientHasScheduledVisitsException;
import com.w1kt0rx.medicalclinic.exception.PatientNotFoundException;
import com.w1kt0rx.medicalclinic.exception.UserNotFoundException;
import com.w1kt0rx.medicalclinic.mapper.PageRequestMapper;
import com.w1kt0rx.medicalclinic.mapper.PatientMapper;
import com.w1kt0rx.medicalclinic.model.Patient;
import com.w1kt0rx.medicalclinic.model.User;
import com.w1kt0rx.medicalclinic.repository.PatientRepository;
import com.w1kt0rx.medicalclinic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final PatientMapper mapper;
    private final PageRequestMapper pageRequestMapper;

    public PatientDto create(CreatePatientCommand command) {
        log.debug("Creating patient for userId={}", command.userId());
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> {
                    log.warn("Cannot create patient - user not found, userId={}", command.userId());
                    return new UserNotFoundException(command.userId());
                });
        Patient patient = mapper.toEntity(command);
        patient.setUser(user);
        Patient saved = patientRepository.save(patient);
        log.info("Patient created, id={}, userId={}", saved.getId(), user.getId());
        return mapper.toDto(saved);
    }

    public PageDto<PatientDto> findAll(PageRequestDto pageRequestDto) {
        Pageable pageable = pageRequestMapper.toPageable(pageRequestDto);
        log.debug("Fetching patients page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return PageDto.from(patientRepository.findAll(pageable).map(mapper::toDto));
    }

    public PatientDto findById(Long id) {
        log.debug("Fetching patient by id={}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Patient not found, id={}", id);
                    return new PatientNotFoundException(id);
                });
        return mapper.toDto(patient);
    }

    public PatientDto update(Long id, UpdatePatientCommand command) {
        log.debug("Updating patient id={}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot update - patient not found, id={}", id);
                    return new PatientNotFoundException(id);
                });
        patient.update(command);
        Patient saved = patientRepository.save(patient);
        log.info("Patient updated, id={}", saved.getId());
        return mapper.toDto(saved);
    }

    public void delete(Long id) {
        log.debug("Deleting patient id={}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot delete - patient not found, id={}", id);
                    return new PatientNotFoundException(id);
                });
        if (!patient.getVisits().isEmpty()) {
            log.warn("Cannot delete patient id={} - has {} scheduled visit(s)", id, patient.getVisits().size());
            throw new PatientHasScheduledVisitsException(id);
        }
        patientRepository.delete(patient);
        log.info("Patient deleted, id={}", id);
    }
}