package com.w1kt0rx.medicalclinic.service;

import com.w1kt0rx.medicalclinic.command.CreateDoctorCommand;
import com.w1kt0rx.medicalclinic.command.UpdateDoctorCommand;
import com.w1kt0rx.medicalclinic.dto.DoctorDto;
import com.w1kt0rx.medicalclinic.exception.DoctorNotFoundException;
import com.w1kt0rx.medicalclinic.exception.UserNotFoundException;
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
                .orElseThrow(() -> new UserNotFoundException("Nie znaleziono użytkownika", HttpStatus.NOT_FOUND));
        List<Clinic> clinics = clinicRepository.findAllById(command.clinicIds());
        Doctor doctor = mapper.toEntity(command);
        doctor.setUser(user);
        doctor.getClinics().addAll(clinics);
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
        return mapper.toDto(doctorRepository.save(doctor.update(command)));
    }

    private Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(String.format("Nie znaleziono doktora o id: %d", id), HttpStatus.NOT_FOUND));
    }
}
