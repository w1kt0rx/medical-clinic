package com.w1kt0rx.medicalclinic.mapper;

import com.w1kt0rx.medicalclinic.command.CreateClinicCommand;
import com.w1kt0rx.medicalclinic.dto.ClinicDto;
import com.w1kt0rx.medicalclinic.model.Clinic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = AddressMapper.class)
public interface ClinicMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "doctors", ignore = true)
    @Mapping(target = "address.id", ignore = true)
    public Clinic toEntity(CreateClinicCommand command);

    public ClinicDto toDto(Clinic clinic);
}
