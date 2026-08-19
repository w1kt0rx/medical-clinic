package com.w1kt0rx.medicalclinic.controller;

import com.w1kt0rx.medicalclinic.dto.PatientDto;
import com.w1kt0rx.medicalclinic.dto.UserDto;
import com.w1kt0rx.medicalclinic.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class PatientControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    PatientService service;

    @Test
    void findById_patientExists_returnsPatient() throws Exception {
        //given
        LocalDate birthday = LocalDate.of(2004, 10, 22);
        UserDto userDto = new UserDto(1L, "example@gmail.com", "Jacek", "Placek", "123123123");
        PatientDto patientDto = new PatientDto(1L, "123321", birthday, userDto);
        when(service.findById(patientDto.id())).thenReturn(patientDto);
        //when then
        mockMvc.perform(get("/patients/{id}", patientDto.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.idCardNo").value("123321"))
                .andExpect(jsonPath("$.birthday").value("2004-10-22"))
                .andExpect(jsonPath("$.user.id").value(1L))
                .andExpect(jsonPath("$.user.email").value("example@gmail.com"))
                .andExpect(jsonPath("$.user.firstName").value("Jacek"))
                .andExpect(jsonPath("$.user.lastName").value("Placek"))
                .andExpect(jsonPath("$.user.phoneNumber").value("123123123"));
    }
}
