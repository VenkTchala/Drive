package com.example.fileservice.repository;

import com.example.fileservice.entity.DriveFile;
import com.example.fileservice.entity.FileUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<DriveFile,Long> {
    List<DriveFile> getDriveFilesByUser(FileUser user);
    Boolean existsByUserAndFileName(FileUser user, String filename);
}
