package com.example.fileservice.repository;

import com.example.fileservice.entity.DriveFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<DriveFile,Long> {
    boolean existsByPath(String path);
    Optional<DriveFile> findByPath(String path);
}
