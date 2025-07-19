package com.linsta.linsta_backend.repository;

import com.linsta.linsta_backend.model.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    Optional<UserAddress> findByAddress(String address);
}
