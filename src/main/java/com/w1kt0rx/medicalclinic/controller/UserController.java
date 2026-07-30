package com.w1kt0rx.medicalclinic.controller;

import com.w1kt0rx.medicalclinic.command.CreateUserCommand;
import com.w1kt0rx.medicalclinic.command.UpdatePasswordCommand;
import com.w1kt0rx.medicalclinic.command.UpdateUserCommand;
import com.w1kt0rx.medicalclinic.dto.UserDto;
import com.w1kt0rx.medicalclinic.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Stworz nowego uzytkownika")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Udalo sie utworzyc uzytkownika",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Zle dane requesta",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Email jest juz w uzyciu",
                    content = @Content)
    })
    @Tag(name = "Stworz nowego uzytkownika")
    @PostMapping
    public ResponseEntity<UserDto> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dane uzytkownika",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateUserCommand.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "email": "Jacek@gmail.com",
                                      "password": "password!",
                                      "firstName": "Jacek",
                                      "lastName": "Palcek",
                                      "phoneNumber": "123456789",
                                    }
                                    """)))
            @RequestBody CreateUserCommand command) {

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(command));
    }

    @Operation(summary = "Usun uzytkownika poprzez email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Uzytkownik usunięty"),
            @ApiResponse(responseCode = "404", description = "Uzytkownik nie znaleziony",
                    content = @Content)
    })
    @Tag(name = "Usun uzytkownika")
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> delete(@PathVariable String email) {
        userService.delete(email);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Zwroc wszystkich uzytkownikow")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista uzytkownikow",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class)))
    })
    @Tag(name = "Otrzymaj wszystkich uzytkownikow")
    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @Operation(summary = "Zwroc uzytkownika poprzez email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Uzytkownik znaleziony",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "Uzytkownik nie znaleziony",
                    content = @Content)
    })
    @Tag(name = "Otrzymaj konkretnego uzytkownika")
    @GetMapping("/{email}")
    public ResponseEntity<UserDto> findByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.findByEmail(email));
    }

    @Operation(summary = "Zaaktualizowanie danych uzytkownika")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Uzytkownik zaaktualizowany",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Niepoprawne żądanie",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Uzytkownik nie znaleziony",
                    content = @Content)
    })
    @Tag(name = "Zaaktualizuj uzytkownika")
    @PutMapping("/{email}")
    public ResponseEntity<UserDto> update(
            @PathVariable String email,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Zaaktualizuj dane uzytkownika",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UpdateUserCommand.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "firstName": "Jan",
                                      "lastName": "Placek",
                                      "phoneNumber": "987654321",
                                    }
                                    """)))
            @RequestBody UpdateUserCommand command) {

        return ResponseEntity.ok(userService.update(email, command));
    }

    @Operation(summary = "Zaaktualizuj haslo uzytkownika")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Haslo zaaktualizowane"),
            @ApiResponse(responseCode = "404", description = "Uzytkownik nie znaleziony",
                    content = @Content)
    })
    @Tag(name = "Zaaktualizuj haslo uzytkownika")
    @PatchMapping("/{email}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable String email,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nowe haslo",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UpdatePasswordCommand.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "password": "NoweHaslo123"
                                    }
                                    """)))
            @RequestBody UpdatePasswordCommand command) {

        userService.updatePassword(email, command.password());
        return ResponseEntity.noContent().build();
    }
}
