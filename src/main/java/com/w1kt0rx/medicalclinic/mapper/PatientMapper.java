package com.w1kt0rx.medicalclinic.mapper;

import com.w1kt0rx.medicalclinic.command.CreatePatientCommand;
import com.w1kt0rx.medicalclinic.dto.PatientDto;
import com.w1kt0rx.medicalclinic.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    Patient toEntity(CreatePatientCommand command);

    PatientDto toDto(Patient patient);
}
