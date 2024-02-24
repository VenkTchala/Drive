package com.example.fileservice.repository;

import com.example.fileservice.entity.DriveFile;
import com.example.fileservice.entity.FileMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileMetaDataRepository extends JpaRepository<FileMetaData,Long> {
    FileMetaData findByFile(DriveFile file);
}
