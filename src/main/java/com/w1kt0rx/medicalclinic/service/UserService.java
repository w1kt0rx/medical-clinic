package com.w1kt0rx.medicalclinic.service;

import com.w1kt0rx.medicalclinic.command.CreateUserCommand;
import com.w1kt0rx.medicalclinic.command.UpdateUserCommand;
import com.w1kt0rx.medicalclinic.dto.UserDto;
import com.w1kt0rx.medicalclinic.exception.EmailAlreadyInUseException;
import com.w1kt0rx.medicalclinic.exception.UserNotFoundException;
import com.w1kt0rx.medicalclinic.mapper.UserMapper;
import com.w1kt0rx.medicalclinic.model.User;
import com.w1kt0rx.medicalclinic.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Transactional
    public UserDto create(CreateUserCommand command) {
        if (repository.existsByEmail(command.email())) {
            throw new EmailAlreadyInUseException(String.format("Email - %s - jest już w uzyciu", command.email()), HttpStatus.CONFLICT);
        }
        User user = mapper.toEntity(command);
        return mapper.toDto(repository.save(user));
    }

    @Transactional
    public void delete(String email) {
        repository.delete(getUserByEmail(email));
    }

    public Page<UserDto> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toDto);
    }

    public UserDto findByEmail(String email) {
        return mapper.toDto(getUserByEmail(email));
    }

    @Transactional
    public UserDto update(String email, UpdateUserCommand command) {
        User user = getUserByEmail(email);
        return mapper.toDto(repository.save(user.update(command)));
    }

    @Transactional
    public void updatePassword(String email, String password) {
        User user = getUserByEmail(email);
        repository.save(user.updatePassword(password));
    }

    private User getUserByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(null));
    }

}
