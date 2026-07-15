package com.w1kt0rx.medicalclinic.service;

import com.w1kt0rx.medicalclinic.model.Patient;
import com.w1kt0rx.medicalclinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository repository;

    public Patient save(Patient patient) {
        if (repository.existsByEmail(patient.getEmail())) {
            throw new RuntimeException("Email jest już zajęty");
        }
        return repository.save(patient);
    }

    public void delete(String email) {
        repository.delete(findByEmail(email));
    }

    public List<Patient> findAll() {
        return repository.findAll();
    }

    public Patient findByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(() -> new RuntimeException("Nie znaleziono pacjenta"));
    }

    public void update(String email, Patient updated) {
        Patient patient = findByEmail(email);

        patient.setFirstName(updated.getFirstName());
        patient.setLastName(updated.getLastName());
        patient.setPassword(updated.getPassword());
        patient.setPhoneNumber(updated.getPhoneNumber());
        patient.setBirthday(updated.getBirthday());
        patient.setIdCardNo(updated.getIdCardNo());
    }

    public void patch(String email, Patient updatedPatient) {

        Patient patient = findByEmail(email);

        if (updatedPatient.getPassword() != null) {
            patient.setPassword(updatedPatient.getPassword());
        }

        if (updatedPatient.getIdCardNo() != null) {
            patient.setIdCardNo(updatedPatient.getIdCardNo());
        }

        if (updatedPatient.getFirstName() != null) {
            patient.setFirstName(updatedPatient.getFirstName());
        }

        if (updatedPatient.getLastName() != null) {
            patient.setLastName(updatedPatient.getLastName());
        }

        if (updatedPatient.getPhoneNumber() != null) {
            patient.setPhoneNumber(updatedPatient.getPhoneNumber());
        }

        if (updatedPatient.getBirthday() != null) {
            patient.setBirthday(updatedPatient.getBirthday());
        }
    }
}
