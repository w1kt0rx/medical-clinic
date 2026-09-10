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
    @Mapping(target = "address.city", source = "city")
    @Mapping(target = "address.postalCode", source = "postalCode")
    @Mapping(target = "address.street", source = "street")
    @Mapping(target = "address.houseNumber", source = "houseNumber")
    @Mapping(target = "address.clinic", ignore = true)
    @Mapping(target = "update", ignore = true)
    Clinic toEntity(CreateClinicCommand command);

    ClinicDto toDto(Clinic clinic);
}
