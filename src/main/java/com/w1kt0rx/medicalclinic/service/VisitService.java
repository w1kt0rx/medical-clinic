package com.w1kt0rx.medicalclinic.service;

import com.w1kt0rx.medicalclinic.command.CreateVisitCommand;
import com.w1kt0rx.medicalclinic.command.RegisterPatientForVisitCommand;
import com.w1kt0rx.medicalclinic.dto.VisitDto;
import com.w1kt0rx.medicalclinic.exception.*;
import com.w1kt0rx.medicalclinic.mapper.VisitMapper;
import com.w1kt0rx.medicalclinic.model.Doctor;
import com.w1kt0rx.medicalclinic.model.Patient;
import com.w1kt0rx.medicalclinic.model.Visit;
import com.w1kt0rx.medicalclinic.repository.DoctorRepository;
import com.w1kt0rx.medicalclinic.repository.PatientRepository;
import com.w1kt0rx.medicalclinic.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitService {
    private final VisitRepository visitRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final VisitMapper mapper;

    public VisitDto create(CreateVisitCommand command) {
        if (command.startDate().isBefore(LocalDateTime.now())) {
            throw new IllegalDateException("Cannot create visit in the past", HttpStatus.CONFLICT);
        }
        if (command.startDate().getMinute() != 0) {
            throw new IllegalDateException("Visit can only start at the top of the hour", HttpStatus.CONFLICT);
        }
        Doctor doctor = doctorRepository.findById(command.doctorId())
                .orElseThrow(() -> new DoctorNotFoundException(String.format("Couldn't find a doctor with id: %s", command.doctorId()), HttpStatus.NOT_FOUND));
        if (visitRepository.existsByDoctorIdAndStartDateBeforeAndFinishDateAfter(doctor.getId(), command.finishDate(), command.startDate())) {
                throw new VisitOverlapException("Doctor has visit booked on this term", HttpStatus.CONFLICT);
        }
        Visit visit = mapper.toEntity(command);
        visit.setDoctor(doctor);
        return mapper.toDto(visitRepository.save(visit));
    }

    public VisitDto registerPatient(Long visitId, RegisterPatientForVisitCommand command) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new VisitNotFoundException(String.format("Couldn't find a visit with id: %s", visitId), HttpStatus.NOT_FOUND));
        if (visit.getStartDate().isBefore(LocalDateTime.now())) {
            throw new IllegalDateException("Cannot book visit that was in the past", HttpStatus.CONFLICT);
        }
        if(visit.getPatient() != null) {
            throw new VisitAlreadyReservedException("This visit is already booked", HttpStatus.CONFLICT);
        }
        Patient patient = patientRepository.findById(command.patientId())
                .orElseThrow(() -> new PatientNotFoundException(String.format("Couldn't find a patient with id: %s", command.patientId()),HttpStatus.NOT_FOUND));
        visit.setPatient(patient);
        return mapper.toDto((visitRepository.save(visit)));
    }

    public List<VisitDto> findAll() {
        return visitRepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    public List<VisitDto> findFreeVisits() {
        return visitRepository.findByPatientIsNull()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public VisitDto findById(Long id) {
        return mapper.toDto(visitRepository.findById(id)
                .orElseThrow(() -> new VisitNotFoundException(String.format("Couldn't find a visit with id: %s", id), HttpStatus.NOT_FOUND)));
    }

    public void delete(Long id) {
        visitRepository.delete(visitRepository.findById(id)
                .orElseThrow(() -> new VisitNotFoundException(String.format("Couldn't find a visit with id: %s", id), HttpStatus.NOT_FOUND)));
    }

    public List<VisitDto> findPatientVisits(Long patientId) {
        return visitRepository.findByPatientId(patientId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}
