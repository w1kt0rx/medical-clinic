package com.w1kt0rx.medicalclinic.controller;

import com.w1kt0rx.medicalclinic.command.CreateClinicCommand;
import com.w1kt0rx.medicalclinic.command.UpdateClinicCommand;
import com.w1kt0rx.medicalclinic.dto.AddressDto;
import com.w1kt0rx.medicalclinic.dto.ClinicDto;
import com.w1kt0rx.medicalclinic.dto.PageDto;
import com.w1kt0rx.medicalclinic.dto.PageRequestDto;
import com.w1kt0rx.medicalclinic.exception.ClinicAlreadyExistsException;
import com.w1kt0rx.medicalclinic.exception.ClinicNotFoundException;
import com.w1kt0rx.medicalclinic.service.ClinicService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ClinicControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    ClinicService clinicService;

    @Test
    void create_dataCorrect_returnsCreatedClinic() throws Exception {
        //given
        CreateClinicCommand command = new CreateClinicCommand("Zdrowie", "Warszawa", "00-001", "Zdrowia", "1");
        AddressDto addressDto = new AddressDto(1L, "Warszawa", "00-001", "Zdrowia", "1");
        ClinicDto clinicDto = new ClinicDto(1L, "Zdrowie", addressDto);
        ArgumentCaptor<CreateClinicCommand> commandCaptor = ArgumentCaptor.forClass(CreateClinicCommand.class);
        when(clinicService.create(any(CreateClinicCommand.class))).thenReturn(clinicDto);
        //when + then
        mockMvc.perform(post("/clinics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Zdrowie"))
                .andExpect(jsonPath("$.address.city").value("Warszawa"));
        Mockito.verify(clinicService).create(commandCaptor.capture());
        Assertions.assertEquals("Zdrowie", commandCaptor.getValue().name());
    }

    @Test
    void create_clinicAlreadyExists_returnsConflictWithMessage() throws Exception {
        //given
        CreateClinicCommand command = new CreateClinicCommand("Zdrowie", "Warszawa", "00-001", "Zdrowia", "1");
        when(clinicService.create(any(CreateClinicCommand.class)))
                .thenThrow(new ClinicAlreadyExistsException("Clinic already exists", HttpStatus.CONFLICT));
        //when + then
        mockMvc.perform(post("/clinics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", Matchers.containsString("Clinic already exists")));
    }

    @Test
    void delete_clinicExists_returnsNoContent() throws Exception {
        //given
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        //when + then
        mockMvc.perform(delete("/clinics/{id}", 1L))
                .andExpect(status().isNoContent());
        Mockito.verify(clinicService).delete(idCaptor.capture());
        Assertions.assertEquals(1L, idCaptor.getValue());
    }

    @Test
    void delete_clinicNotExists_returnsNotFoundWithMessage() throws Exception {
        //given
        Mockito.doThrow(new ClinicNotFoundException(1L)).when(clinicService).delete(1L);
        //when + then
        mockMvc.perform(delete("/clinics/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.containsString("Couldn't find clinic with id: 1")));
    }

    @Test
    void findAll_dataCorrect_returnsPageOfClinics() throws Exception {
        //given
        AddressDto address1 = new AddressDto(1L, "Warszawa", "00-001", "Zdrowia", "1");
        AddressDto address2 = new AddressDto(2L, "Krakow", "30-001", "Zielona", "5");
        ClinicDto clinic1 = new ClinicDto(1L, "Zdrowie", address1);
        ClinicDto clinic2 = new ClinicDto(2L, "Nowa Klinika", address2);
        Pageable pageable = PageRequest.of(0, 20, Sort.by("id"));
        PageDto<ClinicDto> page = PageDto.from(new PageImpl<>(List.of(clinic1, clinic2), pageable, 2));
        ArgumentCaptor<PageRequestDto> dtoCaptor = ArgumentCaptor.forClass(PageRequestDto.class);
        when(clinicService.findAll(any(PageRequestDto.class))).thenReturn(page);
        //when + then
        mockMvc.perform(get("/clinics")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sortBy", "id")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name").value("Zdrowie"))
                .andExpect(jsonPath("$.content[1].name").value("Nowa Klinika"))
                .andExpect(jsonPath("$.totalElements").value(2));
        Mockito.verify(clinicService).findAll(dtoCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals(0, dtoCaptor.getValue().page()),
                () -> Assertions.assertEquals(20, dtoCaptor.getValue().size())
        );
    }

    @Test
    void findById_clinicExists_returnsClinic() throws Exception {
        //given
        AddressDto addressDto = new AddressDto(1L, "Warszawa", "00-001", "Zdrowia", "1");
        ClinicDto clinicDto = new ClinicDto(1L, "Zdrowie", addressDto);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        when(clinicService.findById(1L)).thenReturn(clinicDto);
        //when + then
        mockMvc.perform(get("/clinics/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Zdrowie"));
        Mockito.verify(clinicService).findById(idCaptor.capture());
        Assertions.assertEquals(1L, idCaptor.getValue());
    }

    @Test
    void findById_clinicNotExists_returnsNotFoundWithMessage() throws Exception {
        //given
        when(clinicService.findById(1L)).thenThrow(new ClinicNotFoundException(1L));
        //when + then
        mockMvc.perform(get("/clinics/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.containsString("Couldn't find clinic with id: 1")));
    }

    @Test
    void update_dataCorrect_returnsUpdatedClinic() throws Exception {
        //given
        UpdateClinicCommand command = new UpdateClinicCommand("Nowa Nazwa", "Krakow", "30-001", "Zielona", "5");
        AddressDto addressDto = new AddressDto(1L, "Krakow", "30-001", "Zielona", "5");
        ClinicDto clinicDto = new ClinicDto(1L, "Nowa Nazwa", addressDto);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<UpdateClinicCommand> commandCaptor = ArgumentCaptor.forClass(UpdateClinicCommand.class);
        when(clinicService.update(anyLong(), any(UpdateClinicCommand.class))).thenReturn(clinicDto);
        //when + then
        mockMvc.perform(put("/clinics/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nowa Nazwa"));
        Mockito.verify(clinicService).update(idCaptor.capture(), commandCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, idCaptor.getValue()),
                () -> Assertions.assertEquals("Nowa Nazwa", commandCaptor.getValue().name())
        );
    }

    @Test
    void update_clinicNotExists_returnsNotFoundWithMessage() throws Exception {
        //given
        UpdateClinicCommand command = new UpdateClinicCommand("Nowa Nazwa", "Krakow", "30-001", "Zielona", "5");
        when(clinicService.update(anyLong(), any(UpdateClinicCommand.class))).thenThrow(new ClinicNotFoundException(1L));
        //when + then
        mockMvc.perform(put("/clinics/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.containsString("Couldn't find clinic with id: 1")));
    }
}