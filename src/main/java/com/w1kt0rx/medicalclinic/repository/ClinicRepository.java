package com.w1kt0rx.medicalclinic.repository;

import com.w1kt0rx.medicalclinic.model.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClinicRepository extends JpaRepository<Clinic, Long> {
    boolean existsByName(String name);
}
