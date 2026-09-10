package com.w1kt0rx.medicalclinic.repository;

import com.w1kt0rx.medicalclinic.model.Visit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    Page<Visit> findByPatientIsNull(Pageable pageable);
    Page<Visit> findByPatientId(Long patientId, Pageable pageable);
    boolean existsByDoctorIdAndStartDateBeforeAndFinishDateAfter(Long doctorId, LocalDateTime finishDate, LocalDateTime startDate);
}
