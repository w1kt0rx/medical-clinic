package com.w1kt0rx.medicalclinic.repository;

import com.w1kt0rx.medicalclinic.model.Patient;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static java.util.Optional.ofNullable;

@Repository
@RequiredArgsConstructor
public class PatientRepository {

    private final List<Patient> patients;

    public Patient save(Patient patient) {
        ofNullable(patient)
                .ifPresent(patients::add);
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
                .filter(patient -> patient.getEmail().equals(email)).findFirst();
    }

    public boolean existsByEmail(String email) {
        return patients.stream()
                .anyMatch(patient -> patient.getEmail().equals(email));
    }
}
