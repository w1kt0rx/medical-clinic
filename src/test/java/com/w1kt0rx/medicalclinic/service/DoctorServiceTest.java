package com.w1kt0rx.medicalclinic.service;

import com.w1kt0rx.medicalclinic.command.CreateDoctorCommand;
import com.w1kt0rx.medicalclinic.command.UpdateDoctorCommand;
import com.w1kt0rx.medicalclinic.dto.AddressDto;
import com.w1kt0rx.medicalclinic.dto.ClinicDto;
import com.w1kt0rx.medicalclinic.dto.DoctorDto;
import com.w1kt0rx.medicalclinic.dto.UserDto;
import com.w1kt0rx.medicalclinic.exception.ClinicAlreadyAssignedException;
import com.w1kt0rx.medicalclinic.exception.ClinicNotAssignedException;
import com.w1kt0rx.medicalclinic.exception.ClinicNotFoundException;
import com.w1kt0rx.medicalclinic.exception.DoctorHasScheduledVisitsException;
import com.w1kt0rx.medicalclinic.exception.DoctorNotFoundException;
import com.w1kt0rx.medicalclinic.exception.UserNotFoundException;
import com.w1kt0rx.medicalclinic.mapper.DoctorMapper;
import com.w1kt0rx.medicalclinic.model.Address;
import com.w1kt0rx.medicalclinic.model.Clinic;
import com.w1kt0rx.medicalclinic.model.Doctor;
import com.w1kt0rx.medicalclinic.model.User;
import com.w1kt0rx.medicalclinic.model.Visit;
import com.w1kt0rx.medicalclinic.repository.ClinicRepository;
import com.w1kt0rx.medicalclinic.repository.DoctorRepository;
import com.w1kt0rx.medicalclinic.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class DoctorServiceTest {

    DoctorService doctorService;
    DoctorRepository doctorRepository;
    UserRepository userRepository;
    ClinicRepository clinicRepository;
    DoctorMapper doctorMapper;

    @BeforeEach
    void setup() {
        this.doctorRepository = Mockito.mock(DoctorRepository.class);
        this.userRepository = Mockito.mock(UserRepository.class);
        this.clinicRepository = Mockito.mock(ClinicRepository.class);
        this.doctorMapper = Mappers.getMapper(DoctorMapper.class);
        this.doctorService = new DoctorService(doctorRepository, userRepository, clinicRepository, doctorMapper);
    }

    @Test
    void create_dataCorrect_doctorReturned() {
        //given
        User user = new User(1L, "example@email.com", "password", "John", "Surname", "123123123", null, null);
        Address address = new Address(1L, "Warszawa", "00-001", "Zdrowia", "1", null);
        Clinic clinic = new Clinic(1L, "Zdrowie", address, new HashSet<>());
        UserDto userDto = new UserDto(1L, "example@email.com", "John", "Surname", "123123123");
        AddressDto addressDto = new AddressDto(1L, "Warszawa", "00-001", "Zdrowia", "1");
        ClinicDto clinicDto = new ClinicDto(1L, "Zdrowie", addressDto);
        CreateDoctorCommand command = new CreateDoctorCommand(1L, "Kardiolog", Set.of(1L));
        Doctor savedDoctor = new Doctor(1L, "Kardiolog", user, new HashSet<>(Set.of(clinic)), List.of());
        when(userRepository.findById(command.userId())).thenReturn(Optional.of(user));
        when(clinicRepository.findAllById(command.clinicIds())).thenReturn(List.of(clinic));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(savedDoctor);
        //when
        DoctorDto doctorDto = doctorService.create(command);
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, doctorDto.id()),
                () -> Assertions.assertEquals("Kardiolog", doctorDto.specialization()),
                () -> Assertions.assertEquals(userDto, doctorDto.user()),
                () -> Assertions.assertEquals(1, doctorDto.clinics().size()),
                () -> Assertions.assertTrue(doctorDto.clinics().contains(clinicDto))
        );
    }

    @Test
    void create_userNotExists_throwsException() {
        //given
        CreateDoctorCommand command = new CreateDoctorCommand(1L, "Kardiolog", Set.of());
        when(userRepository.findById(command.userId())).thenReturn(Optional.empty());
        //when then
        Assertions.assertThrows(UserNotFoundException.class, () -> doctorService.create(command));
    }

    @Test
    void delete_doctorExistsWithoutVisits_doctorDeleted() {
        //given
        User user = new User(1L, "example@email.com", "password", "John", "Surname", "123123123", null, null);
        Doctor doctor = new Doctor(1L, "Kardiolog", user, new HashSet<>(), new ArrayList<>());
        when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
        //when
        doctorService.delete(doctor.getId());
        //then
        Mockito.verify(doctorRepository).delete(doctor);
    }

    @Test
    void delete_doctorHasScheduledVisits_throwsException() {
        //given
        User user = new User(1L, "example@email.com", "password", "John", "Surname", "123123123", null, null);
        Visit visit = new Visit(1L, LocalDateTime.of(2026, 12, 22, 10, 0), LocalDateTime.of(2026, 12, 22, 11, 0), null, null);
        Doctor doctor = new Doctor(1L, "Kardiolog", user, new HashSet<>(), new ArrayList<>(List.of(visit)));
        when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
        //when then
        Assertions.assertThrows(DoctorHasScheduledVisitsException.class, () -> doctorService.delete(doctor.getId()));
    }

    @Test
    void delete_doctorNotExists_throwsException() {
        //given
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());
        //when then
        Assertions.assertThrows(DoctorNotFoundException.class, () -> doctorService.delete(1L));
    }

    @Test
    void findAll_dataCorrect_doctorsReturned() {
        //given
        User user1 = new User(1L, "example1@email.com", "password", "John", "Kowalski", "123123123", null, null);
        User user2 = new User(2L, "example2@email.com", "password", "Anna", "Nowak", "321321321", null, null);
        Doctor doctor1 = new Doctor(1L, "Kardiolog", user1, new HashSet<>(), List.of());
        Doctor doctor2 = new Doctor(2L, "Chirurg", user2, new HashSet<>(), List.of());
        when(doctorRepository.findAll()).thenReturn(List.of(doctor1, doctor2));
        //when
        List<DoctorDto> doctors = doctorService.findAll();
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, doctors.size()),
                () -> Assertions.assertEquals(1L, doctors.getFirst().id()),
                () -> Assertions.assertEquals("Kardiolog", doctors.getFirst().specialization()),
                () -> Assertions.assertEquals(2L, doctors.get(1).id()),
                () -> Assertions.assertEquals("Chirurg", doctors.get(1).specialization())
        );
    }

    @Test
    void findById_doctorExists_doctorReturned() {
        //given
        User user = new User(1L, "example@email.com", "password", "John", "Surname", "123123123", null, null);
        Doctor doctor = new Doctor(1L, "Kardiolog", user, new HashSet<>(), List.of());
        when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
        //when
        DoctorDto doctorDto = doctorService.findById(doctor.getId());
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(doctor.getId(), doctorDto.id()),
                () -> Assertions.assertEquals(doctor.getSpecialization(), doctorDto.specialization())
        );
    }

    @Test
    void findById_doctorNotExists_throwsException() {
        //given
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());
        //when then
        Assertions.assertThrows(DoctorNotFoundException.class, () -> doctorService.findById(1L));
    }

    @Test
    void update_doctorExists_updatedDoctorReturned() {
        //given
        User user = new User(1L, "example@email.com", "password", "John", "Surname", "123123123", null, null);
        Address address = new Address(2L, "Krakow", "30-001", "Zielona", "5", null);
        Clinic newClinic = new Clinic(2L, "Nowa Klinika", address, new HashSet<>());
        Doctor doctor = new Doctor(1L, "Kardiolog", user, new HashSet<>(), new ArrayList<>());
        UpdateDoctorCommand command = new UpdateDoctorCommand("Chirurg", Set.of(2L));
        when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
        when(clinicRepository.findAllById(command.clinicIds())).thenReturn(List.of(newClinic));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);
        //when
        DoctorDto doctorDto = doctorService.update(doctor.getId(), command);
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, doctorDto.id()),
                () -> Assertions.assertEquals("Chirurg", doctorDto.specialization()),
                () -> Assertions.assertEquals(1, doctorDto.clinics().size())
        );
    }

    @Test
    void addClinic_clinicNotAssigned_clinicAdded() {
        //given
        User user = new User(1L, "example@email.com", "password", "John", "Surname", "123123123", null, null);
        Address address = new Address(1L, "Warszawa", "00-001", "Zdrowia", "1", null);
        Clinic clinic = new Clinic(1L, "Zdrowie", address, new HashSet<>());
        Doctor doctor = new Doctor(1L, "Kardiolog", user, new HashSet<>(), new ArrayList<>());
        when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
        when(clinicRepository.findById(clinic.getId())).thenReturn(Optional.of(clinic));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);
        //when
        DoctorDto doctorDto = doctorService.addClinic(doctor.getId(), clinic.getId());
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, doctorDto.clinics().size()),
                () -> Assertions.assertTrue(doctor.getClinics().contains(clinic)),
                () -> Assertions.assertTrue(clinic.getDoctors().contains(doctor))
        );
    }

    @Test
    void addClinic_clinicAlreadyAssigned_throwsException() {
        //given
        User user = new User(1L, "example@email.com", "password", "John", "Surname", "123123123", null, null);
        Address address = new Address(1L, "Warszawa", "00-001", "Zdrowia", "1", null);
        Clinic clinic = new Clinic(1L, "Zdrowie", address, new HashSet<>());
        Doctor doctor = new Doctor(1L, "Kardiolog", user, new HashSet<>(Set.of(clinic)), new ArrayList<>());
        clinic.getDoctors().add(doctor);
        when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
        when(clinicRepository.findById(clinic.getId())).thenReturn(Optional.of(clinic));
        //when then
        Assertions.assertThrows(ClinicAlreadyAssignedException.class, () -> doctorService.addClinic(doctor.getId(), clinic.getId()));
    }

    @Test
    void addClinic_clinicNotExists_throwsException() {
        //given
        User user = new User(1L, "example@email.com", "password", "John", "Surname", "123123123", null, null);
        Doctor doctor = new Doctor(1L, "Kardiolog", user, new HashSet<>(), new ArrayList<>());
        when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
        when(clinicRepository.findById(1L)).thenReturn(Optional.empty());
        //when then
        Assertions.assertThrows(ClinicNotFoundException.class, () -> doctorService.addClinic(doctor.getId(), 1L));
    }

    @Test
    void removeClinic_clinicAssigned_clinicRemoved() {
        //given
        User user = new User(1L, "example@email.com", "password", "John", "Surname", "123123123", null, null);
        Address address = new Address(1L, "Warszawa", "00-001", "Zdrowia", "1", null);
        Clinic clinic = new Clinic(1L, "Zdrowie", address, new HashSet<>());
        Doctor doctor = new Doctor(1L, "Kardiolog", user, new HashSet<>(Set.of(clinic)), new ArrayList<>());
        clinic.getDoctors().add(doctor);
        when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
        when(clinicRepository.findById(clinic.getId())).thenReturn(Optional.of(clinic));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);
        //when
        DoctorDto doctorDto = doctorService.removeClinic(doctor.getId(), clinic.getId());
        //then
        Assertions.assertAll(
                () -> Assertions.assertTrue(doctorDto.clinics().isEmpty()),
                () -> Assertions.assertFalse(doctor.getClinics().contains(clinic)),
                () -> Assertions.assertFalse(clinic.getDoctors().contains(doctor))
        );
    }

    @Test
    void removeClinic_clinicNotAssigned_throwsException() {
        //given
        User user = new User(1L, "example@email.com", "password", "John", "Surname", "123123123", null, null);
        Address address = new Address(1L, "Warszawa", "00-001", "Zdrowia", "1", null);
        Clinic clinic = new Clinic(1L, "Zdrowie", address, new HashSet<>());
        Doctor doctor = new Doctor(1L, "Kardiolog", user, new HashSet<>(), new ArrayList<>());
        when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
        when(clinicRepository.findById(clinic.getId())).thenReturn(Optional.of(clinic));
        //when then
        Assertions.assertThrows(ClinicNotAssignedException.class, () -> doctorService.removeClinic(doctor.getId(), clinic.getId()));
    }

    @Test
    void removeClinic_clinicNotExists_throwsException() {
        //given
        User user = new User(1L, "example@email.com", "password", "John", "Surname", "123123123", null, null);
        Doctor doctor = new Doctor(1L, "Kardiolog", user, new HashSet<>(), new ArrayList<>());
        when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
        when(clinicRepository.findById(1L)).thenReturn(Optional.empty());
        //when then
        Assertions.assertThrows(ClinicNotFoundException.class, () -> doctorService.removeClinic(doctor.getId(), 1L));
    }
}
