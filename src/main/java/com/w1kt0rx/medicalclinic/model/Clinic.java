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
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id", nullable = false, unique = true)
    private Address address;
    @ManyToMany(mappedBy = "clinics")
    private Set<Doctor> doctors = new HashSet<>();

    public void setAddress(Address address) {
        this.address = address;
        if (address != null && address.getClinic() != this) {
            address.setClinic(this);
        }
    }

    public boolean addDoctor(Doctor doctor) {
        boolean added = this.doctors.add(doctor);
        if (added) {
            doctor.getClinics().add(this);
        }
        return added;
    }

    public void removeDoctor(Doctor doctor) {
        boolean removed = this.doctors.remove(doctor);
        if (removed) {
            doctor.getClinics().remove(this);
        }
    }

    public Clinic update(UpdateClinicCommand command) {
        this.name = command.name();
        address.update(command);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Clinic))
            return false;
        Clinic other = (Clinic) o;
        return id != null &&
                id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
