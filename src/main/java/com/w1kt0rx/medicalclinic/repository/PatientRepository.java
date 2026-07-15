package com.w1kt0rx.medicalclinic.repository;

import com.w1kt0rx.medicalclinic.model.Patient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PatientRepository {

    private final List<Patient> patients;
    private Long nextId = 1L;

    public PatientRepository() {
        this.patients = new ArrayList<>();
    }

    public Patient save(Patient patient) {
        if (patient.getId() == null) {
            patient.setId(getNextId());
        }
        patients.add(patient);
        return patient;
    }

    public void delete(Patient patient) {
        patients.remove(patient);
    }

    public List<Patient> findAll() {
        return List.copyOf(patients);
    }

    public Optional<Patient> findByEmail(String email) {
        return patients.stream()
                .filter(patient -> patient.getEmail().equals(email))
                .findFirst();
    }

    public boolean existsByEmail(String email) {
        return patients.stream()
                .anyMatch(patient -> patient.getEmail().equals(email));
    }

    private Long getNextId() {
        return nextId++;
    }
}
