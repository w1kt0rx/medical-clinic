package com.w1kt0rx.medicalclinic.service;

import com.w1kt0rx.medicalclinic.command.CreateDoctorCommand;
import com.w1kt0rx.medicalclinic.command.UpdateDoctorCommand;
import com.w1kt0rx.medicalclinic.dto.DoctorDto;
import com.w1kt0rx.medicalclinic.exception.*;
import com.w1kt0rx.medicalclinic.mapper.DoctorMapper;
import com.w1kt0rx.medicalclinic.model.Clinic;
import com.w1kt0rx.medicalclinic.model.Doctor;
import com.w1kt0rx.medicalclinic.model.User;
import com.w1kt0rx.medicalclinic.repository.ClinicRepository;
import com.w1kt0rx.medicalclinic.repository.DoctorRepository;
import com.w1kt0rx.medicalclinic.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final ClinicRepository clinicRepository;
    private final DoctorMapper mapper;

    @Transactional
    public DoctorDto create(CreateDoctorCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));
        Set<Clinic> clinics = new HashSet<>(
                clinicRepository.findAllById(command.clinicIds())
        );
        Doctor doctor = mapper.toEntity(command);
        doctor.setUser(user);
        clinics.forEach(doctor::addClinic);
        return mapper.toDto(doctorRepository.save(doctor));
    }

    @Transactional
    public void delete(Long id) {
        Doctor doctor = getDoctorById(id);
        if (!doctor.getVisits().isEmpty()) {
            throw new DoctorHasScheduledVisitsException(id);
        }
        new HashSet<>(doctor.getClinics()).forEach(doctor::removeClinic);
        doctorRepository.delete(doctor);
    }

    public Page<DoctorDto> findAll(Pageable pageable) {
        return doctorRepository.findAll(pageable)
                .map(mapper::toDto);
    }

    public DoctorDto findById(Long id) {
        return mapper.toDto(getDoctorById(id));
    }

    @Transactional
    public DoctorDto update(Long id, UpdateDoctorCommand command) {
        Doctor doctor = getDoctorById(id);
        Set<Clinic> clinics = new HashSet<>(
                clinicRepository.findAllById(command.clinicIds())
        );
        return mapper.toDto(doctorRepository.save(doctor.update(command, clinics)));
    }

    @Transactional
    public DoctorDto addClinic(Long doctorId, Long clinicId) {
        Doctor doctor = getDoctorById(doctorId);
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ClinicNotFoundException(clinicId));
        if (!doctor.addClinic(clinic)) {
            throw new ClinicAlreadyAssignedException(
                    "Doctor is already assigned to this clinic",
                    HttpStatus.CONFLICT
            );
        }
        return mapper.toDto(doctorRepository.save(doctor));
    }

    @Transactional
    public DoctorDto removeClinic(Long doctorId, Long clinicId) {
        Doctor doctor = getDoctorById(doctorId);
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ClinicNotFoundException(clinicId));
        if (!doctor.removeClinic(clinic)) {
            throw new ClinicNotAssignedException(
                    "Doctor was not assigned to this clinic",
                    HttpStatus.NOT_FOUND
            );
        }
        return mapper.toDto(doctorRepository.save(doctor));
    }

    private Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));
    }
}
