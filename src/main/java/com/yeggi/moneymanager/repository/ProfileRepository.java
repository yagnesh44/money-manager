package com.yeggi.moneymanager.repository;

import com.yeggi.moneymanager.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {

    // Find a profile by email
    Optional<ProfileEntity> findByEmail(String email);

    // Find a profile by activation token
    Optional<ProfileEntity> findByActivationToken(String token);
}
