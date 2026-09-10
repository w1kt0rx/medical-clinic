package com.w1kt0rx.medicalclinic.service;

import com.w1kt0rx.medicalclinic.command.CreateDoctorCommand;
import com.w1kt0rx.medicalclinic.command.UpdateDoctorCommand;
import com.w1kt0rx.medicalclinic.dto.DoctorDto;
import com.w1kt0rx.medicalclinic.dto.PageDto;
import com.w1kt0rx.medicalclinic.dto.PageRequestDto;
import com.w1kt0rx.medicalclinic.exception.*;
import com.w1kt0rx.medicalclinic.mapper.DoctorMapper;
import com.w1kt0rx.medicalclinic.mapper.PageRequestMapper;
import com.w1kt0rx.medicalclinic.model.Clinic;
import com.w1kt0rx.medicalclinic.model.Doctor;
import com.w1kt0rx.medicalclinic.model.User;
import com.w1kt0rx.medicalclinic.repository.ClinicRepository;
import com.w1kt0rx.medicalclinic.repository.DoctorRepository;
import com.w1kt0rx.medicalclinic.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final ClinicRepository clinicRepository;
    private final DoctorMapper mapper;
    private final PageRequestMapper pageRequestMapper;

    @Transactional
    public DoctorDto create(CreateDoctorCommand command) {
        log.debug("Creating doctor for userId={}, specialization={}", command.userId(), command.specialization());
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> {
                    log.warn("Cannot create doctor - user not found, userId={}", command.userId());
                    return new UserNotFoundException(command.userId());
                });
        ;
        Set<Clinic> clinics = new HashSet<>(
                clinicRepository.findAllById(command.clinicIds())
        );
        Doctor doctor = mapper.toEntity(command);
        doctor.setUser(user);
        clinics.forEach(doctor::addClinic);
        Doctor saved = doctorRepository.save(doctor);
        log.info("Doctor created, id={}, userId={}, clinicsAssigned={}", saved.getId(), user.getId(), clinics.size());
        return mapper.toDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        log.debug("Deleting doctor id={}", id);
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot delete - doctor not found, id={}", id);
                    return new DoctorNotFoundException(id);
                });
        if (!doctor.getVisits().isEmpty()) {
            log.warn("Cannot delete doctor id={} - has {} scheduled visit(s)", id, doctor.getVisits().size());
            throw new DoctorHasScheduledVisitsException(id);
        }
        doctorRepository.delete(doctor);
        log.info("Doctor deleted, id={}", id);
    }

    public PageDto<DoctorDto> findAll(PageRequestDto pageRequestDto) {
        Pageable pageable = pageRequestMapper.toPageable(pageRequestDto);
        log.debug("Fetching doctors page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return PageDto.from(doctorRepository.findAll(pageable).map(mapper::toDto));
    }

    public DoctorDto findById(Long id) {
        log.debug("Fetching doctor by id={}", id);
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Doctor not found, id={}", id);
                    return new DoctorNotFoundException(id);
                });
        return mapper.toDto(doctor);
    }

    @Transactional
    public DoctorDto update(Long id, UpdateDoctorCommand command) {
        log.debug("Updating doctor id={}", id);
        Doctor doctor = getDoctorById(id);
        Set<Clinic> clinics = new HashSet<>(
                clinicRepository.findAllById(command.clinicIds())
        );
        doctor.update(command, clinics);
        Doctor saved = doctorRepository.save(doctor);
        log.info("Doctor updated, id={}", saved.getId());
        return mapper.toDto(saved);
    }

    @Transactional
    public DoctorDto addClinic(Long doctorId, Long clinicId) {
        log.debug("Adding clinicId={} to doctorId={}", clinicId, doctorId);
        Doctor doctor = getDoctorById(doctorId);
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> {
                    log.warn("Cannot add clinic - clinic not found, clinicId={}", clinicId);
                    return new ClinicNotFoundException(clinicId);
                });
        if (!doctor.addClinic(clinic)) {
            log.warn("Cannot add clinic - doctorId={} already assigned to clinicId={}", doctorId, clinicId);
            throw new ClinicAlreadyAssignedException(
                    "Doctor is already assigned to this clinic",
                    HttpStatus.CONFLICT
            );
        }
        Doctor saved = doctorRepository.save(doctor);
        log.info("Clinic added, doctorId={}, clinicId={}", doctorId, clinicId);
        return mapper.toDto(saved);
    }

    @Transactional
    public DoctorDto removeClinic(Long doctorId, Long clinicId) {
        log.debug("Removing clinicId={} from doctorId={}", clinicId, doctorId);
        Doctor doctor = getDoctorById(doctorId);
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> {
                    log.warn("Cannot remove clinic - clinic not found, clinicId={}", clinicId);
                    return new ClinicNotFoundException(clinicId);
                });
        if (!doctor.removeClinic(clinic)) {
            log.warn("Cannot remove clinic - doctorId={} was not assigned to clinicId={}", doctorId, clinicId);
            throw new ClinicNotAssignedException(
                    "Doctor was not assigned to this clinic",
                    HttpStatus.NOT_FOUND
            );
        }
        Doctor saved = doctorRepository.save(doctor);
        log.info("Clinic removed, doctorId={}, clinicId={}", doctorId, clinicId);
        return mapper.toDto(saved);
    }

    private Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot update - doctor not found, id={}", id);
                    return new DoctorNotFoundException(id);
                });
    }
}
