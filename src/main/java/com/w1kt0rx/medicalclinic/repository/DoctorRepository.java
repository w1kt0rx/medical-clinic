package com.w1kt0rx.medicalclinic.repository;

import com.w1kt0rx.medicalclinic.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}
