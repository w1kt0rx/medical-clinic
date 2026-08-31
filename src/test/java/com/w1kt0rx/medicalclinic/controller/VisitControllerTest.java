package com.w1kt0rx.medicalclinic.controller;

import com.w1kt0rx.medicalclinic.command.CreateVisitCommand;
import com.w1kt0rx.medicalclinic.command.RegisterPatientForVisitCommand;
import com.w1kt0rx.medicalclinic.dto.*;
import com.w1kt0rx.medicalclinic.exception.DoctorNotFoundException;
import com.w1kt0rx.medicalclinic.exception.IllegalDateException;
import com.w1kt0rx.medicalclinic.exception.PatientNotFoundException;
import com.w1kt0rx.medicalclinic.exception.VisitAlreadyReservedException;
import com.w1kt0rx.medicalclinic.exception.VisitNotFoundException;
import com.w1kt0rx.medicalclinic.exception.VisitOverlapException;
import com.w1kt0rx.medicalclinic.service.VisitService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class VisitControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    VisitService visitService;

    @Test
    void create_dataCorrect_returnsCreatedVisit() throws Exception {
        //given
        UserDto userDto = new UserDto(1L, "example@email.com", "John", "Surname", "123123123");
        DoctorDto doctorDto = new DoctorDto(1L, "Kardiolog", userDto, Set.of());
        LocalDateTime start = LocalDateTime.of(2026, 12, 22, 10, 0);
        LocalDateTime finish = LocalDateTime.of(2026, 12, 22, 11, 0);
        CreateVisitCommand command = new CreateVisitCommand(1L, start, finish);
        VisitDto visitDto = new VisitDto(1L, start, doctorDto, null);
        ArgumentCaptor<CreateVisitCommand> commandCaptor = ArgumentCaptor.forClass(CreateVisitCommand.class);
        when(visitService.create(any(CreateVisitCommand.class))).thenReturn(visitDto);
        //when + then
        mockMvc.perform(post("/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.doctor.id").value(1L))
                .andExpect(jsonPath("$.patient").isEmpty());
        Mockito.verify(visitService).create(commandCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, commandCaptor.getValue().doctorId()),
                () -> Assertions.assertEquals(start, commandCaptor.getValue().startDate()),
                () -> Assertions.assertEquals(finish, commandCaptor.getValue().finishDate())
        );
    }

    @Test
    void create_startDateInPast_returnsConflictWithMessage() throws Exception {
        //given
        CreateVisitCommand command = new CreateVisitCommand(1L, LocalDateTime.of(2020, 1, 1, 10, 0), LocalDateTime.of(2020, 1, 1, 11, 0));
        when(visitService.create(any(CreateVisitCommand.class))).thenThrow(
                new IllegalDateException("Cannot create visit in the past", HttpStatus.CONFLICT));
        //when + then
        mockMvc.perform(post("/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", Matchers.containsString("Cannot create visit in the past")));
    }

    @Test
    void create_doctorNotExists_returnsNotFoundWithMessage() throws Exception {
        //given
        CreateVisitCommand command = new CreateVisitCommand(1L, LocalDateTime.of(2026, 12, 22, 10, 0), LocalDateTime.of(2026, 12, 22, 11, 0));
        when(visitService.create(any(CreateVisitCommand.class))).thenThrow(new DoctorNotFoundException(1L));
        //when + then
        mockMvc.perform(post("/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.containsString("Couldn't find doctor with id: 1")));
    }

    @Test
    void create_visitOverlaps_returnsConflictWithMessage() throws Exception {
        //given
        CreateVisitCommand command = new CreateVisitCommand(1L, LocalDateTime.of(2026, 12, 22, 10, 0), LocalDateTime.of(2026, 12, 22, 11, 0));
        when(visitService.create(any(CreateVisitCommand.class))).thenThrow(
                new VisitOverlapException("Doctor has visit booked on this term", HttpStatus.CONFLICT));
        //when + then
        mockMvc.perform(post("/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", Matchers.containsString("Doctor has visit booked on this term")));
    }

    @Test
    void registerPatient_dataCorrect_returnsVisitWithPatient() throws Exception {
        //given
        UserDto userDto = new UserDto(1L, "example@email.com", "John", "Surname", "123123123");
        DoctorDto doctorDto = new DoctorDto(1L, "Kardiolog", userDto, Set.of());
        PatientDto patientDto = new PatientDto(1L, "123123", LocalDate.of(1990, 1, 1), userDto);
        RegisterPatientForVisitCommand command = new RegisterPatientForVisitCommand(1L);
        VisitDto visitDto = new VisitDto(1L, LocalDateTime.of(2026, 12, 22, 10, 0), doctorDto, patientDto);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<RegisterPatientForVisitCommand> commandCaptor = ArgumentCaptor.forClass(RegisterPatientForVisitCommand.class);
        when(visitService.registerPatient(anyLong(), any(RegisterPatientForVisitCommand.class))).thenReturn(visitDto);
        //when + then
        mockMvc.perform(put("/visits/{id}/register", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patient.id").value(1L));
        Mockito.verify(visitService).registerPatient(idCaptor.capture(), commandCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, idCaptor.getValue()),
                () -> Assertions.assertEquals(1L, commandCaptor.getValue().patientId())
        );
    }

    @Test
    void registerPatient_visitNotExists_returnsNotFoundWithMessage() throws Exception {
        //given
        RegisterPatientForVisitCommand command = new RegisterPatientForVisitCommand(1L);
        when(visitService.registerPatient(anyLong(), any(RegisterPatientForVisitCommand.class)))
                .thenThrow(new VisitNotFoundException(1L));
        //when + then
        mockMvc.perform(put("/visits/{id}/register", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.containsString("Couldn't find visit with id: 1")));
    }

    @Test
    void registerPatient_visitAlreadyReserved_returnsConflictWithMessage() throws Exception {
        //given
        RegisterPatientForVisitCommand command = new RegisterPatientForVisitCommand(1L);
        when(visitService.registerPatient(anyLong(), any(RegisterPatientForVisitCommand.class)))
                .thenThrow(new VisitAlreadyReservedException("This visit is already booked", HttpStatus.CONFLICT));
        //when + then
        mockMvc.perform(put("/visits/{id}/register", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", Matchers.containsString("This visit is already booked")));
    }

    @Test
    void registerPatient_patientNotExists_returnsNotFoundWithMessage() throws Exception {
        //given
        RegisterPatientForVisitCommand command = new RegisterPatientForVisitCommand(1L);
        when(visitService.registerPatient(anyLong(), any(RegisterPatientForVisitCommand.class)))
                .thenThrow(new PatientNotFoundException(1L));
        //when + then
        mockMvc.perform(put("/visits/{id}/register", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.containsString("Couldn't find patient with id: 1")));
    }

    @Test
    void findById_visitExists_returnsVisit() throws Exception {
        //given
        UserDto userDto = new UserDto(1L, "example@email.com", "John", "Surname", "123123123");
        DoctorDto doctorDto = new DoctorDto(1L, "Kardiolog", userDto, Set.of());
        VisitDto visitDto = new VisitDto(1L, LocalDateTime.of(2026, 12, 22, 10, 0), doctorDto, null);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        when(visitService.findById(1L)).thenReturn(visitDto);
        //when + then
        mockMvc.perform(get("/visits/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
        Mockito.verify(visitService).findById(idCaptor.capture());
        Assertions.assertEquals(1L, idCaptor.getValue());
    }

    @Test
    void findById_visitNotExists_returnsNotFoundWithMessage() throws Exception {
        //given
        when(visitService.findById(1L)).thenThrow(new VisitNotFoundException(1L));
        //when + then
        mockMvc.perform(get("/visits/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.containsString("Couldn't find visit with id: 1")));
    }

    @Test
    void delete_visitExists_returnsNoContent() throws Exception {
        //given
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        //when + then
        mockMvc.perform(delete("/visits/{id}", 1L))
                .andExpect(status().isNoContent());
        Mockito.verify(visitService).delete(idCaptor.capture());
        Assertions.assertEquals(1L, idCaptor.getValue());
    }

    @Test
    void delete_visitNotExists_returnsNotFoundWithMessage() throws Exception {
        //given
        Mockito.doThrow(new VisitNotFoundException(1L)).when(visitService).delete(1L);
        //when + then
        mockMvc.perform(delete("/visits/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.containsString("Couldn't find visit with id: 1")));
    }

    @Test
    void findAll_dataCorrect_returnsPageOfVisits() throws Exception {
        //given
        UserDto userDto = new UserDto(1L, "example@email.com", "John", "Surname", "123123123");
        DoctorDto doctorDto = new DoctorDto(1L, "Kardiolog", userDto, Set.of());
        VisitDto visit1 = new VisitDto(1L, LocalDateTime.of(2026, 12, 22, 10, 0), doctorDto, null);
        VisitDto visit2 = new VisitDto(2L, LocalDateTime.of(2026, 12, 23, 10, 0), doctorDto, null);
        Pageable pageable = PageRequest.of(0, 20, Sort.by("id"));
        PageDto<VisitDto> page = PageDto.from(new PageImpl<>(List.of(visit1, visit2), pageable, 2));
        ArgumentCaptor<PageRequestDto> dtoCaptor = ArgumentCaptor.forClass(PageRequestDto.class);
        when(visitService.findAll(any(PageRequestDto.class))).thenReturn(page);
        //when + then
        mockMvc.perform(get("/visits")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sortBy", "id")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.totalElements").value(2));
        Mockito.verify(visitService).findAll(dtoCaptor.capture());
        Assertions.assertEquals(0, dtoCaptor.getValue().page());
    }

    @Test
    void findFreeVisits_dataCorrect_returnsPageOfFreeVisits() throws Exception {
        //given
        UserDto userDto = new UserDto(1L, "example@email.com", "John", "Surname", "123123123");
        DoctorDto doctorDto = new DoctorDto(1L, "Kardiolog", userDto, Set.of());
        VisitDto visit = new VisitDto(1L, LocalDateTime.of(2026, 12, 22, 10, 0), doctorDto, null);
        Pageable pageable = PageRequest.of(0, 20, Sort.by("id"));
        PageDto<VisitDto> page = PageDto.from(new PageImpl<>(List.of(visit), pageable, 1));
        ArgumentCaptor<PageRequestDto> dtoCaptor = ArgumentCaptor.forClass(PageRequestDto.class);
        when(visitService.findFreeVisits(any(PageRequestDto.class))).thenReturn(page);
        //when + then
        mockMvc.perform(get("/visits/free"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].patient").isEmpty());
        Mockito.verify(visitService).findFreeVisits(dtoCaptor.capture());
    }

    @Test
    void findPatientVisits_dataCorrect_returnsPageOfVisits() throws Exception {
        //given
        UserDto userDto = new UserDto(1L, "example@email.com", "John", "Surname", "123123123");
        DoctorDto doctorDto = new DoctorDto(1L, "Kardiolog", userDto, Set.of());
        PatientDto patientDto = new PatientDto(1L, "123123", LocalDate.of(1990, 1, 1), userDto);
        VisitDto visitDto = new VisitDto(1L, LocalDateTime.of(2026, 12, 22, 10, 0), doctorDto, patientDto);
        Pageable pageable = PageRequest.of(0, 20, Sort.by("id"));
        PageDto<VisitDto> page = PageDto.from(new PageImpl<>(List.of(visitDto), pageable, 1));
        ArgumentCaptor<Long> patientIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<PageRequestDto> dtoCaptor = ArgumentCaptor.forClass(PageRequestDto.class);
        when(visitService.findPatientVisits(org.mockito.ArgumentMatchers.anyLong(), any(PageRequestDto.class))).thenReturn(page);
        //when + then
        mockMvc.perform(get("/visits/patient/{patientId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].patient.id").value(1L));
        Mockito.verify(visitService).findPatientVisits(patientIdCaptor.capture(), dtoCaptor.capture());
        Assertions.assertEquals(1L, patientIdCaptor.getValue());
    }
}