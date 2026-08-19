package com.w1kt0rx.medicalclinic.service;

import com.w1kt0rx.medicalclinic.command.CreateClinicCommand;
import com.w1kt0rx.medicalclinic.command.UpdateClinicCommand;
import com.w1kt0rx.medicalclinic.dto.ClinicDto;
import com.w1kt0rx.medicalclinic.exception.ClinicAlreadyExistsException;
import com.w1kt0rx.medicalclinic.exception.ClinicNotFoundException;
import com.w1kt0rx.medicalclinic.mapper.AddressMapper;
import com.w1kt0rx.medicalclinic.mapper.ClinicMapper;
import com.w1kt0rx.medicalclinic.model.Address;
import com.w1kt0rx.medicalclinic.model.Clinic;
import com.w1kt0rx.medicalclinic.repository.ClinicRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ClinicServiceTest {

    ClinicService clinicService;
    ClinicRepository clinicRepository;
    ClinicMapper clinicMapper;

    @BeforeEach
    void setup() {
        this.clinicRepository = Mockito.mock(ClinicRepository.class);
        this.clinicMapper = Mappers.getMapper(ClinicMapper.class);
        ReflectionTestUtils.setField(clinicMapper, "addressMapper", Mappers.getMapper(AddressMapper.class));
        this.clinicService = new ClinicService(clinicRepository, clinicMapper);
    }

    @Test
    void create_dataCorrect_clinicReturned() {
        //given
        CreateClinicCommand command = new CreateClinicCommand("Zdrowie", "Warszawa", "00-001", "Zdrowia", "1");
        Address address = new Address(1L, "Warszawa", "00-001", "Zdrowia", "1", null);
        Clinic clinic = new Clinic(1L, "Zdrowie", address, new HashSet<>());
        ArgumentCaptor<Clinic> clinicCaptor = ArgumentCaptor.forClass(Clinic.class);
        when(clinicRepository.existsByName(command.name())).thenReturn(Boolean.FALSE);
        when(clinicRepository.save(any(Clinic.class))).thenReturn(clinic);
        //when
        ClinicDto clinicDto = clinicService.create(command);
        //then
        Mockito.verify(clinicRepository).save(clinicCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, clinicDto.id()),
                () -> Assertions.assertEquals("Zdrowie", clinicDto.name()),
                () -> Assertions.assertEquals(1L, clinicDto.address().id()),
                () -> Assertions.assertEquals("Warszawa", clinicDto.address().city()),
                () -> Assertions.assertEquals("00-001", clinicDto.address().postalCode()),
                () -> Assertions.assertEquals("Zdrowia", clinicDto.address().street()),
                () -> Assertions.assertEquals("1", clinicDto.address().houseNumber()),
                () -> Assertions.assertEquals("Zdrowie", clinicCaptor.getValue().getName()),
                () -> Assertions.assertEquals("Warszawa", clinicCaptor.getValue().getAddress().getCity())
        );
    }

    @Test
    void create_clinicAlreadyExists_throwsException() {
        //given
        CreateClinicCommand command = new CreateClinicCommand("Zdrowie", "Warszawa", "00-001", "Zdrowia", "1");
        when(clinicRepository.existsByName(command.name())).thenReturn(Boolean.TRUE);
        //when + then
        Assertions.assertThrows(ClinicAlreadyExistsException.class, () -> clinicService.create(command));
        Mockito.verify(clinicRepository, Mockito.never()).save(any());
    }

    @Test
    void delete_clinicExists_clinicDeleted() {
        //given
        Address address = new Address(1L, "Warszawa", "00-001", "Zdrowia", "1", null);
        Clinic clinic = new Clinic(1L, "Zdrowie", address, new HashSet<>());
        ArgumentCaptor<Clinic> clinicCaptor = ArgumentCaptor.forClass(Clinic.class);
        when(clinicRepository.findById(clinic.getId())).thenReturn(Optional.of(clinic));
        //when
        clinicService.delete(clinic.getId());
        //then
        Mockito.verify(clinicRepository).delete(clinicCaptor.capture());
        Assertions.assertEquals(clinic, clinicCaptor.getValue());
    }

    @Test
    void delete_clinicNotExists_throwsException() {
        //given
        when(clinicRepository.findById(1L)).thenReturn(Optional.empty());
        //when + then
        Assertions.assertThrows(ClinicNotFoundException.class, () -> clinicService.delete(1L));
        Mockito.verify(clinicRepository, Mockito.never()).delete(any());
    }

    @Test
    void findAll_dataCorrect_clinicsReturned() {
        //given
        Address address1 = new Address(1L, "Warszawa", "00-001", "Zdrowia", "1", null);
        Address address2 = new Address(2L, "Krakow", "30-001", "Zielona", "5", null);
        Clinic clinic1 = new Clinic(1L, "Zdrowie", address1, new HashSet<>());
        Clinic clinic2 = new Clinic(2L, "Nowa Klinika", address2, new HashSet<>());
        when(clinicRepository.findAll()).thenReturn(List.of(clinic1, clinic2));
        //when
        List<ClinicDto> clinics = clinicService.findAll();
        //then
        Mockito.verify(clinicRepository).findAll();
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, clinics.size()),
                () -> Assertions.assertEquals(1L, clinics.getFirst().id()),
                () -> Assertions.assertEquals("Zdrowie", clinics.getFirst().name()),
                () -> Assertions.assertEquals("Warszawa", clinics.getFirst().address().city()),
                () -> Assertions.assertEquals(2L, clinics.get(1).id()),
                () -> Assertions.assertEquals("Nowa Klinika", clinics.get(1).name()),
                () -> Assertions.assertEquals("Krakow", clinics.get(1).address().city())
        );
    }

    @Test
    void findById_clinicExists_clinicReturned() {
        //given
        Address address = new Address(1L, "Warszawa", "00-001", "Zdrowia", "1", null);
        Clinic clinic = new Clinic(1L, "Zdrowie", address, new HashSet<>());
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        when(clinicRepository.findById(clinic.getId())).thenReturn(Optional.of(clinic));
        //when
        ClinicDto clinicDto = clinicService.findById(clinic.getId());
        //then
        Mockito.verify(clinicRepository).findById(idCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals(clinic.getId(), clinicDto.id()),
                () -> Assertions.assertEquals(clinic.getName(), clinicDto.name()),
                () -> Assertions.assertEquals(clinic.getAddress().getCity(), clinicDto.address().city()),
                () -> Assertions.assertEquals(1L, idCaptor.getValue())
        );
    }

    @Test
    void findById_clinicNotExists_throwsException() {
        //given
        when(clinicRepository.findById(1L)).thenReturn(Optional.empty());
        //when + then
        Assertions.assertThrows(ClinicNotFoundException.class, () -> clinicService.findById(1L));
    }

    @Test
    void update_clinicExists_updatedClinicReturned() {
        //given
        Address address = new Address(1L, "Warszawa", "00-001", "Zdrowia", "1", null);
        Clinic clinic = new Clinic(1L, "Zdrowie", address, new HashSet<>());
        UpdateClinicCommand command = new UpdateClinicCommand("Nowa Nazwa", "Krakow", "30-001", "Zielona", "5");
        ArgumentCaptor<Clinic> clinicCaptor = ArgumentCaptor.forClass(Clinic.class);
        when(clinicRepository.findById(clinic.getId())).thenReturn(Optional.of(clinic));
        when(clinicRepository.save(any(Clinic.class))).thenReturn(clinic);
        //when
        ClinicDto clinicDto = clinicService.update(clinic.getId(), command);
        //then
        Mockito.verify(clinicRepository).save(clinicCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, clinicDto.id()),
                () -> Assertions.assertEquals("Nowa Nazwa", clinicDto.name()),
                () -> Assertions.assertEquals("Krakow", clinicDto.address().city()),
                () -> Assertions.assertEquals("30-001", clinicDto.address().postalCode()),
                () -> Assertions.assertEquals("Zielona", clinicDto.address().street()),
                () -> Assertions.assertEquals("5", clinicDto.address().houseNumber()),
                () -> Assertions.assertEquals("Nowa Nazwa", clinicCaptor.getValue().getName())
        );
    }

    @Test
    void update_clinicNotExists_throwsException() {
        //given
        UpdateClinicCommand command = new UpdateClinicCommand("Nowa Nazwa", "Krakow", "30-001", "Zielona", "5");
        when(clinicRepository.findById(1L)).thenReturn(Optional.empty());
        //when + then
        Assertions.assertThrows(ClinicNotFoundException.class, () -> clinicService.update(1L, command));
        Mockito.verify(clinicRepository, Mockito.never()).save(any());
    }
}