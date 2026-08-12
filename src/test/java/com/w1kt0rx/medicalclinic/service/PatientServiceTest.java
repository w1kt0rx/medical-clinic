package com.w1kt0rx.medicalclinic.service;

import com.w1kt0rx.medicalclinic.command.CreatePatientCommand;
import com.w1kt0rx.medicalclinic.command.UpdatePatientCommand;
import com.w1kt0rx.medicalclinic.dto.PatientDto;
import com.w1kt0rx.medicalclinic.dto.UserDto;
import com.w1kt0rx.medicalclinic.mapper.PatientMapper;
import com.w1kt0rx.medicalclinic.mapper.PatientMapperImpl;
import com.w1kt0rx.medicalclinic.mapper.UserMapper;
import com.w1kt0rx.medicalclinic.model.Patient;
import com.w1kt0rx.medicalclinic.model.User;
import com.w1kt0rx.medicalclinic.repository.PatientRepository;
import com.w1kt0rx.medicalclinic.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class PatientServiceTest {

    PatientService patientService;
    PatientRepository patientRepository;
    UserRepository userRepository;
    PatientMapper patientMapper;


    @BeforeEach
    void setup() {
        this.patientRepository = Mockito.mock(PatientRepository.class);
        this.userRepository = Mockito.mock(UserRepository.class);
        this.patientMapper = Mappers.getMapper(PatientMapper.class);
        ReflectionTestUtils.setField(patientMapper, "userMapper", Mappers.getMapper(UserMapper.class));
        this.patientService = new PatientService(patientRepository, userRepository, patientMapper);
    }

    @Test
    void create_dataCorrect_mappedPatientReturned() {
        //given
        User user = new User(1L, "example@email.com", "password", "John", "Surname", "123123123", null, null);
        Patient patient = new Patient(1L, "123123", LocalDate.of(2026, 12, 22), user, List.of());
        UserDto userDto = new UserDto(1L, "example@email.com", "John", "Surname", "123123123");
        CreatePatientCommand command = new CreatePatientCommand(1L, "123123", LocalDate.of(2026, 12, 22));
        when(userRepository.findById(command.userId())).thenReturn(Optional.of(user));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        //when
        PatientDto patientDto = patientService.create(command);
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, patientDto.id()),
                () -> Assertions.assertEquals("123123", patientDto.idCardNo()),
                () -> Assertions.assertEquals(LocalDate.of(2026, 12, 22), patientDto.birthday()),
                () -> Assertions.assertEquals(userDto, patientDto.user()),
                () -> Assertions.assertEquals(userDto.email(), patientDto.user().email()),
                () -> Assertions.assertEquals(userDto.id(), patientDto.user().id()),
                () -> Assertions.assertEquals(userDto.firstName(), patientDto.user().firstName()),
                () -> Assertions.assertEquals(userDto.lastName(), patientDto.user().lastName()),
                () -> Assertions.assertEquals(userDto.phoneNumber(), patientDto.user().phoneNumber()));
    }

    @Test
    void getAllPatients_dataCorrect_patientsReturned() {
        //given
        User user = new User(1L, "example@email.com", "password", "John", "Surname", "123123123", null, null);
        Patient patient1 = new Patient(1L, "123123", LocalDate.of(2026, 12, 22), user, List.of());
        Patient patient2 = new Patient(2L, "456456", LocalDate.of(1995, 5, 10), user, List.of());
        UserDto userDto = new UserDto(1L, "example@email.com", "John", "Surname", "123123123");
        when(patientRepository.findAll()).thenReturn(List.of(patient1, patient2));
        //when
        List<PatientDto> patients = patientService.findAll();
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, patients.size()),
                () -> Assertions.assertEquals(1L, patients.getFirst().id()),
                () -> Assertions.assertEquals("123123", patients.getFirst().idCardNo()),
                () -> Assertions.assertEquals(LocalDate.of(2026, 12, 22), patients.getFirst().birthday()),
                () -> Assertions.assertEquals(userDto, patients.getFirst().user()),
                () -> Assertions.assertEquals(2L, patients.get(1).id()),
                () -> Assertions.assertEquals("456456", patients.get(1).idCardNo()),
                () -> Assertions.assertEquals(LocalDate.of(1995, 5, 10), patients.get(1).birthday()),
                () -> Assertions.assertEquals(userDto, patients.get(1).user())
        );
    }

    @Test
    void findById_patientExists_patientReturned() {
        //given
        User user = new User(1L, "example@email.com", "password", "John", "Surname", "123123123", null, null);
        Patient patient = new Patient(1L, "123123", LocalDate.of(2026, 12, 22), user, List.of());
        UserDto userDto = new UserDto(1L, "example@email.com", "John", "Surname", "123123123");
        when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
        //when
        PatientDto patientDto = patientService.findById(patient.getId());
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(patient.getId(), patientDto.id()),
                () -> Assertions.assertEquals(patient.getIdCardNo(), patientDto.idCardNo()),
                () -> Assertions.assertEquals(patient.getBirthday(), patientDto.birthday()),
                () -> Assertions.assertEquals(userDto, patientDto.user())
        );
    }

    @Test
    void update_patientExists_updatedPatientReturned() {
        //given
        User user = new User(1L, "example@email.com", "password", "John", "Surname", "123123123", null, null);
        Patient patient = new Patient(1L, "123123", LocalDate.of(2026, 12, 22), user, List.of());
        UserDto userDto = new UserDto(1L, "example@email.com", "John", "Surname", "123123123");
        UpdatePatientCommand command = new UpdatePatientCommand(
                "999999",
                LocalDate.of(2000, 1, 1)
        );
        when(patientRepository.findById(patient.getId()))
                .thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class)))
                .thenReturn(patient);
        //when
        PatientDto patientDto = patientService.update(patient.getId(), command);
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(patient.getId(), patientDto.id()),
                () -> Assertions.assertEquals("999999", patientDto.idCardNo()),
                () -> Assertions.assertEquals(LocalDate.of(2000, 1, 1), patientDto.birthday()),
                () -> Assertions.assertEquals(userDto, patientDto.user())
        );
    }

    @Test
    void delete_patientExistsWithoutVisits_patientDeleted() {
        //given
        User user = new User(1L, "example@email.com", "password", "John", "Surname", "123123123", null, null);
        Patient patient = new Patient(1L, "123123", LocalDate.of(2026, 12, 22), user, List.of());
        when(patientRepository.findById(patient.getId()))
                .thenReturn(Optional.of(patient));
        //when
        patientService.delete(patient.getId());
        //then
        Mockito.verify(patientRepository).delete(patient);
    }
}