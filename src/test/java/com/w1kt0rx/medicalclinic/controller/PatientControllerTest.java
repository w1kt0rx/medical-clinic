package com.w1kt0rx.medicalclinic.controller;

import com.w1kt0rx.medicalclinic.command.CreatePatientCommand;
import com.w1kt0rx.medicalclinic.command.UpdatePatientCommand;
import com.w1kt0rx.medicalclinic.dto.PageDto;
import com.w1kt0rx.medicalclinic.dto.PageRequestDto;
import com.w1kt0rx.medicalclinic.dto.PatientDto;
import com.w1kt0rx.medicalclinic.dto.UserDto;
import com.w1kt0rx.medicalclinic.exception.PatientNotFoundException;
import com.w1kt0rx.medicalclinic.service.PatientService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PatientControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    PatientService patientService;

    @Test
    void create_dataCorrect_returnsCreatedPatient() throws Exception {
        //given
        UserDto userDto = new UserDto(1L, "example@email.com", "John", "Surname", "123123123");
        CreatePatientCommand command = new CreatePatientCommand(1L, "123123", LocalDate.of(2026, 12, 22));
        PatientDto patientDto = new PatientDto(1L, "123123", LocalDate.of(2026, 12, 22), userDto);
        ArgumentCaptor<CreatePatientCommand> commandCaptor = ArgumentCaptor.forClass(CreatePatientCommand.class);
        when(patientService.create(any(CreatePatientCommand.class))).thenReturn(patientDto);
        //when + then
        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.idCardNo").value("123123"))
                .andExpect(jsonPath("$.birthday").value("2026-12-22"))
                .andExpect(jsonPath("$.user.email").value("example@email.com"));
        Mockito.verify(patientService).create(commandCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals("123123", commandCaptor.getValue().idCardNo()),
                () -> Assertions.assertEquals(LocalDate.of(2026, 12, 22), commandCaptor.getValue().birthday())
        );
    }

    @Test
    void findAll_dataCorrect_returnsPageOfPatients() throws Exception {
        //given
        UserDto userDto = new UserDto(1L, "example@email.com", "John", "Surname", "123123123");
        PatientDto patient1 = new PatientDto(1L, "123123", LocalDate.of(2026, 12, 22), userDto);
        PatientDto patient2 = new PatientDto(2L, "456456", LocalDate.of(1995, 5, 10), userDto);
        Pageable pageable = PageRequest.of(0, 20, Sort.by("id"));
        PageDto<PatientDto> page = PageDto.from(new PageImpl<>(List.of(patient1, patient2), pageable, 2));
        ArgumentCaptor<PageRequestDto> dtoCaptor = ArgumentCaptor.forClass(PageRequestDto.class);
        when(patientService.findAll(any(PageRequestDto.class))).thenReturn(page);
        //when + then
        mockMvc.perform(get("/patients")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sortBy", "id")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[1].id").value(2L))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20));
        Mockito.verify(patientService).findAll(dtoCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals(0, dtoCaptor.getValue().page()),
                () -> Assertions.assertEquals(20, dtoCaptor.getValue().size()),
                () -> Assertions.assertEquals("id", dtoCaptor.getValue().sortBy()),
                () -> Assertions.assertEquals("asc", dtoCaptor.getValue().direction())
        );
    }

    @Test
    void findAll_noParamsProvided_stillReturnsOk() throws Exception {
        //given
        PageDto<PatientDto> emptyPage = PageDto.from(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));
        ArgumentCaptor<PageRequestDto> dtoCaptor = ArgumentCaptor.forClass(PageRequestDto.class);
        when(patientService.findAll(any(PageRequestDto.class))).thenReturn(emptyPage);
        //when + then
        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk());
        Mockito.verify(patientService).findAll(dtoCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertNull(dtoCaptor.getValue().page()),
                () -> Assertions.assertNull(dtoCaptor.getValue().size()),
                () -> Assertions.assertNull(dtoCaptor.getValue().sortBy()),
                () -> Assertions.assertNull(dtoCaptor.getValue().direction())
        );
    }

    @Test
    void findById_patientExists_returnsPatient() throws Exception {
        //given
        UserDto userDto = new UserDto(1L, "example@email.com", "John", "Surname", "123123123");
        PatientDto patientDto = new PatientDto(1L, "123123", LocalDate.of(2026, 12, 22), userDto);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        when(patientService.findById(1L)).thenReturn(patientDto);
        //when + then
        mockMvc.perform(get("/patients/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.idCardNo").value("123123"))
                .andExpect(jsonPath("$.user.email").value("example@email.com"));
        Mockito.verify(patientService).findById(idCaptor.capture());
        Assertions.assertEquals(1L, idCaptor.getValue());
    }

    @Test
    void findById_patientNotExists_returnsNotFound() throws Exception {
        //given
        when(patientService.findById(1L)).thenThrow(new PatientNotFoundException(1L));
        //when + then
        mockMvc.perform(get("/patients/{id}", 1L))
                .andExpect(status().isNotFound());
        Mockito.verify(patientService).findById(1L);
    }

    @Test
    void update_dataCorrect_returnsUpdatedPatient() throws Exception {
        //given
        UserDto userDto = new UserDto(1L, "example@email.com", "John", "Surname", "123123123");
        UpdatePatientCommand command = new UpdatePatientCommand("999999", LocalDate.of(2000, 1, 1));
        PatientDto patientDto = new PatientDto(1L, "999999", LocalDate.of(2000, 1, 1), userDto);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<UpdatePatientCommand> commandCaptor = ArgumentCaptor.forClass(UpdatePatientCommand.class);
        when(patientService.update(anyLong(), any(UpdatePatientCommand.class))).thenReturn(patientDto);
        //when + then
        mockMvc.perform(put("/patients/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.idCardNo").value("999999"))
                .andExpect(jsonPath("$.birthday").value("2000-01-01"));
        Mockito.verify(patientService).update(idCaptor.capture(), commandCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, idCaptor.getValue()),
                () -> Assertions.assertEquals("999999", commandCaptor.getValue().idCardNo()),
                () -> Assertions.assertEquals(LocalDate.of(2000, 1, 1), commandCaptor.getValue().birthday())
        );
    }

    @Test
    void update_patientNotExists_returnsNotFound() throws Exception {
        //given
        UpdatePatientCommand command = new UpdatePatientCommand("999999", LocalDate.of(2000, 1, 1));
        when(patientService.update(anyLong(), any(UpdatePatientCommand.class))).thenThrow(new PatientNotFoundException(1L));
        //when + then
        mockMvc.perform(put("/patients/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_patientExists_returnsNoContent() throws Exception {
        //given
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        //when + then
        mockMvc.perform(delete("/patients/{id}", 1L))
                .andExpect(status().isNoContent());
        Mockito.verify(patientService).delete(idCaptor.capture());
        Assertions.assertEquals(1L, idCaptor.getValue());
    }

    @Test
    void delete_patientNotExists_returnsNotFound() throws Exception {
        //given
        Mockito.doThrow(new PatientNotFoundException(1L)).when(patientService).delete(1L);
        //when + then
        mockMvc.perform(delete("/patients/{id}", 1L))
                .andExpect(status().isNotFound());
        Mockito.verify(patientService).delete(1L);
    }
}