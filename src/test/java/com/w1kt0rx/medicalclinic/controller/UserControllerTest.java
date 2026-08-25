package com.w1kt0rx.medicalclinic.controller;

import com.w1kt0rx.medicalclinic.command.CreateUserCommand;
import com.w1kt0rx.medicalclinic.command.UpdatePasswordCommand;
import com.w1kt0rx.medicalclinic.command.UpdateUserCommand;
import com.w1kt0rx.medicalclinic.dto.UserDto;
import com.w1kt0rx.medicalclinic.exception.EmailAlreadyInUseException;
import com.w1kt0rx.medicalclinic.exception.UserNotFoundException;
import com.w1kt0rx.medicalclinic.service.UserService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    UserService userService;

    @Test
    void create_dataCorrect_returnsCreatedUser() throws Exception {
        //given
        CreateUserCommand command = new CreateUserCommand("jacek@gmail.com", "password!", "Jacek", "Placek", "123123123");
        UserDto userDto = new UserDto(1L, "jacek@gmail.com", "Jacek", "Placek", "123123123");
        ArgumentCaptor<CreateUserCommand> commandCaptor = ArgumentCaptor.forClass(CreateUserCommand.class);
        when(userService.create(any(CreateUserCommand.class))).thenReturn(userDto);
        //when + then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("jacek@gmail.com"));
        Mockito.verify(userService).create(commandCaptor.capture());
        Assertions.assertEquals("jacek@gmail.com", commandCaptor.getValue().email());
    }

    @Test
    void create_emailAlreadyInUse_returnsConflictWithMessage() throws Exception {
        //given
        CreateUserCommand command = new CreateUserCommand("jacek@gmail.com", "password!", "Jacek", "Placek", "123123123");
        when(userService.create(any(CreateUserCommand.class))).thenThrow(
                new EmailAlreadyInUseException("Email - jacek@gmail.com - jest już w uzyciu", HttpStatus.CONFLICT));
        //when + then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", Matchers.containsString("jest już w uzyciu")));
    }

    @Test
    void delete_userExists_returnsNoContent() throws Exception {
        //given
        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        //when + then
        mockMvc.perform(delete("/users/{email}", "jacek@gmail.com"))
                .andExpect(status().isNoContent());
        Mockito.verify(userService).delete(emailCaptor.capture());
        Assertions.assertEquals("jacek@gmail.com", emailCaptor.getValue());
    }

    @Test
    void delete_userNotExists_returnsNotFoundWithMessage() throws Exception {
        //given
        Mockito.doThrow(new UserNotFoundException(null)).when(userService).delete("missing@gmail.com");
        //when + then
        mockMvc.perform(delete("/users/{email}", "missing@gmail.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.containsString("Couldn't find user with id: null")));
    }

    @Test
    void findAll_dataCorrect_returnsPageOfUsers() throws Exception {
        //given
        UserDto user1 = new UserDto(1L, "jacek@gmail.com", "Jacek", "Placek", "123123123");
        UserDto user2 = new UserDto(2L, "anna@gmail.com", "Anna", "Nowak", "321321321");
        Pageable pageable = PageRequest.of(0, 20, Sort.by("id"));
        Page<UserDto> page = new PageImpl<>(List.of(user1, user2), pageable, 2);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(userService.findAll(any(Pageable.class))).thenReturn(page);
        //when + then
        mockMvc.perform(get("/users")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "id,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].email").value("jacek@gmail.com"))
                .andExpect(jsonPath("$.content[1].email").value("anna@gmail.com"))
                .andExpect(jsonPath("$.page.totalElements").value(2));
        Mockito.verify(userService).findAll(pageableCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals(0, pageableCaptor.getValue().getPageNumber()),
                () -> Assertions.assertEquals(20, pageableCaptor.getValue().getPageSize())
        );
    }

    @Test
    void findByEmail_userExists_returnsUser() throws Exception {
        //given
        UserDto userDto = new UserDto(1L, "jacek@gmail.com", "Jacek", "Placek", "123123123");
        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        when(userService.findByEmail("jacek@gmail.com")).thenReturn(userDto);
        //when + then
        mockMvc.perform(get("/users/{email}", "jacek@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("jacek@gmail.com"));
        Mockito.verify(userService).findByEmail(emailCaptor.capture());
        Assertions.assertEquals("jacek@gmail.com", emailCaptor.getValue());
    }

    @Test
    void findByEmail_userNotExists_returnsNotFoundWithMessage() throws Exception {
        //given
        when(userService.findByEmail("missing@gmail.com")).thenThrow(new UserNotFoundException(null));
        //when + then
        mockMvc.perform(get("/users/{email}", "missing@gmail.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.containsString("Couldn't find user with id: null")));
    }

    @Test
    void update_dataCorrect_returnsUpdatedUser() throws Exception {
        //given
        UpdateUserCommand command = new UpdateUserCommand("Jan", "newPassword", "Placek", "987654321");
        UserDto userDto = new UserDto(1L, "jacek@gmail.com", "Jan", "Placek", "987654321");
        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UpdateUserCommand> commandCaptor = ArgumentCaptor.forClass(UpdateUserCommand.class);
        when(userService.update(anyString(), any(UpdateUserCommand.class))).thenReturn(userDto);
        //when + then
        mockMvc.perform(put("/users/{email}", "jacek@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jan"));
        Mockito.verify(userService).update(emailCaptor.capture(), commandCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals("jacek@gmail.com", emailCaptor.getValue()),
                () -> Assertions.assertEquals("Jan", commandCaptor.getValue().firstName())
        );
    }

    @Test
    void update_userNotExists_returnsNotFound() throws Exception {
        //given
        UpdateUserCommand command = new UpdateUserCommand("Jan", "newPassword", "Placek", "987654321");
        when(userService.update(anyString(), any(UpdateUserCommand.class))).thenThrow(new UserNotFoundException(null));
        //when + then
        mockMvc.perform(put("/users/{email}", "missing@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePassword_dataCorrect_returnsNoContent() throws Exception {
        //given
        UpdatePasswordCommand command = new UpdatePasswordCommand("NewPassword123!");
        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
        //when + then
        mockMvc.perform(patch("/users/{email}/password", "jacek@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNoContent());
        Mockito.verify(userService).updatePassword(emailCaptor.capture(), passwordCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals("jacek@gmail.com", emailCaptor.getValue()),
                () -> Assertions.assertEquals("NewPassword123!", passwordCaptor.getValue())
        );
    }

    @Test
    void updatePassword_userNotExists_returnsNotFound() throws Exception {
        //given
        UpdatePasswordCommand command = new UpdatePasswordCommand("NewPassword123!");
        Mockito.doThrow(new UserNotFoundException(null)).when(userService).updatePassword(anyString(), anyString());
        //when + then
        mockMvc.perform(patch("/users/{email}/password", "missing@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound());
    }
}