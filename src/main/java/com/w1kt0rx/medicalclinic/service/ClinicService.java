package com.w1kt0rx.medicalclinic.service;

import com.w1kt0rx.medicalclinic.command.CreateClinicCommand;
import com.w1kt0rx.medicalclinic.command.UpdateClinicCommand;
import com.w1kt0rx.medicalclinic.dto.ClinicDto;
import com.w1kt0rx.medicalclinic.exception.ClinicAlreadyExistsException;
import com.w1kt0rx.medicalclinic.exception.ClinicNotFoundException;
import com.w1kt0rx.medicalclinic.mapper.ClinicMapper;
import com.w1kt0rx.medicalclinic.model.Address;
import com.w1kt0rx.medicalclinic.model.Clinic;
import com.w1kt0rx.medicalclinic.repository.ClinicRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ClinicService {

    private final ClinicRepository clinicRepository;
    private final ClinicMapper mapper;

    @Transactional
    public ClinicDto create(CreateClinicCommand command) {
        if (clinicRepository.existsByName(command.name())) {
            throw new ClinicAlreadyExistsException("Clinic already exists", HttpStatus.CONFLICT);
        }
        Clinic clinic = mapper.toEntity(command);
        return mapper.toDto(clinicRepository.save(clinic));
    }

    @Transactional
    public void delete(Long id) {
        Clinic clinic = getClinicById(id);
        new HashSet<>(clinic.getDoctors()).forEach(clinic::removeDoctor);
        clinicRepository.delete(clinic);
    }

    public List<ClinicDto> findAll() {
        return clinicRepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    public ClinicDto findById(Long id) {
        return mapper.toDto(getClinicById(id));
    }

    @Transactional
    public ClinicDto update(Long id, UpdateClinicCommand command) {
        Clinic clinic = getClinicById(id).update(command);
        return mapper.toDto(clinicRepository.save(clinic));
    }

    private Clinic getClinicById(Long id) {
        return clinicRepository.findById(id)
                .orElseThrow(() -> new ClinicNotFoundException(id));
    }
}
