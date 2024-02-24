package com.example.repository;

import com.example.entity.DriveUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriveUserRepository extends JpaRepository<DriveUser,Long> {
    Optional<DriveUser> getDriveUserByEmail(String email);
    Boolean existsByEmail(String email);
}
