package com.w1kt0rx.medicalclinic.controller;

import com.w1kt0rx.medicalclinic.command.CreateVisitCommand;
import com.w1kt0rx.medicalclinic.command.RegisterPatientForVisitCommand;
import com.w1kt0rx.medicalclinic.dto.VisitDto;
import com.w1kt0rx.medicalclinic.service.VisitService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/visits")
@RequiredArgsConstructor
@Tag(name = "Visit Management", description = "Endpoints for scheduling, managing, and registering medical visits")
public class VisitController {

    private final VisitService service;

    @Operation(summary = "Create a new visit", description = "Schedules a new doctor's availability slot/visit in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Visit created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VisitDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid visit scheduling data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Doctor or clinic not found",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<VisitDto> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Visit creation payload with doctor, clinic, start, and end time",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateVisitCommand.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "doctorId": 1,
                                      "clinicId": 1,
                                      "start": "2026-09-01T09:00:00",
                                      "end": "2026-09-01T09:30:00"
                                    }
                                    """)))
            @RequestBody CreateVisitCommand command) {

        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(command));
    }

    @Operation(summary = "Register patient for a visit", description = "Assigns a patient to a specific available visit slot.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient registered for the visit successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VisitDto.class))),
            @ApiResponse(responseCode = "400", description = "Visit is already taken or invalid patient data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Visit or patient not found",
                    content = @Content)
    })
    @PutMapping("/{id}/register")
    public ResponseEntity<VisitDto> registerPatient(
            @Parameter(description = "ID of the visit to register for", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Patient registration payload containing patient ID",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegisterPatientForVisitCommand.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "patientId": 10
                                    }
                                    """)))
            @RequestBody RegisterPatientForVisitCommand command) {

        return ResponseEntity.ok(service.registerPatient(id, command));
    }

    @Operation(summary = "Get all visits", description = "Retrieves a complete list of all visits in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of visits retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = VisitDto.class))))
    })
    @GetMapping
    public ResponseEntity<List<VisitDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Delete visit by ID", description = "Cancels/removes a visit slot from the system by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Visit deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Visit not found",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the visit to delete", example = "1")
            @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get visit by ID", description = "Retrieves details of a specific visit by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Visit found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VisitDto.class))),
            @ApiResponse(responseCode = "404", description = "Visit not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<VisitDto> findById(
            @Parameter(description = "ID of the visit to retrieve", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Get all available (free) visits", description = "Retrieves a list of visits that do not currently have an assigned patient.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of free visits retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = VisitDto.class))))
    })
    @GetMapping("/free")
    public ResponseEntity<List<VisitDto>> findFreeVisits() {
        return ResponseEntity.ok(service.findFreeVisits());
    }

    @Operation(summary = "Get visits by patient ID", description = "Retrieves all visits scheduled for a specific patient.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of patient visits retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = VisitDto.class)))),
            @ApiResponse(responseCode = "404", description = "Patient not found",
                    content = @Content)
    })
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<VisitDto>> findPatientVisits(
            @Parameter(description = "ID of the patient whose visits should be retrieved", example = "10")
            @PathVariable Long patientId) {
        return ResponseEntity.ok(service.findPatientVisits(patientId));
    }
}