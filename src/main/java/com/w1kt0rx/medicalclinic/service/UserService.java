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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final PageRequestMapper pageRequestMapper;

    @Transactional
    public UserDto create(CreateUserCommand command) {
        log.debug("Creating user, email={}", command.email());
        if (userRepository.existsByEmail(command.email())) {
            log.warn("Cannot create user - email already in use: {}", command.email());
            throw new EmailAlreadyInUseException(String.format("Email - %s - is already used", command.email()), HttpStatus.CONFLICT);
        }
        User user = mapper.toEntity(command);
        User saved = userRepository.save(user);
        log.info("User created, id={}, email={}", saved.getId(), saved.getEmail());
        return mapper.toDto(saved);
    }

    @Transactional
    public void delete(String email) {
        log.debug("Deleting user, email={}", email);
        User user = getUserByEmail(email);
        userRepository.delete(user);
        log.info("User deleted, id={}, email={}", user.getId(), email);
    }

    public PageDto<UserDto> findAll(PageRequestDto pageRequestDto) {
        Pageable pageable = pageRequestMapper.toPageable(pageRequestDto);
        log.debug("Fetching users page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return PageDto.from(userRepository.findAll(pageable).map(mapper::toDto));
    }

    public UserDto findByEmail(String email) {
        log.debug("Fetching user by email={}", email);
        User user = getUserByEmail(email);
        return mapper.toDto(user);
    }

    @Transactional
    public UserDto update(String email, UpdateUserCommand command) {
        log.debug("Updating user, email={}", email);
        User user = getUserByEmail(email);
        user.update(command);
        User saved = userRepository.save(user);
        log.info("User updated, id={}, email={}", saved.getId(), email);
        return mapper.toDto(saved);
    }

    @Transactional
    public void updatePassword(String email, String newPassword) {
        log.debug("Updating password for email={}", email);
        User user = getUserByEmail(email);
        user.setPassword(newPassword);
        userRepository.save(user);
        log.info("Password updated for userId={}", user.getId());
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found, email={}", email);
                    return new UserNotFoundException(null);
                });
    }
}
