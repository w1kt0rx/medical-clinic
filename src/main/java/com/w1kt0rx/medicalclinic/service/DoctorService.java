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
import lombok.RequiredArgsConstructor;
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

    public DoctorDto create(CreateDoctorCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException("Couldn't find user", HttpStatus.NOT_FOUND));
        Set<Clinic> clinics = new HashSet<>(
                clinicRepository.findAllById(command.clinicIds())
        );
        Doctor doctor = mapper.toEntity(command);
        doctor.setUser(user);
        doctor.setClinics(clinics);
        return mapper.toDto(doctorRepository.save(doctor));
    }

    public void delete(Long id) {
        doctorRepository.delete(getDoctorById(id));
    }

    public List<DoctorDto> findAll() {
        return doctorRepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    public DoctorDto findById(Long id) {
        return mapper.toDto(getDoctorById(id));
    }

    public DoctorDto update(Long id, UpdateDoctorCommand command) {
        Doctor doctor = getDoctorById(id);
        Set<Clinic> clinics = new HashSet<>(
                clinicRepository.findAllById(command.clinicIds())
        );
        return mapper.toDto(doctorRepository.save(doctor.update(command, clinics)));
    }

    public DoctorDto addClinic(Long doctorId, Long clinicId) {
        Doctor doctor = getDoctorById(doctorId);
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ClinicNotFoundException("Couldn't find clinic", HttpStatus.NOT_FOUND));
        if (!doctor.getClinics().add(clinic)) {
            throw new ClinicAlreadyAssignedException(
                    "Doctor is already assigned to this clinic",
                    HttpStatus.CONFLICT
            );
        }
        return mapper.toDto(doctorRepository.save(doctor));
    }

    public DoctorDto removeClinic(Long doctorId, Long clinicId) {
        Doctor doctor = getDoctorById(doctorId);
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ClinicNotFoundException("Couldn't find clinic", HttpStatus.NOT_FOUND));
        if (!doctor.getClinics().remove(clinic)) {
            throw new ClinicNotAssignedException(
                    "Doctor was not assigned to this clinic",
                    HttpStatus.NOT_FOUND
            );
        }
        return mapper.toDto(doctorRepository.save(doctor));
    }

    private Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(String.format("Couldn't find doctor with id: %d", id), HttpStatus.NOT_FOUND));
    }
}
