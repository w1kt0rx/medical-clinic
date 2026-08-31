package com.w1kt0rx.medicalclinic.controller;

import com.w1kt0rx.medicalclinic.command.CreateVisitCommand;
import com.w1kt0rx.medicalclinic.command.RegisterPatientForVisitCommand;
import com.w1kt0rx.medicalclinic.dto.PageDto;
import com.w1kt0rx.medicalclinic.dto.PageRequestDto;
import com.w1kt0rx.medicalclinic.dto.VisitDto;
import com.w1kt0rx.medicalclinic.service.VisitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/visits")
@RequiredArgsConstructor
@Tag(name = "Visit Management", description = "Endpoints for scheduling, managing, and registering medical visits")
public class VisitController {

    private final VisitService visitService;

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
        log.debug("POST /visits - doctorId={}, start={}, finish={}",
                command.doctorId(), command.startDate(), command.finishDate());        return ResponseEntity.status(HttpStatus.CREATED).body(visitService.create(command));
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
        log.debug("PUT /visits/{}/register - patientId={}", id, command.patientId());
        return ResponseEntity.ok(visitService.registerPatient(id, command));
    }

    @Operation(summary = "Get all visits", description = "Retrieves a list of all registered visits.")
    @ApiResponse(responseCode = "200", description = "Page of visits retrieved successfully")
    @GetMapping
    public ResponseEntity<PageDto<VisitDto>> findAll(@ParameterObject PageRequestDto pageRequestDto) {
        log.debug("GET /visits - page={}, size={}", pageRequestDto.page(), pageRequestDto.size());
        return ResponseEntity.ok(visitService.findAll(pageRequestDto));
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
        log.debug("DELETE /visits/{}", id);
        visitService.delete(id);
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
        log.debug("GET /visits/{}", id);
        return ResponseEntity.ok(visitService.findById(id));
    }

    @Operation(summary = "Get all available (free) visits", description = "Retrieves a list of visits that do not currently have an assigned patient.")
    @ApiResponse(responseCode = "200", description = "Page of free visits retrieved successfully")
    @GetMapping("/free")
    public ResponseEntity<PageDto<VisitDto>> findFreeVisits(@ParameterObject PageRequestDto pageRequestDto) {
        log.debug("GET /visits/free - page={}, size={}", pageRequestDto.page(), pageRequestDto.size());
        return ResponseEntity.ok(visitService.findFreeVisits(pageRequestDto));
    }

    @Operation(summary = "Get visits by patient ID", description = "Retrieves all visits scheduled for a specific patient.")
    @ApiResponse(responseCode = "200", description = "Page of patient visits retrieved successfully")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<PageDto<VisitDto>> findPatientVisits(
            @Parameter(description = "ID of the patient whose visits should be retrieved", example = "10")
            @PathVariable Long patientId, @ParameterObject PageRequestDto pageRequestDto) {
        log.debug("GET /visits/patient/{} - page={}, size={}", patientId, pageRequestDto.page(), pageRequestDto.size());
        return ResponseEntity.ok(visitService.findPatientVisits(patientId, pageRequestDto));
    }
}