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
@Entity
@Table(name = "Doctor")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String specialization;
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "Doctor_Clinic",
            joinColumns = {@JoinColumn(name = "doctor_id")},
            inverseJoinColumns = {@JoinColumn(name = "clinic_id")}
    )
    private Set<Clinic> clinics = new HashSet<>();
    @OneToMany(mappedBy = "doctor")
    private List<Visit> visits = new ArrayList<>();
    public Doctor update(UpdateDoctorCommand command, Set<Clinic> clinics) {
        this.specialization = command.specialization();
        this.clinics = clinics;
        return this;
    }
}
