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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
                .orElseThrow(() -> new DoctorNotFoundException(command.doctorId()));
        if (visitRepository.existsByDoctorIdAndStartDateBeforeAndFinishDateAfter(doctor.getId(), command.finishDate(), command.startDate())) {
                throw new VisitOverlapException("Doctor has visit booked on this term", HttpStatus.CONFLICT);
        }
        Visit visit = mapper.toEntity(command);
        doctor.addVisit(visit);
        return mapper.toDto(visitRepository.save(visit));
    }

    public VisitDto registerPatient(Long visitId, RegisterPatientForVisitCommand command) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new VisitNotFoundException(visitId));
        if (visit.getStartDate().isBefore(LocalDateTime.now())) {
            throw new IllegalDateException("Cannot book visit that was in the past", HttpStatus.CONFLICT);
        }
        if(visit.getPatient() != null) {
            throw new VisitAlreadyReservedException("This visit is already booked", HttpStatus.CONFLICT);
        }
        Patient patient = patientRepository.findById(command.patientId())
                .orElseThrow(() -> new PatientNotFoundException(command.patientId()));
        patient.addVisit(visit);
        return mapper.toDto((visitRepository.save(visit)));
    }

    public Page<VisitDto> findAll(Pageable pageable) {
        return visitRepository.findAll(pageable)
                .map(mapper::toDto);
    }

    public Page<VisitDto> findFreeVisits(Pageable pageable) {
        return visitRepository.findByPatientIsNull(pageable)
                .map(mapper::toDto);
    }

    public VisitDto findById(Long id) {
        return mapper.toDto(visitRepository.findById(id)
                .orElseThrow(() -> new VisitNotFoundException(id)));
    }

    public void delete(Long id) {
        visitRepository.delete(visitRepository.findById(id)
                .orElseThrow(() -> new VisitNotFoundException(id)));
    }

    public Page<VisitDto> findPatientVisits(Long patientId, Pageable pageable) {
        return visitRepository.findByPatientId(patientId, pageable)
                .map(mapper::toDto);
    }
}
