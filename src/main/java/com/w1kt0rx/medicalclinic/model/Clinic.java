package com.w1kt0rx.medicalclinic.model;

import com.w1kt0rx.medicalclinic.command.UpdateClinicCommand;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Clinic")
public class Clinic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;
    @OneToOne
    @JoinColumn(name = "address_id", nullable = false, unique = true)
    private Address address;
    @ManyToMany(mappedBy = "clinics")
    private Set<Doctor> doctors = new HashSet<>();

    public Clinic update(UpdateClinicCommand command) {
        this.name = command.name();
        address.update(command);
        return this;
    }
}
