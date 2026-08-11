package com.w1kt0rx.medicalclinic.model;

import com.w1kt0rx.medicalclinic.command.UpdatePatientCommand;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "Patient")
public class Patient {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String idCardNo;
    private LocalDate birthday;
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
    @OneToMany(mappedBy = "patient")
    private List<Visit> visits = new ArrayList<>();

    public void setUser(User user) {
        this.user = user;
        if (user != null && user.getPatient() != this) {
            user.setPatient(this);
        }
    }

    public void addVisit(Visit visit) {
        this.visits.add(visit);
        visit.setPatient(this);
    }

    public void removeVisit(Visit visit) {
        this.visits.remove(visit);
        visit.setPatient(null);
    }

    public Patient update(UpdatePatientCommand command) {
        this.idCardNo = command.idCardNo();
        this.birthday = command.birthday();
        return this;
    }
}
