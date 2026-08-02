package com.w1kt0rx.medicalclinic.repository;

import com.w1kt0rx.medicalclinic.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Address getAddressesById(Long id);
}
