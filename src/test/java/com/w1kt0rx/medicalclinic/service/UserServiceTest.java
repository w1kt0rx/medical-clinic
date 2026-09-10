package com.w1kt0rx.medicalclinic.service;

import com.w1kt0rx.medicalclinic.command.CreateUserCommand;
import com.w1kt0rx.medicalclinic.command.UpdateUserCommand;
import com.w1kt0rx.medicalclinic.dto.PageDto;
import com.w1kt0rx.medicalclinic.dto.PageRequestDto;
import com.w1kt0rx.medicalclinic.dto.UserDto;
import com.w1kt0rx.medicalclinic.exception.EmailAlreadyInUseException;
import com.w1kt0rx.medicalclinic.exception.UserNotFoundException;
import com.w1kt0rx.medicalclinic.mapper.PageRequestMapper;
import com.w1kt0rx.medicalclinic.mapper.UserMapper;
import com.w1kt0rx.medicalclinic.model.User;
import com.w1kt0rx.medicalclinic.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    UserService userService;
    UserRepository userRepository;
    UserMapper userMapper;
    PageRequestMapper pageRequestMapper;

    @BeforeEach
    void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        userMapper = Mappers.getMapper(UserMapper.class);
        pageRequestMapper = Mappers.getMapper(PageRequestMapper.class);
        userService = new UserService(userRepository, userMapper, pageRequestMapper);
    }

    @Test
    void create_dataCorrect_userReturned() {
        //given
        CreateUserCommand command = new CreateUserCommand("example@email.com", "password", "Jacek", "Placek", "123123123");
        User user = new User(1L, "example@email.com", "password", "Jacek", "Placek", "123123123", null, null);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.existsByEmail(command.email())).thenReturn(Boolean.FALSE);
        when(userRepository.save(any())).thenReturn(user);
        //when
        UserDto userDto = userService.create(command);
        //then
        Mockito.verify(userRepository).save(userCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, userDto.id()),
                () -> Assertions.assertEquals("example@email.com", userDto.email()),
                () -> Assertions.assertEquals("Jacek", userDto.firstName()),
                () -> Assertions.assertEquals("Placek", userDto.lastName()),
                () -> Assertions.assertEquals("123123123", userDto.phoneNumber()),
                () -> Assertions.assertEquals("example@email.com", userCaptor.getValue().getEmail()),
                () -> Assertions.assertEquals("Jacek", userCaptor.getValue().getFirstName()),
                () -> Assertions.assertEquals("Placek", userCaptor.getValue().getLastName()),
                () -> Assertions.assertEquals("123123123", userCaptor.getValue().getPhoneNumber()));
    }

    @Test
    void create_emailAlreadyInUse_throwsException() {
        //given
        CreateUserCommand command = new CreateUserCommand("example@email.com", "password", "Jacek", "Placek", "123123123");
        when(userRepository.existsByEmail(command.email())).thenReturn(Boolean.TRUE);
        //when + then
        EmailAlreadyInUseException ex = Assertions.assertThrows(EmailAlreadyInUseException.class, () -> userService.create(command));
        Assertions.assertAll(
                () -> Assertions.assertEquals("Email - example@email.com - is already used", ex.getMessage()),
                () -> Assertions.assertEquals(org.springframework.http.HttpStatus.CONFLICT, ex.getHttpStatus())
        );
        Mockito.verify(userRepository, Mockito.never()).save(any());
    }

    @Test
    void findAll_dataCorrect_pageOfUsersReturned() {
        //given
        User user1 = new User(1L, "example1@gmail.com", "haslo1", "Jacek", "Placek", "123123123", null, null);
        User user2 = new User(2L, "example2@gmail.com", "haslo2", "Marcin", "Radzki", "321321321", null, null);
        PageRequestDto pageRequestDto = new PageRequestDto(0, 20, "id", "asc");
        Pageable expectedPageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "id"));
        Page<User> userPage = new PageImpl<>(List.of(user1, user2), expectedPageable, 2);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);
        //when
        PageDto<UserDto> result = userService.findAll(pageRequestDto);
        //then
        Mockito.verify(userRepository).findAll(pageableCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, result.content().size()),
                () -> Assertions.assertEquals(1L, result.content().getFirst().id()),
                () -> Assertions.assertEquals("example1@gmail.com", result.content().getFirst().email()),
                () -> Assertions.assertEquals(2L, result.content().get(1).id()),
                () -> Assertions.assertEquals(2, result.totalElements()),
                () -> Assertions.assertEquals(0, pageableCaptor.getValue().getPageNumber()),
                () -> Assertions.assertEquals(20, pageableCaptor.getValue().getPageSize())
        );
    }

    @Test
    void findByEmail_dataCorrect_userReturned() {
        //given
        User user = new User(1L, "Example1@gmail.com", "password", "Mateusz", "Markowski", "123123123", null, null);
        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        //when
        UserDto userReturned = userService.findByEmail(user.getEmail());
        //then
        Mockito.verify(userRepository).findByEmail(emailCaptor.capture());
        Assertions.assertAll(() -> Assertions.assertEquals(1L, userReturned.id()), () -> Assertions.assertEquals("Example1@gmail.com", userReturned.email()), () -> Assertions.assertEquals("Mateusz", userReturned.firstName()), () -> Assertions.assertEquals("Markowski", userReturned.lastName()), () -> Assertions.assertEquals("123123123", userReturned.phoneNumber()), () -> Assertions.assertEquals("Example1@gmail.com", emailCaptor.getValue()));
    }

    @Test
    void findByEmail_userNotExists_throwsExceptionWithNullIdInMessage() {
        //given
        when(userRepository.findByEmail("missing@email.com")).thenReturn(Optional.empty());
        //when + then
        UserNotFoundException ex = Assertions.assertThrows(UserNotFoundException.class, () -> userService.findByEmail("missing@email.com"));
        Assertions.assertEquals("Couldn't find user with id: null", ex.getMessage());
    }

    @Test
    void delete_userExists_userDeleted() {
        //given
        User user = new User(1L, "example@email.com", "password", "Jacek", "Placek", "123123123", null, null);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        //when
        userService.delete(user.getEmail());
        //then
        Mockito.verify(userRepository).delete(userCaptor.capture());
        Assertions.assertEquals(user, userCaptor.getValue());
    }

    @Test
    void delete_userNotExists_throwsException() {
        //given
        when(userRepository.findByEmail("missing@email.com")).thenReturn(Optional.empty());
        //when + then
        UserNotFoundException ex = Assertions.assertThrows(UserNotFoundException.class, () -> userService.delete("missing@email.com"));
        Assertions.assertEquals("Couldn't find user with id: null", ex.getMessage());
        Mockito.verify(userRepository, Mockito.never()).delete(any());
    }

    @Test
    void update_userExists_updatedUserReturned() {
        //given
        User user = new User(1L, "example@email.com", "password", "Jacek", "Placek", "123123123", null, null);
        UpdateUserCommand command = new UpdateUserCommand("Marcin", "newPassword", "Nowak", "999999999");
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        //when
        UserDto userDto = userService.update(user.getEmail(), command);
        //then
        Mockito.verify(userRepository).save(userCaptor.capture());
        Assertions.assertAll(() -> Assertions.assertEquals(1L, userDto.id()), () -> Assertions.assertEquals("example@email.com", userDto.email()), () -> Assertions.assertEquals("Marcin", userDto.firstName()), () -> Assertions.assertEquals("Nowak", userDto.lastName()), () -> Assertions.assertEquals("999999999", userDto.phoneNumber()), () -> Assertions.assertEquals("Marcin", userCaptor.getValue().getFirstName()), () -> Assertions.assertEquals("Nowak", userCaptor.getValue().getLastName()), () -> Assertions.assertEquals("999999999", userCaptor.getValue().getPhoneNumber()));
    }

    @Test
    void update_userNotExists_throwsException() {
        //given
        UpdateUserCommand command = new UpdateUserCommand("Marcin", "newPassword", "Nowak", "999999999");
        when(userRepository.findByEmail("missing@email.com")).thenReturn(Optional.empty());
        //when + then
        UserNotFoundException ex = Assertions.assertThrows(UserNotFoundException.class, () -> userService.update("missing@email.com", command));
        Assertions.assertEquals("Couldn't find user with id: null", ex.getMessage());
        Mockito.verify(userRepository, Mockito.never()).save(any());
    }

    @Test
    void updatePassword_userExists_passwordUpdated() {
        //given
        User user = new User(1L, "example@email.com", "password", "Jacek", "Placek", "123123123", null, null);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        //when
        userService.updatePassword(user.getEmail(), "newPassword");
        //then
        Mockito.verify(userRepository).save(userCaptor.capture());
        Assertions.assertEquals("newPassword", userCaptor.getValue().getPassword());
    }

    @Test
    void updatePassword_userNotExists_throwsException() {
        //given
        when(userRepository.findByEmail("missing@email.com")).thenReturn(Optional.empty());
        //when + then
        UserNotFoundException ex = Assertions.assertThrows(UserNotFoundException.class, () -> userService.updatePassword("missing@email.com", "newPassword"));
        Assertions.assertEquals("Couldn't find user with id: null", ex.getMessage());
        Mockito.verify(userRepository, Mockito.never()).save(any());
    }
}