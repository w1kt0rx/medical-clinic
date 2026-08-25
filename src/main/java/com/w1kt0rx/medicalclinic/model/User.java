package com.w1kt0rx.medicalclinic.model;

import com.w1kt0rx.medicalclinic.command.UpdateUserCommand;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    @OneToOne(mappedBy = "user",
            cascade = CascadeType.ALL)
    private Patient patient;

    @OneToOne(mappedBy = "user",
            cascade = CascadeType.ALL)
    private Doctor doctor;

    public User update(UpdateUserCommand command) {
        this.firstName = command.firstName();
        this.lastName = command.lastName();
        this.phoneNumber = command.phoneNumber();
        return this;
    }

    public User updatePassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User))
            return false;
        User other = (User) o;
        return id != null &&
                id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

