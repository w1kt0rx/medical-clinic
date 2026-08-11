package com.w1kt0rx.medicalclinic.model;

import com.w1kt0rx.medicalclinic.command.UpdateDoctorCommand;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "Doctor")
public class Doctor {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String specialization;
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
    @ManyToMany
    @JoinTable(
            name = "Doctor_Clinic",
            joinColumns = {@JoinColumn(name = "doctor_id")},
            inverseJoinColumns = {@JoinColumn(name = "clinic_id")}
    )
    private Set<Clinic> clinics = new HashSet<>();
    @OneToMany(mappedBy = "doctor")
    private List<Visit> visits = new ArrayList<>();

    public void setUser(User user) {
        this.user = user;
        if (user != null && user.getDoctor() != this) {
            user.setDoctor(this);
        }
    }

    public boolean addClinic(Clinic clinic) {
        boolean added = this.clinics.add(clinic);
        if (added) {
            clinic.getDoctors().add(this);
        }
        return added;
    }

    public boolean removeClinic(Clinic clinic) {
        boolean removed = this.clinics.remove(clinic);
        if (removed) {
            clinic.getDoctors().remove(this);
        }
        return removed;
    }

    public void addVisit(Visit visit) {
        this.visits.add(visit);
        visit.setDoctor(this);
    }

    public void removeVisit(Visit visit) {
        this.visits.remove(visit);
        visit.setDoctor(null);
    }

    public Doctor update(UpdateDoctorCommand command, Set<Clinic> clinics) {
        this.specialization = command.specialization();
        new HashSet<>(this.clinics).forEach(this::removeClinic);
        clinics.forEach(this::addClinic);
        return this;
    }
}

