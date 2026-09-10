package com.w1kt0rx.medicalclinic.controller;

import com.w1kt0rx.medicalclinic.command.CreateClinicCommand;
import com.w1kt0rx.medicalclinic.command.UpdateClinicCommand;
import com.w1kt0rx.medicalclinic.dto.ClinicDto;
import com.w1kt0rx.medicalclinic.dto.PageDto;
import com.w1kt0rx.medicalclinic.dto.PageRequestDto;
import com.w1kt0rx.medicalclinic.service.ClinicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/clinics")
@RequiredArgsConstructor
@Tag(name = "Clinic Management", description = "Endpoints for managing medical clinics")
public class ClinicController {

    private final ClinicService clinicService;

    @Operation(summary = "Create a new clinic", description = "Registers a new clinic in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Clinic created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data provided")
    })
    @PostMapping
    public ResponseEntity<ClinicDto> create(@RequestBody CreateClinicCommand command) {
        log.debug("POST /clinics - name={}", command.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(clinicService.create(command));
    }

    @Operation(summary = "Delete clinic by ID", description = "Removes a clinic from the system by its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Clinic deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Clinic not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the clinic to be deleted", example = "1")
            @PathVariable Long id) {
        log.debug("DELETE /clinics/{}", id);
        clinicService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all clinics", description = "Retrieves a list of all registered clinics.")
    @ApiResponse(responseCode = "200", description = "Page of clinics retrieved successfully")
    @GetMapping
    public ResponseEntity<PageDto<ClinicDto>> findAll(
            @ParameterObject PageRequestDto pageRequestDto) {
        log.debug("GET /clinics - page={}, size={}", pageRequestDto.page(), pageRequestDto.size());
        return ResponseEntity.ok(clinicService.findAll(pageRequestDto));
    }

    @Operation(summary = "Get clinic by ID", description = "Retrieves details of a specific clinic by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clinic found"),
            @ApiResponse(responseCode = "404", description = "Clinic not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClinicDto> findById(
            @Parameter(description = "ID of the clinic to retrieve", example = "1")
            @PathVariable Long id) {
        log.debug("GET /clinics/{}", id);
        return ResponseEntity.ok(clinicService.findById(id));
    }

    @Operation(summary = "Update an existing clinic", description = "Updates details of a clinic identified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clinic updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data provided"),
            @ApiResponse(responseCode = "404", description = "Clinic not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClinicDto> update(
            @Parameter(description = "ID of the clinic to update", example = "1")
            @PathVariable Long id,
            @RequestBody UpdateClinicCommand command) {
        log.debug("PUT /clinics/{}", id);
        return ResponseEntity.ok(clinicService.update(id, command));
    }
}