package com.w1kt0rx.medicalclinic.service;

import com.w1kt0rx.medicalclinic.command.CreateUserCommand;
import com.w1kt0rx.medicalclinic.command.UpdateUserCommand;
import com.w1kt0rx.medicalclinic.dto.UserDto;
import com.w1kt0rx.medicalclinic.exception.EmailAlreadyInUseException;
import com.w1kt0rx.medicalclinic.exception.UserNotFoundException;
import com.w1kt0rx.medicalclinic.mapper.UserMapper;
import com.w1kt0rx.medicalclinic.model.User;
import com.w1kt0rx.medicalclinic.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    UserService userService;
    UserRepository userRepository;
    UserMapper userMapper;

    @BeforeEach
    void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        userMapper = Mappers.getMapper(UserMapper.class);
        userService = new UserService(userRepository, userMapper);
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
        Assertions.assertAll(() -> Assertions.assertEquals(1L, userDto.id()), () -> Assertions.assertEquals("example@email.com", userDto.email()), () -> Assertions.assertEquals("Jacek", userDto.firstName()), () -> Assertions.assertEquals("Placek", userDto.lastName()), () -> Assertions.assertEquals("123123123", userDto.phoneNumber()), () -> Assertions.assertEquals("example@email.com", userCaptor.getValue().getEmail()), () -> Assertions.assertEquals("Jacek", userCaptor.getValue().getFirstName()), () -> Assertions.assertEquals("Placek", userCaptor.getValue().getLastName()), () -> Assertions.assertEquals("123123123", userCaptor.getValue().getPhoneNumber()));
    }

    @Test
    void create_emailAlreadyInUse_throwsException() {
        //given
        CreateUserCommand command = new CreateUserCommand("example@email.com", "password", "Jacek", "Placek", "123123123");
        when(userRepository.existsByEmail(command.email())).thenReturn(Boolean.TRUE);
        //when + then
        Assertions.assertThrows(EmailAlreadyInUseException.class, () -> userService.create(command));
        Mockito.verify(userRepository, Mockito.never()).save(any());
    }

    @Test
    void findAll_dataCorrect_listOfUsersReturned() {
        //given
        User user1 = new User(1L, "example1@gmail.com", "haslo1", "Jacek", "Placek", "123123123", null, null);
        User user2 = new User(2L, "example2@gmail.com", "haslo2", "Marcin", "Radzki", "321321321", null, null);
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        //when
        List<UserDto> users = userService.findAll();
        //then
        Mockito.verify(userRepository).findAll();
        Assertions.assertAll(() -> Assertions.assertEquals(2, users.size()), () -> Assertions.assertEquals(1L, users.getFirst().id()), () -> Assertions.assertEquals("example1@gmail.com", users.getFirst().email()), () -> Assertions.assertEquals("Jacek", users.getFirst().firstName()), () -> Assertions.assertEquals("Placek", users.getFirst().lastName()), () -> Assertions.assertEquals("123123123", users.getFirst().phoneNumber()), () -> Assertions.assertEquals(2L, users.get(1).id()), () -> Assertions.assertEquals("example2@gmail.com", users.get(1).email()), () -> Assertions.assertEquals("Marcin", users.get(1).firstName()), () -> Assertions.assertEquals("Radzki", users.get(1).lastName()), () -> Assertions.assertEquals("321321321", users.get(1).phoneNumber()));
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
    void findByEmail_userNotExists_throwsException() {
        //given
        when(userRepository.findByEmail("missing@email.com")).thenReturn(Optional.empty());
        //when + then
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.findByEmail("missing@email.com"));
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
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.delete("missing@email.com"));
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
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.update("missing@email.com", command));
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
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.updatePassword("missing@email.com", "newPassword"));
        Mockito.verify(userRepository, Mockito.never()).save(any());
    }
}