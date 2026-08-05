package com.w1kt0rx.medicalclinic.repository;

import com.w1kt0rx.medicalclinic.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findByPatientIsNull();
    List<Visit> findByPatientId(Long patientId);
    boolean existsByDoctorIdAndStartDateBeforeAndFinishDateAfter(Long doctorId, LocalDateTime finishDate, LocalDateTime startDate);
}
