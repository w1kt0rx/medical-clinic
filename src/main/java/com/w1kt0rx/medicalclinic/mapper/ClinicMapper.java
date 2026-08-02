package com.w1kt0rx.medicalclinic.mapper;

import com.w1kt0rx.medicalclinic.command.CreateClinicCommand;
import com.w1kt0rx.medicalclinic.dto.ClinicDto;
import com.w1kt0rx.medicalclinic.model.Clinic;
import com.w1kt0rx.medicalclinic.model.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ClinicMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "doctors", ignore = true)
    public Clinic toEntity(CreateClinicCommand command);

    public ClinicDto toDto(Clinic clinic);
}
