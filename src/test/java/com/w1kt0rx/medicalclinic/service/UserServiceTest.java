package com.w1kt0rx.medicalclinic.service;

import com.w1kt0rx.medicalclinic.command.CreateUserCommand;
import com.w1kt0rx.medicalclinic.dto.UserDto;
import com.w1kt0rx.medicalclinic.mapper.UserMapper;
import com.w1kt0rx.medicalclinic.model.User;
import com.w1kt0rx.medicalclinic.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;

import java.util.List;

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
    void create_dataCorrect_mappedUserReturned() {
        //given
        CreateUserCommand command = new CreateUserCommand("example@email.com", "password", "Jacek", "Placek", "123123123");
        User user = new User(1L, "example@email.com", "password", "Jacek", "Placek", "123123123", null, null);
        when(userRepository.existsByEmail(command.email())).thenReturn(Boolean.FALSE);
        when(userRepository.save(any())).thenReturn(user);
        //when
        UserDto userDto = userService.create(command);
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, userDto.id()),
                () -> Assertions.assertEquals("example@email.com", userDto.email()),
                () -> Assertions.assertEquals("Jacek", userDto.firstName()),
                () -> Assertions.assertEquals("Placek", userDto.lastName()),
                () -> Assertions.assertEquals("123123123", userDto.phoneNumber())
        );
    }

    @Test
    void findAll_dataCorrect_listOfMappedUsers() {
        //given
        User user1 = new User(1L, "example1@gmail.com", "haslo1", "Jacek", "Placek", "123123123", null, null);
        User user2 = new User(2L, "example2@gmail.com", "haslo2", "Marcin", "Radzki", "321321321", null, null);
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        //when

        //then
    }
}
