package com.w1kt0rx.medicalclinic.service;

import com.w1kt0rx.medicalclinic.command.CreateClinicCommand;
import com.w1kt0rx.medicalclinic.command.UpdateClinicCommand;
import com.w1kt0rx.medicalclinic.dto.ClinicDto;
import com.w1kt0rx.medicalclinic.dto.PageDto;
import com.w1kt0rx.medicalclinic.dto.PageRequestDto;
import com.w1kt0rx.medicalclinic.exception.ClinicAlreadyExistsException;
import com.w1kt0rx.medicalclinic.exception.ClinicNotFoundException;
import com.w1kt0rx.medicalclinic.mapper.ClinicMapper;
import com.w1kt0rx.medicalclinic.mapper.PageRequestMapper;
import com.w1kt0rx.medicalclinic.model.Clinic;
import com.w1kt0rx.medicalclinic.repository.ClinicRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Slf4j
@RequiredArgsConstructor
@Service
public class ClinicService {

    private final ClinicRepository clinicRepository;
    private final ClinicMapper mapper;
    private final PageRequestMapper pageRequestMapper;

    @Transactional
    public ClinicDto create(CreateClinicCommand command) {
        log.debug("Creating clinic, name={}", command.name());
        if (clinicRepository.existsByName(command.name())) {
            log.warn("Cannot create clinic - name already exists: {}", command.name());
            throw new ClinicAlreadyExistsException("Clinic already exists", HttpStatus.CONFLICT);
        }
        Clinic clinic = mapper.toEntity(command);
        Clinic saved = clinicRepository.save(clinic);
        log.info("Clinic created, id={}, name={}", saved.getId(), saved.getName());
        return mapper.toDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        log.debug("Deleting clinic id={}", id);
        Clinic clinic = getClinicById(id);
        new HashSet<>(clinic.getDoctors()).forEach(clinic::removeDoctor);
        clinicRepository.delete(clinic);
        log.info("Clinic deleted, id={}", id);
    }

    public PageDto<ClinicDto> findAll(PageRequestDto pageRequestDto) {
        Pageable pageable = pageRequestMapper.toPageable(pageRequestDto);
        log.debug("Fetching clinics page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return PageDto.from(clinicRepository.findAll(pageable).map(mapper::toDto));
    }

    public ClinicDto findById(Long id) {
        log.debug("Fetching clinic by id={}", id);
        return mapper.toDto(getClinicById(id));
    }

    @Transactional
    public ClinicDto update(Long id, UpdateClinicCommand command) {
        log.debug("Updating clinic id={}", id);
        Clinic clinic = getClinicById(id);
        clinic.update(command);
        Clinic saved = clinicRepository.save(clinic);
        log.info("Clinic updated, id={}", saved.getId());
        return mapper.toDto(saved);
    }

    private Clinic getClinicById(Long id) {
        return clinicRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot delete - clinic not found, id={}", id);
                    return new ClinicNotFoundException(id);
                });
    }
}
