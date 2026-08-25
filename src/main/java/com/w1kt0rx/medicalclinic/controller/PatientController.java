package com.w1kt0rx.medicalclinic.controller;

import com.w1kt0rx.medicalclinic.command.CreatePatientCommand;
import com.w1kt0rx.medicalclinic.command.UpdatePatientCommand;
import com.w1kt0rx.medicalclinic.dto.PatientDto;
import com.w1kt0rx.medicalclinic.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
@RequestMapping("/patients")
@RequiredArgsConstructor
@Tag(name = "Patient Management", description = "Endpoints for managing patients")
public class PatientController {

    private final PatientService patientService;

    @Operation(summary = "Create a new patient", description = "Registers a new patient in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PatientDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data provided",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Email is already in use",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<PatientDto> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Patient registration data",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreatePatientCommand.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "idCardNo": "123456",
                                      "birthday": "2000-01-01"
                                    }
                                    """)))
            @RequestBody CreatePatientCommand command) {

        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.create(command));
    }

    @Operation(summary = "Delete patient by ID", description = "Removes a patient from the system by their unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Patient deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Patient not found",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the patient to be deleted", example = "1")
            @PathVariable Long id) {
        patientService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all patients", description = "Retrieves a list of all registered patients.")
    @ApiResponse(responseCode = "200", description = "Page of patients retrieved successfully")
    @GetMapping
    public ResponseEntity<Page<PatientDto>> findAll(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(patientService.findAll(pageable));
    }

    @Operation(summary = "Get patient by ID", description = "Retrieves details of a specific patient by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PatientDto.class))),
            @ApiResponse(responseCode = "404", description = "Patient not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<PatientDto> findById(
            @Parameter(description = "ID of the patient to retrieve", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(patientService.findById(id));
    }

    @Operation(summary = "Update patient data", description = "Updates details of an existing patient identified by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PatientDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data provided",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Patient not found",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<PatientDto> update(
            @Parameter(description = "ID of the patient to update", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated patient details",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdatePatientCommand.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "idCardNo": "42424241414",
                                      "birthday": "1995-01-01"
                                    }
                                    """)))
            @RequestBody UpdatePatientCommand command) {
        return ResponseEntity.ok(patientService.update(id, command));
    }
}