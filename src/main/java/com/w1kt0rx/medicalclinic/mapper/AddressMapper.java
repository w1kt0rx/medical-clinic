package com.w1kt0rx.medicalclinic.mapper;

import com.w1kt0rx.medicalclinic.dto.AddressDto;
import com.w1kt0rx.medicalclinic.model.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        uses = ClinicMapper.class)
public interface AddressMapper {
    AddressDto toDto(Address address);
}
