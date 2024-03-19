package com.example.fileservice.repository;

import com.example.fileservice.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileMetaDataRepository extends JpaRepository<FileMetadata,Long> {
}
