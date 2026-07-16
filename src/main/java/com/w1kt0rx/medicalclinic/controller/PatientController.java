package com.w1kt0rx.medicalclinic.controller;

import com.w1kt0rx.medicalclinic.command.CreatePatientCommand;
import com.w1kt0rx.medicalclinic.command.UpdatePatientCommand;
import com.w1kt0rx.medicalclinic.dto.PatientDto;
import com.w1kt0rx.medicalclinic.service.PatientService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PatientDto create(@RequestBody CreatePatientCommand command) {
        return patientService.create(command);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{email}")
    public void delete(@PathVariable String email) {
        patientService.delete(email);
    }

    @GetMapping
    public List<PatientDto> findAll() {
        return patientService.findAll();
    }

    @GetMapping("/{email}")
    public PatientDto findByEmail(@PathVariable String email) {
        return patientService.findByEmail(email);
    }

    @PutMapping("/{email}")
    public PatientDto update(@PathVariable String email, @RequestBody UpdatePatientCommand command) {
        return patientService.update(email, command);
    }
}
