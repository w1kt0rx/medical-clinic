package com.w1kt0rx.medicalclinic.controller;

import com.w1kt0rx.medicalclinic.model.Patient;
import com.w1kt0rx.medicalclinic.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @PostMapping
    public Patient save(@RequestBody Patient patient) {
        return patientService.save(patient);
    }

    @DeleteMapping("/{email}")
    public void delete(@PathVariable String email) {
        patientService.delete(email);
    }

    @GetMapping
    public List<Patient> findAll() {
        return patientService.findAll();
    }

    @GetMapping("/{patientEmail}")
    public Patient findByEmail(@PathVariable String patientEmail) {
        return patientService.findByEmail(patientEmail);
    }

    @PutMapping("/{email}")
    public void update(@PathVariable String email, @RequestBody Patient patient) {
        patientService.update(email, patient);
    }

    @PatchMapping("/{email}")
    public void patchPatient(@PathVariable String email, @RequestBody Patient patient) {
        patientService.patch(email, patient);
    }
}
