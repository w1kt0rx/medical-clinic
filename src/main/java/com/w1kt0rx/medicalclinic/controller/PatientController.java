package com.w1kt0rx.medicalclinic.controller;

import com.w1kt0rx.medicalclinic.command.CreatePatientCommand;
import com.w1kt0rx.medicalclinic.command.UpdatePasswordCommand;
import com.w1kt0rx.medicalclinic.command.UpdatePatientCommand;
import com.w1kt0rx.medicalclinic.dto.PatientDto;
import com.w1kt0rx.medicalclinic.service.PatientService;
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
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @Operation(summary = "Stworz nowego pacjenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Udalo sie utworzyc pacjenta",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PatientDto.class))),
            @ApiResponse(responseCode = "400", description = "Zle dane requesta",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Email jest juz w uzyciu",
                    content = @Content)
    })
    @Tag(name = "Stworz nowego pacjenta")
    @PostMapping
    public ResponseEntity<PatientDto> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dane pacjenta",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreatePatientCommand.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "email": "Jacek@gmail.com",
                                      "password": "password!",
                                      "idCardNo": "123456",
                                      "firstName": "Jacek",
                                      "lastName": "Palcek",
                                      "phoneNumber": "123456789",
                                      "birthday": "2000-01-01"
                                    }
                                    """)))
            @RequestBody CreatePatientCommand command) {

        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.create(command));
    }

    @Operation(summary = "Usun pacjenta poprzez email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pacjent usunięty"),
            @ApiResponse(responseCode = "404", description = "Pacjent nie znaleziony",
                    content = @Content)
    })
    @Tag(name = "Usun pacjenta")
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> delete(@PathVariable String email) {
        patientService.delete(email);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Zwroc wszystkich pacjentow")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista pacjentow",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PatientDto.class)))
    })
    @Tag(name = "Otrzymaj wszystkich pacjentow")
    @GetMapping
    public ResponseEntity<List<PatientDto>> findAll() {
        return ResponseEntity.ok(patientService.findAll());
    }

    @Operation(summary = "Zwroc pacjenta poprzez email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pacjent znaleziony",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PatientDto.class))),
            @ApiResponse(responseCode = "404", description = "Pacjent nie znaleziony",
                    content = @Content)
    })
    @Tag(name = "Otrzymaj konkretnego pacjenta")
    @GetMapping("/{email}")
    public ResponseEntity<PatientDto> findByEmail(@PathVariable String email) {
        return ResponseEntity.ok(patientService.findByEmail(email));
    }

    @Operation(summary = "Zaaktualizowanie danych pacjenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pacjent zaaktualizowany",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PatientDto.class))),
            @ApiResponse(responseCode = "400", description = "Niepoprawne żądanie",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Pacjent nie znaleziony",
                    content = @Content)
    })
    @Tag(name = "Zaaktualizuj pacjenta")
    @PutMapping("/{email}")
    public ResponseEntity<PatientDto> update(
            @PathVariable String email,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Zaaktualizuj dane pacjenta",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UpdatePatientCommand.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "firstName": "Jan",
                                      "lastName": "Placek",
                                      "phoneNumber": "987654321",
                                      "birthday": "1995-01-01"
                                    }
                                    """)))
            @RequestBody UpdatePatientCommand command) {

        return ResponseEntity.ok(patientService.update(email, command));
    }

    @Operation(summary = "Zaaktualizuj haslo pacjenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Haslo zaaktualizowane"),
            @ApiResponse(responseCode = "404", description = "Pacjent nie znaleziony",
                    content = @Content)
    })
    @Tag(name = "Zaaktualizuj haslo pacjenta")
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

        patientService.updatePassword(email, command.password());
        return ResponseEntity.noContent().build();
    }
}