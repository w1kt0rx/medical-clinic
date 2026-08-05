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
@Entity
@Table(name = "Patient")
public class Patient {

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
    public Patient update(UpdatePatientCommand command) {
        this.idCardNo = command.idCardNo();
        this.birthday = command.birthday();
        return this;
    }
}
