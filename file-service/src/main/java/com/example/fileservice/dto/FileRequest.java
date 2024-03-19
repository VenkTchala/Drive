package com.example.fileservice.dto;

import com.example.fileservice.entity.DriveFile;
import com.example.fileservice.entity.FileUser;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;


@Data
@AllArgsConstructor
@Builder
public class FileRequest {
    private Long id;
    private Long size;
    private String  name;
    private String owner;
    private String modificationDate;
}
