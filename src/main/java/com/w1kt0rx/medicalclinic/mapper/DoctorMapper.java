package com.w1kt0rx.medicalclinic.mapper;

import com.w1kt0rx.medicalclinic.command.CreateDoctorCommand;
import com.w1kt0rx.medicalclinic.dto.DoctorDto;
import com.w1kt0rx.medicalclinic.model.Doctor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DoctorMapper {
    Doctor toEntity(CreateDoctorCommand command);

    DoctorDto toDto(Doctor doctor);
}
