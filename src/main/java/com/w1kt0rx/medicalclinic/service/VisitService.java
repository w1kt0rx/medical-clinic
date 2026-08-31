package com.w1kt0rx.medicalclinic.service;

import com.w1kt0rx.medicalclinic.command.CreateVisitCommand;
import com.w1kt0rx.medicalclinic.command.RegisterPatientForVisitCommand;
import com.w1kt0rx.medicalclinic.dto.PageDto;
import com.w1kt0rx.medicalclinic.dto.PageRequestDto;
import com.w1kt0rx.medicalclinic.dto.VisitDto;
import com.w1kt0rx.medicalclinic.exception.*;
import com.w1kt0rx.medicalclinic.mapper.PageRequestMapper;
import com.w1kt0rx.medicalclinic.mapper.VisitMapper;
import com.w1kt0rx.medicalclinic.model.Doctor;
import com.w1kt0rx.medicalclinic.model.Patient;
import com.w1kt0rx.medicalclinic.model.Visit;
import com.w1kt0rx.medicalclinic.repository.DoctorRepository;
import com.w1kt0rx.medicalclinic.repository.PatientRepository;
import com.w1kt0rx.medicalclinic.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class VisitService {
    private final VisitRepository visitRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final VisitMapper mapper;
    private final PageRequestMapper pageRequestMapper;

    public VisitDto create(CreateVisitCommand command) {
        log.debug("Creating visit for doctorId={}, start={}, finish={}", command.doctorId(), command.startDate(), command.finishDate());
        if (command.startDate().isBefore(LocalDateTime.now())) {
            log.warn("Rejected visit creation - start date in the past: {}", command.startDate());
            throw new IllegalDateException("Cannot create visit in the past", HttpStatus.CONFLICT);
        }
        if (command.startDate().getMinute() != 0) {
            log.warn("Rejected visit creation - start date not on the hour: {}", command.startDate());
            throw new IllegalDateException("Visit can only start at the top of the hour", HttpStatus.CONFLICT);
        }
        Doctor doctor = doctorRepository.findById(command.doctorId())
                .orElseThrow(() ->{
                    log.warn("Cannot create visit - doctor not found, doctorId={}", command.doctorId());
                    return new DoctorNotFoundException(command.doctorId());
                });
        if (visitRepository.existsByDoctorIdAndStartDateBeforeAndFinishDateAfter(doctor.getId(), command.finishDate(), command.startDate())) {
            log.warn("Rejected visit creation - overlapping visit for doctorId={}", doctor.getId());
            throw new VisitOverlapException("Doctor has visit booked on this term", HttpStatus.CONFLICT);
        }
        Visit visit = mapper.toEntity(command);
        doctor.addVisit(visit);
        Visit saved = visitRepository.save(visit);
        log.info("Visit created, id={}, doctorId={}", saved.getId(), doctor.getId());
        return mapper.toDto(saved);
    }

    public VisitDto registerPatient(Long visitId, RegisterPatientForVisitCommand command) {
        log.debug("Registering patientId={} for visitId={}", command.patientId(), visitId);
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> {
                    log.warn("Cannot register patient - visit not found, id={}", visitId);
                    return new VisitNotFoundException(visitId);
                });
        if (visit.getStartDate().isBefore(LocalDateTime.now())) {
            log.warn("Cannot register patient - visit id={} already in the past", visitId);
            throw new IllegalDateException("Cannot book visit that was in the past", HttpStatus.CONFLICT);
        }
        if (visit.getPatient() != null) {
            log.warn("Cannot register patient - visit id={} already reserved by patientId={}", visitId, visit.getPatient().getId());
            throw new VisitAlreadyReservedException("This visit is already booked", HttpStatus.CONFLICT);
        }
        Patient patient = patientRepository.findById(command.patientId())
                .orElseThrow(() -> {
                    log.warn("Cannot register patient - patient not found, patientId={}", command.patientId());
                    return new PatientNotFoundException(command.patientId());
                });
        patient.addVisit(visit);
        Visit saved = visitRepository.save(visit);
        log.info("Patient registered for visit, visitId={}, patientId={}", saved.getId(), patient.getId());
        return mapper.toDto(saved);
    }

    public PageDto<VisitDto> findAll(PageRequestDto pageRequestDto) {
        Pageable pageable = pageRequestMapper.toPageable(pageRequestDto);
        log.debug("Fetching visits page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return PageDto.from(visitRepository.findAll(pageable).map(mapper::toDto));
    }

    public PageDto<VisitDto> findFreeVisits(PageRequestDto pageRequestDto) {
        Pageable pageable = pageRequestMapper.toPageable(pageRequestDto);
        log.debug("Fetching free visits page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return PageDto.from(visitRepository.findByPatientIsNull(pageable).map(mapper::toDto));
    }

    public VisitDto findById(Long id) {
        log.debug("Fetching visit by id={}", id);
        Visit visit = visitRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Visit not found, id={}", id);
                    return new VisitNotFoundException(id);
                });
        return mapper.toDto(visit);
    }

    public void delete(Long id) {
        log.debug("Deleting visit id={}", id);
        Visit visit = visitRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot delete - visit not found, id={}", id);
                    return new VisitNotFoundException(id);
                });
        visitRepository.delete(visit);
        log.info("Visit deleted, id={}", id);
    }


    public PageDto<VisitDto> findPatientVisits(Long patientId, PageRequestDto pageRequestDto) {
        Pageable pageable = pageRequestMapper.toPageable(pageRequestDto);
        log.debug("Fetching visits for patientId={}", patientId);
        return PageDto.from(visitRepository.findByPatientId(patientId, pageable).map(mapper::toDto));
    }
}
