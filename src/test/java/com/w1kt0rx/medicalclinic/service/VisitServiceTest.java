package com.w1kt0rx.medicalclinic.service;

import com.w1kt0rx.medicalclinic.command.CreateVisitCommand;
import com.w1kt0rx.medicalclinic.command.RegisterPatientForVisitCommand;
import com.w1kt0rx.medicalclinic.dto.VisitDto;
import com.w1kt0rx.medicalclinic.exception.DoctorNotFoundException;
import com.w1kt0rx.medicalclinic.exception.IllegalDateException;
import com.w1kt0rx.medicalclinic.exception.PatientNotFoundException;
import com.w1kt0rx.medicalclinic.exception.VisitAlreadyReservedException;
import com.w1kt0rx.medicalclinic.exception.VisitNotFoundException;
import com.w1kt0rx.medicalclinic.exception.VisitOverlapException;
import com.w1kt0rx.medicalclinic.mapper.VisitMapper;
import com.w1kt0rx.medicalclinic.model.Doctor;
import com.w1kt0rx.medicalclinic.model.Patient;
import com.w1kt0rx.medicalclinic.model.User;
import com.w1kt0rx.medicalclinic.model.Visit;
import com.w1kt0rx.medicalclinic.repository.DoctorRepository;
import com.w1kt0rx.medicalclinic.repository.PatientRepository;
import com.w1kt0rx.medicalclinic.repository.VisitRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class VisitServiceTest {

    VisitService visitService;
    VisitRepository visitRepository;
    DoctorRepository doctorRepository;
    PatientRepository patientRepository;
    VisitMapper visitMapper;

    @BeforeEach
    void setup() {
        this.visitRepository = Mockito.mock(VisitRepository.class);
        this.doctorRepository = Mockito.mock(DoctorRepository.class);
        this.patientRepository = Mockito.mock(PatientRepository.class);
        this.visitMapper = Mappers.getMapper(VisitMapper.class);
        this.visitService = new VisitService(visitRepository, doctorRepository, patientRepository, visitMapper);
    }

    @Test
    void create_dataCorrect_visitReturned() {
        //given
        LocalDateTime start = LocalDateTime.of(2026, 12, 22, 10, 0);
        LocalDateTime finish = LocalDateTime.of(2026, 12, 22, 11, 0);
        CreateVisitCommand command = new CreateVisitCommand(1L, start, finish);
        Doctor doctor = new Doctor(1L, "Kardiolog", null, new HashSet<>(), new ArrayList<>());
        Visit savedVisit = new Visit(1L, start, finish, doctor, null);
        when(doctorRepository.findById(command.doctorId())).thenReturn(Optional.of(doctor));
        when(visitRepository.existsByDoctorIdAndStartDateBeforeAndFinishDateAfter(doctor.getId(), finish, start)).thenReturn(Boolean.FALSE);
        when(visitRepository.save(any(Visit.class))).thenReturn(savedVisit);
        //when
        VisitDto visitDto = visitService.create(command);
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, visitDto.id()),
                () -> Assertions.assertEquals(1L, visitDto.doctor().id()),
                () -> Assertions.assertEquals("Kardiolog", visitDto.doctor().specialization()),
                () -> Assertions.assertNull(visitDto.patient())
        );
    }

    @Test
    void create_startDateInPast_throwsException() {
        //given
        CreateVisitCommand command = new CreateVisitCommand(1L, LocalDateTime.of(2020, 1, 1, 10, 0), LocalDateTime.of(2020, 1, 1, 11, 0));
        //when then
        Assertions.assertThrows(IllegalDateException.class, () -> visitService.create(command));
    }

    @Test
    void create_startDateNotFullHour_throwsException() {
        //given
        CreateVisitCommand command = new CreateVisitCommand(1L, LocalDateTime.of(2026, 12, 22, 10, 30), LocalDateTime.of(2026, 12, 22, 11, 30));
        //when then
        Assertions.assertThrows(IllegalDateException.class, () -> visitService.create(command));
    }

    @Test
    void create_doctorNotExists_throwsException() {
        //given
        CreateVisitCommand command = new CreateVisitCommand(1L, LocalDateTime.of(2026, 12, 22, 10, 0), LocalDateTime.of(2026, 12, 22, 11, 0));
        when(doctorRepository.findById(command.doctorId())).thenReturn(Optional.empty());
        //when then
        Assertions.assertThrows(DoctorNotFoundException.class, () -> visitService.create(command));
    }

    @Test
    void create_visitOverlapsExistingVisit_throwsException() {
        //given
        LocalDateTime start = LocalDateTime.of(2026, 12, 22, 10, 0);
        LocalDateTime finish = LocalDateTime.of(2026, 12, 22, 11, 0);
        CreateVisitCommand command = new CreateVisitCommand(1L, start, finish);
        Doctor doctor = new Doctor(1L, "Kardiolog", null, new HashSet<>(), new ArrayList<>());
        when(doctorRepository.findById(command.doctorId())).thenReturn(Optional.of(doctor));
        when(visitRepository.existsByDoctorIdAndStartDateBeforeAndFinishDateAfter(doctor.getId(), finish, start)).thenReturn(Boolean.TRUE);
        //when then
        Assertions.assertThrows(VisitOverlapException.class, () -> visitService.create(command));
    }

    @Test
    void registerPatient_dataCorrect_visitReturned() {
        //given
        LocalDateTime start = LocalDateTime.of(2026, 12, 22, 10, 0);
        LocalDateTime finish = LocalDateTime.of(2026, 12, 22, 11, 0);
        Doctor doctor = new Doctor(1L, "Kardiolog", null, new HashSet<>(), new ArrayList<>());
        Visit visit = new Visit(1L, start, finish, doctor, null);
        User user = new User(1L, "example@email.com", "password", "John", "Surname", "123123123", null, null);
        Patient patient = new Patient(1L, "123123", LocalDate.of(1990, 1, 1), user, new ArrayList<>());
        RegisterPatientForVisitCommand command = new RegisterPatientForVisitCommand(1L);
        when(visitRepository.findById(visit.getId())).thenReturn(Optional.of(visit));
        when(patientRepository.findById(command.patientId())).thenReturn(Optional.of(patient));
        when(visitRepository.save(any(Visit.class))).thenReturn(visit);
        //when
        VisitDto visitDto = visitService.registerPatient(visit.getId(), command);
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, visitDto.id()),
                () -> Assertions.assertEquals(1L, visitDto.patient().id()),
                () -> Assertions.assertEquals("123123", visitDto.patient().idCardNo()),
                () -> Assertions.assertTrue(patient.getVisits().contains(visit))
        );
    }

    @Test
    void registerPatient_visitNotExists_throwsException() {
        //given
        RegisterPatientForVisitCommand command = new RegisterPatientForVisitCommand(1L);
        when(visitRepository.findById(1L)).thenReturn(Optional.empty());
        //when + then
        Assertions.assertThrows(VisitNotFoundException.class, () -> visitService.registerPatient(1L, command));
    }

    @Test
    void registerPatient_visitInPast_throwsException() {
        //given
        Doctor doctor = new Doctor(1L, "Kardiolog", null, new HashSet<>(), new ArrayList<>());
        Visit visit = new Visit(1L, LocalDateTime.of(2020, 1, 1, 10, 0), LocalDateTime.of(2020, 1, 1, 11, 0), doctor, null);
        RegisterPatientForVisitCommand command = new RegisterPatientForVisitCommand(1L);
        when(visitRepository.findById(visit.getId())).thenReturn(Optional.of(visit));
        //when then
        Assertions.assertThrows(IllegalDateException.class, () -> visitService.registerPatient(visit.getId(), command));
    }

    @Test
    void registerPatient_visitAlreadyReserved_throwsException() {
        //given
        Doctor doctor = new Doctor(1L, "Kardiolog", null, new HashSet<>(), new ArrayList<>());
        User user = new User(1L, "example@email.com", "password", "John", "Surname", "123123123", null, null);
        Patient patient = new Patient(1L, "123123", LocalDate.of(1990, 1, 1), user, new ArrayList<>());
        Visit visit = new Visit(1L, LocalDateTime.of(2026, 12, 22, 10, 0), LocalDateTime.of(2026, 12, 22, 11, 0), doctor, patient);
        RegisterPatientForVisitCommand command = new RegisterPatientForVisitCommand(1L);
        when(visitRepository.findById(visit.getId())).thenReturn(Optional.of(visit));
        //when then
        Assertions.assertThrows(VisitAlreadyReservedException.class, () -> visitService.registerPatient(visit.getId(), command));
    }

    @Test
    void registerPatient_patientNotExists_throwsException() {
        //given
        Doctor doctor = new Doctor(1L, "Kardiolog", null, new HashSet<>(), new ArrayList<>());
        Visit visit = new Visit(1L, LocalDateTime.of(2026, 12, 22, 10, 0), LocalDateTime.of(2026, 12, 22, 11, 0), doctor, null);
        RegisterPatientForVisitCommand command = new RegisterPatientForVisitCommand(1L);
        when(visitRepository.findById(visit.getId())).thenReturn(Optional.of(visit));
        when(patientRepository.findById(command.patientId())).thenReturn(Optional.empty());
        //when then
        Assertions.assertThrows(PatientNotFoundException.class, () -> visitService.registerPatient(visit.getId(), command));
    }

    @Test
    void findAll_dataCorrect_visitsReturned() {
        //given
        Doctor doctor = new Doctor(1L, "Kardiolog", null, new HashSet<>(), new ArrayList<>());
        Visit visit1 = new Visit(1L, LocalDateTime.of(2026, 12, 22, 10, 0), LocalDateTime.of(2026, 12, 22, 11, 0), doctor, null);
        Visit visit2 = new Visit(2L, LocalDateTime.of(2026, 12, 23, 10, 0), LocalDateTime.of(2026, 12, 23, 11, 0), doctor, null);
        when(visitRepository.findAll()).thenReturn(List.of(visit1, visit2));
        //when
        List<VisitDto> visits = visitService.findAll();
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, visits.size()),
                () -> Assertions.assertEquals(1L, visits.getFirst().id()),
                () -> Assertions.assertEquals(2L, visits.get(1).id())
        );
    }

    @Test
    void findFreeVisits_dataCorrect_visitsReturned() {
        //given
        Doctor doctor = new Doctor(1L, "Kardiolog", null, new HashSet<>(), new ArrayList<>());
        Visit visit = new Visit(1L, LocalDateTime.of(2026, 12, 22, 10, 0), LocalDateTime.of(2026, 12, 22, 11, 0), doctor, null);
        when(visitRepository.findByPatientIsNull()).thenReturn(List.of(visit));
        //when
        List<VisitDto> visits = visitService.findFreeVisits();
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, visits.size()),
                () -> Assertions.assertEquals(1L, visits.getFirst().id()),
                () -> Assertions.assertNull(visits.getFirst().patient())
        );
    }

    @Test
    void findById_visitExists_visitReturned() {
        //given
        Doctor doctor = new Doctor(1L, "Kardiolog", null, new HashSet<>(), new ArrayList<>());
        Visit visit = new Visit(1L, LocalDateTime.of(2026, 12, 22, 10, 0), LocalDateTime.of(2026, 12, 22, 11, 0), doctor, null);
        when(visitRepository.findById(visit.getId())).thenReturn(Optional.of(visit));
        //when
        VisitDto visitDto = visitService.findById(visit.getId());
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(visit.getId(), visitDto.id()),
                () -> Assertions.assertEquals(visit.getDoctor().getId(), visitDto.doctor().id())
        );
    }

    @Test
    void findById_visitNotExists_throwsException() {
        //given
        when(visitRepository.findById(1L)).thenReturn(Optional.empty());
        //when then
        Assertions.assertThrows(VisitNotFoundException.class, () -> visitService.findById(1L));
    }

    @Test
    void delete_visitExists_visitDeleted() {
        //given
        Doctor doctor = new Doctor(1L, "Kardiolog", null, new HashSet<>(), new ArrayList<>());
        Visit visit = new Visit(1L, LocalDateTime.of(2026, 12, 22, 10, 0), LocalDateTime.of(2026, 12, 22, 11, 0), doctor, null);
        when(visitRepository.findById(visit.getId())).thenReturn(Optional.of(visit));
        //when
        visitService.delete(visit.getId());
        //then
        Mockito.verify(visitRepository).delete(visit);
    }

    @Test
    void delete_visitNotExists_throwsException() {
        //given
        when(visitRepository.findById(1L)).thenReturn(Optional.empty());
        //when then
        Assertions.assertThrows(VisitNotFoundException.class, () -> visitService.delete(1L));
    }

    @Test
    void findPatientVisits_dataCorrect_visitsReturned() {
        //given
        Doctor doctor = new Doctor(1L, "Kardiolog", null, new HashSet<>(), new ArrayList<>());
        User user = new User(1L, "example@email.com", "password", "John", "Surname", "123123123", null, null);
        Patient patient = new Patient(1L, "123123", LocalDate.of(1990, 1, 1), user, new ArrayList<>());
        Visit visit = new Visit(1L, LocalDateTime.of(2026, 12, 22, 10, 0), LocalDateTime.of(2026, 12, 22, 11, 0), doctor, patient);
        when(visitRepository.findByPatientId(patient.getId())).thenReturn(List.of(visit));
        //when
        List<VisitDto> visits = visitService.findPatientVisits(patient.getId());
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, visits.size()),
                () -> Assertions.assertEquals(1L, visits.getFirst().id()),
                () -> Assertions.assertEquals(1L, visits.getFirst().patient().id())
        );
    }
}
