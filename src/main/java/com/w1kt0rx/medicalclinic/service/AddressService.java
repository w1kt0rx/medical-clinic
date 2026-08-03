package com.w1kt0rx.medicalclinic.service;

import com.w1kt0rx.medicalclinic.command.CreateAddressCommand;
import com.w1kt0rx.medicalclinic.command.UpdateAddressCommand;
import com.w1kt0rx.medicalclinic.dto.AddressDto;
import com.w1kt0rx.medicalclinic.exception.AddressNotFoundException;
import com.w1kt0rx.medicalclinic.mapper.AddressMapper;
import com.w1kt0rx.medicalclinic.model.Address;
import com.w1kt0rx.medicalclinic.repository.AddressRepository;
import com.w1kt0rx.medicalclinic.repository.ClinicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final AddressMapper mapper;

    public AddressDto create(CreateAddressCommand command) {
        Address address = mapper.toEntity(command);
        return mapper.toDto(addressRepository.save(address));
    }

    public void delete(Long id) {
        addressRepository.delete(getAddressesById(id));
    }

    public List<AddressDto> findAll() {
        return addressRepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    public AddressDto findById(Long id) {
        return mapper.toDto(getAddressesById(id));
    }

    public AddressDto update(Long id, UpdateAddressCommand command) {
        Address address = getAddressesById(id);
        return mapper.toDto(addressRepository.save(address.update(command)));
    }


    private Address getAddressesById(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new AddressNotFoundException("Nie znaleziono adresu", HttpStatus.NOT_FOUND));
    }


}
