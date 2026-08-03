package com.w1kt0rx.medicalclinic.service;

import com.w1kt0rx.medicalclinic.command.CreateClinicCommand;
import com.w1kt0rx.medicalclinic.command.UpdateClinicCommand;
import com.w1kt0rx.medicalclinic.dto.ClinicDto;
import com.w1kt0rx.medicalclinic.exception.ClinicNotFoundException;
import com.w1kt0rx.medicalclinic.mapper.ClinicMapper;
import com.w1kt0rx.medicalclinic.model.Address;
import com.w1kt0rx.medicalclinic.model.Clinic;
import com.w1kt0rx.medicalclinic.repository.AddressRepository;
import com.w1kt0rx.medicalclinic.repository.ClinicRepository;
import com.w1kt0rx.medicalclinic.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ClinicService {

    private final ClinicRepository clinicRepository;
    private final AddressRepository addressRepository;
    private final DoctorRepository doctorRepository;
    private final ClinicMapper mapper;

    public ClinicDto create(CreateClinicCommand command) {
        Address address = addressRepository.getAddressesById(command.addressId());
        Clinic clinic = mapper.toEntity(command);
        clinic.setAddress(address);
        return mapper.toDto(clinicRepository.save(clinic));
    }

    public void delete(Long id) {
        clinicRepository.delete(getClinicById(id));
    }

    public List<ClinicDto> findAll() {
        return clinicRepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    public ClinicDto findById(Long id) {
        return mapper.toDto(getClinicById(id));
    }

    public ClinicDto update(Long id, UpdateClinicCommand command) {
        Clinic clinic = getClinicById(id).update(command);
        return mapper.toDto(clinicRepository.save(clinic));
    }

    private Clinic getClinicById(Long id) {
        return clinicRepository.findById(id)
                .orElseThrow(() -> new ClinicNotFoundException(String.format("Nie znaleziono kliniki o id: %d", id), HttpStatus.NOT_FOUND));
    }
}
