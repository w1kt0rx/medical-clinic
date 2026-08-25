package com.w1kt0rx.medicalclinic.controller;

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
import com.w1kt0rx.medicalclinic.service.DoctorService;
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

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DoctorControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    DoctorService doctorService;

    @Test
    void create_dataCorrect_returnsCreatedDoctor() throws Exception {
        //given
        UserDto userDto = new UserDto(1L, "example@email.com", "John", "Surname", "123123123");
        AddressDto addressDto = new AddressDto(1L, "Warszawa", "00-001", "Zdrowia", "1");
        ClinicDto clinicDto = new ClinicDto(1L, "Zdrowie", addressDto);
        CreateDoctorCommand command = new CreateDoctorCommand(1L, "Kardiolog", Set.of(1L));
        DoctorDto doctorDto = new DoctorDto(1L, "Kardiolog", userDto, Set.of(clinicDto));
        ArgumentCaptor<CreateDoctorCommand> commandCaptor = ArgumentCaptor.forClass(CreateDoctorCommand.class);
        when(doctorService.create(any(CreateDoctorCommand.class))).thenReturn(doctorDto);
        //when + then
        mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.specialization").value("Kardiolog"))
                .andExpect(jsonPath("$.user.email").value("example@email.com"))
                .andExpect(jsonPath("$.clinics.length()").value(1));
        Mockito.verify(doctorService).create(commandCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals("Kardiolog", commandCaptor.getValue().specialization()),
                () -> Assertions.assertEquals(1L, commandCaptor.getValue().userId())
        );
    }

    @Test
    void create_userNotExists_returnsNotFoundWithMessage() throws Exception {
        //given
        CreateDoctorCommand command = new CreateDoctorCommand(1L, "Kardiolog", Set.of());
        when(doctorService.create(any(CreateDoctorCommand.class))).thenThrow(new UserNotFoundException(1L));
        //when + then
        mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.containsString("Couldn't find user with id: 1")));
    }

    @Test
    void delete_doctorExists_returnsNoContent() throws Exception {
        //given
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        //when + then
        mockMvc.perform(delete("/doctors/{id}", 1L))
                .andExpect(status().isNoContent());
        Mockito.verify(doctorService).delete(idCaptor.capture());
        Assertions.assertEquals(1L, idCaptor.getValue());
    }

    @Test
    void delete_doctorNotExists_returnsNotFoundWithMessage() throws Exception {
        //given
        Mockito.doThrow(new DoctorNotFoundException(1L)).when(doctorService).delete(1L);
        //when + then
        mockMvc.perform(delete("/doctors/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.containsString("Couldn't find doctor with id: 1")));
    }

    @Test
    void delete_doctorHasScheduledVisits_returnsInternalServerError() throws Exception {
        //given
        Mockito.doThrow(new DoctorHasScheduledVisitsException(1L)).when(doctorService).delete(1L);
        //when + then
        mockMvc.perform(delete("/doctors/{id}", 1L))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void findAll_dataCorrect_returnsPageOfDoctors() throws Exception {
        //given
        UserDto userDto = new UserDto(1L, "example@email.com", "John", "Surname", "123123123");
        DoctorDto doctor1 = new DoctorDto(1L, "Kardiolog", userDto, Set.of());
        DoctorDto doctor2 = new DoctorDto(2L, "Chirurg", userDto, Set.of());
        Pageable pageable = PageRequest.of(0, 20, Sort.by("id"));
        Page<DoctorDto> page = new PageImpl<>(List.of(doctor1, doctor2), pageable, 2);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(doctorService.findAll(any(Pageable.class))).thenReturn(page);
        //when + then
        mockMvc.perform(get("/doctors")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "id,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[1].id").value(2L))
                .andExpect(jsonPath("$.page.totalElements").value(2));
        Mockito.verify(doctorService).findAll(pageableCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals(0, pageableCaptor.getValue().getPageNumber()),
                () -> Assertions.assertEquals(20, pageableCaptor.getValue().getPageSize())
        );
    }

    @Test
    void findById_doctorExists_returnsDoctor() throws Exception {
        //given
        UserDto userDto = new UserDto(1L, "example@email.com", "John", "Surname", "123123123");
        AddressDto addressDto = new AddressDto(1L, "Warszawa", "00-001", "Zdrowia", "1");
        ClinicDto clinicDto = new ClinicDto(1L, "Zdrowie", addressDto);
        DoctorDto doctorDto = new DoctorDto(1L, "Kardiolog", userDto, Set.of(clinicDto));
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        when(doctorService.findById(1L)).thenReturn(doctorDto);
        //when + then
        mockMvc.perform(get("/doctors/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.specialization").value("Kardiolog"));
        Mockito.verify(doctorService).findById(idCaptor.capture());
        Assertions.assertEquals(1L, idCaptor.getValue());
    }

    @Test
    void findById_doctorNotExists_returnsNotFoundWithMessage() throws Exception {
        //given
        when(doctorService.findById(1L)).thenThrow(new DoctorNotFoundException(1L));
        //when + then
        mockMvc.perform(get("/doctors/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.containsString("Couldn't find doctor with id: 1")));
    }

    @Test
    void update_dataCorrect_returnsUpdatedDoctor() throws Exception {
        //given
        UserDto userDto = new UserDto(1L, "example@email.com", "John", "Surname", "123123123");
        AddressDto addressDto = new AddressDto(1L, "Warszawa", "00-001", "Zdrowia", "1");
        ClinicDto clinicDto = new ClinicDto(1L, "Zdrowie", addressDto);
        UpdateDoctorCommand command = new UpdateDoctorCommand("Chirurg", Set.of(1L));
        DoctorDto doctorDto = new DoctorDto(1L, "Chirurg", userDto, Set.of(clinicDto));
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<UpdateDoctorCommand> commandCaptor = ArgumentCaptor.forClass(UpdateDoctorCommand.class);
        when(doctorService.update(anyLong(), any(UpdateDoctorCommand.class))).thenReturn(doctorDto);
        //when + then
        mockMvc.perform(put("/doctors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.specialization").value("Chirurg"));
        Mockito.verify(doctorService).update(idCaptor.capture(), commandCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, idCaptor.getValue()),
                () -> Assertions.assertEquals("Chirurg", commandCaptor.getValue().specialization())
        );
    }

    @Test
    void update_doctorNotExists_returnsNotFoundWithMessage() throws Exception {
        //given
        UpdateDoctorCommand command = new UpdateDoctorCommand("Chirurg", Set.of());
        when(doctorService.update(anyLong(), any(UpdateDoctorCommand.class))).thenThrow(new DoctorNotFoundException(1L));
        //when + then
        mockMvc.perform(put("/doctors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.containsString("Couldn't find doctor with id: 1")));
    }

    @Test
    void addClinic_dataCorrect_returnsDoctorWithClinic() throws Exception {
        //given
        UserDto userDto = new UserDto(1L, "example@email.com", "John", "Surname", "123123123");
        AddressDto addressDto = new AddressDto(1L, "Warszawa", "00-001", "Zdrowia", "1");
        ClinicDto clinicDto = new ClinicDto(1L, "Zdrowie", addressDto);
        DoctorDto doctorDto = new DoctorDto(1L, "Kardiolog", userDto, Set.of(clinicDto));
        ArgumentCaptor<Long> doctorIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> clinicIdCaptor = ArgumentCaptor.forClass(Long.class);
        when(doctorService.addClinic(1L, 1L)).thenReturn(doctorDto);
        //when + then
        mockMvc.perform(post("/doctors/{doctorId}/clinics/{clinicId}", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clinics.length()").value(1));
        Mockito.verify(doctorService).addClinic(doctorIdCaptor.capture(), clinicIdCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, doctorIdCaptor.getValue()),
                () -> Assertions.assertEquals(1L, clinicIdCaptor.getValue())
        );
    }

    @Test
    void addClinic_clinicAlreadyAssigned_returnsConflictWithMessage() throws Exception {
        //given
        when(doctorService.addClinic(1L, 1L)).thenThrow(
                new ClinicAlreadyAssignedException("Doctor is already assigned to this clinic", HttpStatus.CONFLICT));
        //when + then
        mockMvc.perform(post("/doctors/{doctorId}/clinics/{clinicId}", 1L, 1L))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", Matchers.containsString("Doctor is already assigned to this clinic")));
    }

    @Test
    void addClinic_clinicNotExists_returnsNotFoundWithMessage() throws Exception {
        //given
        when(doctorService.addClinic(1L, 1L)).thenThrow(new ClinicNotFoundException(1L));
        //when + then
        mockMvc.perform(post("/doctors/{doctorId}/clinics/{clinicId}", 1L, 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.containsString("Couldn't find clinic with id: 1")));
    }

    @Test
    void removeClinic_dataCorrect_returnsDoctorWithoutClinic() throws Exception {
        //given
        UserDto userDto = new UserDto(1L, "example@email.com", "John", "Surname", "123123123");
        DoctorDto doctorDto = new DoctorDto(1L, "Kardiolog", userDto, Set.of());
        ArgumentCaptor<Long> doctorIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> clinicIdCaptor = ArgumentCaptor.forClass(Long.class);
        when(doctorService.removeClinic(1L, 1L)).thenReturn(doctorDto);
        //when + then
        mockMvc.perform(delete("/doctors/{doctorId}/clinics/{clinicId}", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clinics.length()").value(0));
        Mockito.verify(doctorService).removeClinic(doctorIdCaptor.capture(), clinicIdCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, doctorIdCaptor.getValue()),
                () -> Assertions.assertEquals(1L, clinicIdCaptor.getValue())
        );
    }

    @Test
    void removeClinic_clinicNotAssigned_returnsNotFoundWithMessage() throws Exception {
        //given
        when(doctorService.removeClinic(1L, 1L)).thenThrow(
                new ClinicNotAssignedException("Doctor was not assigned to this clinic", HttpStatus.NOT_FOUND));
        //when + then
        mockMvc.perform(delete("/doctors/{doctorId}/clinics/{clinicId}", 1L, 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.containsString("Doctor was not assigned to this clinic")));
    }

    @Test
    void removeClinic_clinicNotExists_returnsNotFoundWithMessage() throws Exception {
        //given
        when(doctorService.removeClinic(1L, 1L)).thenThrow(new ClinicNotFoundException(1L));
        //when + then
        mockMvc.perform(delete("/doctors/{doctorId}/clinics/{clinicId}", 1L, 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.containsString("Couldn't find clinic with id: 1")));
    }
}