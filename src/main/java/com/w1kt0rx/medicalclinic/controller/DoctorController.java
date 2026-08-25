package com.w1kt0rx.medicalclinic.controller;

import com.w1kt0rx.medicalclinic.command.CreateDoctorCommand;
import com.w1kt0rx.medicalclinic.command.UpdateDoctorCommand;
import com.w1kt0rx.medicalclinic.dto.DoctorDto;
import com.w1kt0rx.medicalclinic.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctor Management", description = "Endpoints for managing doctors and their clinic assignments")
public class DoctorController {

    private final DoctorService doctorService;

    @Operation(summary = "Create a new doctor", description = "Registers a new doctor in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Doctor created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data provided")
    })
    @PostMapping
    public ResponseEntity<DoctorDto> create(@RequestBody CreateDoctorCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.create(command));
    }

    @Operation(summary = "Delete doctor by ID", description = "Removes a doctor from the system by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Doctor deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the doctor to be deleted", example = "1")
            @PathVariable Long id) {
        doctorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all doctors", description = "Retrieves a list of all registered doctors.")
    @ApiResponse(responseCode = "200", description = "Page of doctors retrieved successfully")
    @GetMapping
    public ResponseEntity<Page<DoctorDto>> findAll(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(doctorService.findAll(pageable));
    }

    @Operation(summary = "Get doctor by ID", description = "Retrieves details of a specific doctor by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor found"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DoctorDto> findById(
            @Parameter(description = "ID of the doctor to retrieve", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(doctorService.findById(id));
    }

    @Operation(summary = "Update an existing doctor", description = "Updates details of a doctor identified by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data provided"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DoctorDto> update(
            @Parameter(description = "ID of the doctor to update", example = "1")
            @PathVariable Long id,
            @RequestBody UpdateDoctorCommand command) {
        return ResponseEntity.ok(doctorService.update(id, command));
    }

    @Operation(summary = "Assign doctor to clinic", description = "Establishes an association between a doctor and a clinic.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clinic assigned successfully"),
            @ApiResponse(responseCode = "404", description = "Doctor or Clinic not found")
    })
    @PostMapping("/{doctorId}/clinics/{clinicId}")
    public ResponseEntity<DoctorDto> addClinic(
            @Parameter(description = "ID of the doctor", example = "1")
            @PathVariable Long doctorId,
            @Parameter(description = "ID of the clinic to assign", example = "2")
            @PathVariable Long clinicId) {

        return ResponseEntity.ok(doctorService.addClinic(doctorId, clinicId));
    }

    @Operation(summary = "Remove doctor from clinic", description = "Removes the association between a doctor and a clinic.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clinic removed successfully"),
            @ApiResponse(responseCode = "404", description = "Doctor or Clinic assignment not found")
    })
    @DeleteMapping("/{doctorId}/clinics/{clinicId}")
    public ResponseEntity<DoctorDto> removeClinic(
            @Parameter(description = "ID of the doctor", example = "1")
            @PathVariable Long doctorId,
            @Parameter(description = "ID of the clinic to remove", example = "2")
            @PathVariable Long clinicId) {

        return ResponseEntity.ok(doctorService.removeClinic(doctorId, clinicId));
    }
}