package com.w1kt0rx.medicalclinic.repository;

import com.w1kt0rx.medicalclinic.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUserEmail(String email);
    List<Doctor> findBySpecialization(String specialization);
}
