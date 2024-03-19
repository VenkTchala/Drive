package com.example.fileservice.repository;

import com.example.fileservice.entity.FileUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileUserRepository extends JpaRepository<FileUser,Long> {
    boolean existsByUsername(String username);
    Optional<FileUser> findDistinctByUsername(String username);
}
