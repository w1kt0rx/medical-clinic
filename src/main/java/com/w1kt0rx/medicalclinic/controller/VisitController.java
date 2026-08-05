package com.w1kt0rx.medicalclinic.controller;

import com.w1kt0rx.medicalclinic.command.CreateVisitCommand;
import com.w1kt0rx.medicalclinic.command.RegisterPatientForVisitCommand;
import com.w1kt0rx.medicalclinic.dto.VisitDto;
import com.w1kt0rx.medicalclinic.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/visits")
@RequiredArgsConstructor
public class VisitController {
    private final VisitService service;

    @PostMapping
    public VisitDto create(@RequestBody CreateVisitCommand command) {
        return service.create(command);
    }

    @PutMapping("/{id}/register")
    public VisitDto registerPatient(@PathVariable Long id, @RequestBody RegisterPatientForVisitCommand command){
        return service.registerPatient(id, command);
    }

    @GetMapping
    public List<VisitDto> findAll() {
        return service.findAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/{id}")
    public VisitDto findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/free")
    public List<VisitDto> findFreeVisits() {
        return service.findFreeVisits();
    }

    @GetMapping("/patient/{patientId}")
    public List<VisitDto> findPatientVisits(@PathVariable Long patientId) {
        return service.findPatientVisits(patientId);
    }
}
