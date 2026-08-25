package com.w1kt0rx.medicalclinic.controller;

import com.w1kt0rx.medicalclinic.command.CreateUserCommand;
import com.w1kt0rx.medicalclinic.command.UpdatePasswordCommand;
import com.w1kt0rx.medicalclinic.command.UpdateUserCommand;
import com.w1kt0rx.medicalclinic.dto.UserDto;
import com.w1kt0rx.medicalclinic.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for managing system users and authentication details")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Create a new user", description = "Registers a new user in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data provided",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Email is already in use",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<UserDto> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User creation data",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateUserCommand.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "email": "jacek@gmail.com",
                                      "password": "password!",
                                      "firstName": "Jacek",
                                      "lastName": "Palcek",
                                      "phoneNumber": "123456789"
                                    }
                                    """)))
            @RequestBody CreateUserCommand command) {

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(command));
    }

    @Operation(summary = "Delete user by email", description = "Removes a user from the system by their email address.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)
    })
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Email address of the user to delete", example = "jacek@gmail.com")
            @PathVariable String email) {
        userService.delete(email);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all users", description = "Retrieves a list of all registered users.")
    @ApiResponse(responseCode = "200", description = "Page of users retrieved successfully")
    @GetMapping
    public ResponseEntity<Page<UserDto>> findAll(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(userService.findAll(pageable));
    }

    @Operation(summary = "Get user by email", description = "Retrieves details of a specific user by their email address.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)
    })
    @GetMapping("/{email}")
    public ResponseEntity<UserDto> findByEmail(
            @Parameter(description = "Email address of the user to retrieve", example = "jacek@gmail.com")
            @PathVariable String email) {
        return ResponseEntity.ok(userService.findByEmail(email));
    }

    @Operation(summary = "Update user details", description = "Updates profile details for an existing user identified by email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data provided",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)
    })
    @PutMapping("/{email}")
    public ResponseEntity<UserDto> update(
            @Parameter(description = "Email address of the user to update", example = "jacek@gmail.com")
            @PathVariable String email,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated user profile details",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateUserCommand.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "firstName": "Jan",
                                      "lastName": "Placek",
                                      "phoneNumber": "987654321"
                                    }
                                    """)))
            @RequestBody UpdateUserCommand command) {

        return ResponseEntity.ok(userService.update(email, command));
    }

    @Operation(summary = "Update user password", description = "Updates password for a user identified by email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Password updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid password format provided",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)
    })
    @PatchMapping("/{email}/password")
    public ResponseEntity<Void> updatePassword(
            @Parameter(description = "Email address of the user whose password will be updated", example = "jacek@gmail.com")
            @PathVariable String email,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New password payload",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdatePasswordCommand.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "password": "NewPassword123!"
                                    }
                                    """)))
            @RequestBody UpdatePasswordCommand command) {

        userService.updatePassword(email, command.password());
        return ResponseEntity.noContent().build();
    }
}