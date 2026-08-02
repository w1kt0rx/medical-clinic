package com.w1kt0rx.medicalclinic.controller;

import com.w1kt0rx.medicalclinic.command.CreateClinicCommand;
import com.w1kt0rx.medicalclinic.command.UpdateClinicCommand;
import com.w1kt0rx.medicalclinic.dto.ClinicDto;
import com.w1kt0rx.medicalclinic.service.ClinicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clinics")
@RequiredArgsConstructor
public class ClinicController {

    private final ClinicService clinicService;

    @PostMapping
    public ResponseEntity<ClinicDto> create(@RequestBody CreateClinicCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clinicService.create(command));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clinicService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ClinicDto>> findAll() {
        return ResponseEntity.ok(clinicService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClinicDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(clinicService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClinicDto> update(@PathVariable Long id, @RequestBody UpdateClinicCommand command) {
        return ResponseEntity.ok(clinicService.update(id, command));
    }
}
