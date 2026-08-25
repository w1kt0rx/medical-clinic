package com.w1kt0rx.medicalclinic.model;

import com.w1kt0rx.medicalclinic.command.UpdateClinicCommand;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Address")
public class Address {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address))
            return false;
        Address other = (Address) o;
        return id != null &&
                id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
