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
    UserMapper userMapper;

    User user;
    UserDto userDto;
    Patient patient1;
    Patient patient2;

    @BeforeEach
    void setup() {
        this.patientRepository = Mockito.mock(PatientRepository.class);
        this.userRepository = Mockito.mock(UserRepository.class);
        this.patientMapper = Mappers.getMapper(PatientMapper.class);
        ReflectionTestUtils.setField(patientMapper, "userMapper", Mappers.getMapper(UserMapper.class));
        this.patientService = new PatientService(patientRepository, userRepository, patientMapper);
        this.user = new User(1L, "example@email.com", "password", "John", "Surname", "123123123", null, null);
        this.userDto = new UserDto(1L, "example@email.com", "John", "Surname", "123123123");
        this.patient1 = new Patient(1L, "123123", LocalDate.of(2026, 12, 22), user, List.of());
        this.patient2 = new Patient(2L, "456456", LocalDate.of(1995, 5, 10), user, List.of());
    }

    @Test
    void create_dataCorrect_mappedPatientReturned() {
        CreatePatientCommand command = new CreatePatientCommand(1L, "123123", LocalDate.of(2026, 12, 22));
        when(userRepository.findById(command.userId())).thenReturn(Optional.of(user));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient1);

        PatientDto patientDto = patientService.create(command);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, patientDto.id()),
                () -> Assertions.assertEquals("123123", patientDto.idCardNo()),
                () -> Assertions.assertEquals(LocalDate.of(2026, 12, 22), patientDto.birthday()),
                () -> Assertions.assertEquals(userDto,patientDto.user()),
                () -> Assertions.assertEquals(userDto.email(),patientDto.user().email()),
                () -> Assertions.assertEquals(userDto.id(),patientDto.user().id()),
                () -> Assertions.assertEquals(userDto.firstName(),patientDto.user().firstName()),
                () -> Assertions.assertEquals(userDto.lastName(),patientDto.user().lastName()),
                () -> Assertions.assertEquals(userDto.phoneNumber(),patientDto.user().phoneNumber()));
    }

    @Test
    void getAllPatients_dataCorrect_patientsReturned() {
        when(patientRepository.findAll()).thenReturn(List.of(patient1, patient2));

        List<PatientDto> patients = patientService.findAll();

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
        when(patientRepository.findById(patient1.getId())).thenReturn(Optional.of(patient1));

        PatientDto patientDto = patientService.findById(patient1.getId());

        Assertions.assertAll(
                () -> Assertions.assertEquals(patient1.getId(), patientDto.id()),
                () -> Assertions.assertEquals(patient1.getIdCardNo(), patientDto.idCardNo()),
                () -> Assertions.assertEquals(patient1.getBirthday(), patientDto.birthday()),
                () -> Assertions.assertEquals(userDto, patientDto.user())
        );
    }

    @Test
    void update_patientExists_updatedPatientReturned() {
        UpdatePatientCommand command = new UpdatePatientCommand(
                "999999",
                LocalDate.of(2000, 1, 1)
        );
        when(patientRepository.findById(patient1.getId()))
                .thenReturn(Optional.of(patient1));
        when(patientRepository.save(any(Patient.class)))
                .thenReturn(patient1);

        PatientDto patientDto = patientService.update(patient1.getId(), command);

        Assertions.assertAll(
                () -> Assertions.assertEquals(patient1.getId(), patientDto.id()),
                () -> Assertions.assertEquals("999999", patientDto.idCardNo()),
                () -> Assertions.assertEquals(LocalDate.of(2000, 1, 1), patientDto.birthday()),
                () -> Assertions.assertEquals(userDto, patientDto.user())
        );
    }

    @Test
    void delete_patientExistsWithoutVisits_patientDeleted() {
        when(patientRepository.findById(patient1.getId()))
                .thenReturn(Optional.of(patient1));

        patientService.delete(patient1.getId());

        Mockito.verify(patientRepository).delete(patient1);
    }
}