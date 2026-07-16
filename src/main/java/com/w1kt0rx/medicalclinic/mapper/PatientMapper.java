package com.w1kt0rx.medicalclinic.mapper;

import com.w1kt0rx.medicalclinic.command.CreatePatientCommand;
import com.w1kt0rx.medicalclinic.dto.PatientDto;
import com.w1kt0rx.medicalclinic.model.Patient;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PatientMapper {

    public static Patient toEntity(CreatePatientCommand command) {
        return new Patient(
            command.email(),
            command.password(),
            command.idCardNo(),
            command.firstName(),
            command.lastName(),
            command.phoneNumber(),
            command.birthday()
        );
    }

    public static PatientDto toDto(Patient patient) {
        return new PatientDto(
            patient.getEmail(),
            patient.getPassword(),
            patient.getIdCardNo(),
            patient.getFirstName(),
            patient.getLastName(),
            patient.getPhoneNumber(),
            patient.getBirthday()
        );
    }
}
