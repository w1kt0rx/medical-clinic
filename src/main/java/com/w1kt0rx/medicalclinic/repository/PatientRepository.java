package com.w1kt0rx.medicalclinic.repository;

import com.w1kt0rx.medicalclinic.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PatientRepository extends JpaRepository<Patient, Long> {
}
