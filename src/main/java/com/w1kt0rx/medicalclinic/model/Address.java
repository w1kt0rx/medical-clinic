package com.w1kt0rx.medicalclinic.model;

import com.w1kt0rx.medicalclinic.command.UpdateClinicCommand;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "Address")
public class Address {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String city;
    private String postalCode;
    private String street;
    private String houseNumber;
    @OneToOne(mappedBy = "address")
    private Clinic clinic;

    public Address update(UpdateClinicCommand command) {
        this.city = command.city();
        this.postalCode = command.postalCode();
        this.street = command.street();
        this.houseNumber = command.houseNumber();
        return this;
    }
}
