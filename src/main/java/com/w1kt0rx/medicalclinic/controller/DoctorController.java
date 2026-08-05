package com.w1kt0rx.medicalclinic.controller;

import com.w1kt0rx.medicalclinic.command.CreateDoctorCommand;
import com.w1kt0rx.medicalclinic.command.UpdateDoctorCommand;
import com.w1kt0rx.medicalclinic.dto.DoctorDto;
import com.w1kt0rx.medicalclinic.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    @PostMapping
    public ResponseEntity<DoctorDto> create(@RequestBody CreateDoctorCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.create(command));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        doctorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<DoctorDto>> findAll() {
        return ResponseEntity.ok(doctorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorDto> update(@PathVariable Long id, @RequestBody UpdateDoctorCommand command) {
        return ResponseEntity.ok(doctorService.update(id, command));
    }

    @PostMapping("/{doctorId}/clinics/{clinicId}")
    public DoctorDto addClinic(
            @PathVariable Long doctorId,
            @PathVariable Long clinicId) {

        return doctorService.addClinic(doctorId, clinicId);
    }

    @DeleteMapping("/{doctorId}/clinics/{clinicId}")
    public DoctorDto removeClinic(
            @PathVariable Long doctorId,
            @PathVariable Long clinicId) {

        return doctorService.removeClinic(doctorId, clinicId);
    }
}
