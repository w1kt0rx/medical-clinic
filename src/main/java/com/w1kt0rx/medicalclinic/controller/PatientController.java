package com.w1kt0rx.medicalclinic.controller;

import com.w1kt0rx.medicalclinic.command.CreatePatientCommand;
import com.w1kt0rx.medicalclinic.command.UpdatePasswordCommand;
import com.w1kt0rx.medicalclinic.command.UpdatePatientCommand;
import com.w1kt0rx.medicalclinic.dto.PatientDto;
import com.w1kt0rx.medicalclinic.service.PatientService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<PatientDto> create(@RequestBody CreatePatientCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.create(command));
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> delete(@PathVariable String email) {
        patientService.delete(email);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<List<PatientDto>> findAll() {
        return ResponseEntity.ok(patientService.findAll());
    }

    @GetMapping("/{email}")
    public ResponseEntity<PatientDto> findByEmail(@PathVariable String email) {
        return ResponseEntity.ok(patientService.findByEmail(email));
    }

    @PutMapping("/{email}")
    public ResponseEntity<PatientDto> update(@PathVariable String email, @RequestBody UpdatePatientCommand command) {
        return ResponseEntity.ok(patientService.update(email, command));
    }

    @PatchMapping("/{email}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable String email, @RequestBody UpdatePasswordCommand command) {
        patientService.updatePassword(email, command.password());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
