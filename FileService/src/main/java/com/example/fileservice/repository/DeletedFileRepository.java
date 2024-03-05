package com.example.fileservice.repository;

import com.example.fileservice.entity.DeletedFile;
import com.example.fileservice.entity.FileUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeletedFileRepository extends JpaRepository<DeletedFile,Long> {
    List<DeletedFile> getDeletedFilesByUser(FileUser user);
}
