package com.example.fileservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class FileResponse implements Serializable {
    private String fileName;
    private String path;
    private Boolean isDirectory;
    private Long size;
    private String owner;
    private Instant creationDate;
    private Instant modificationDate;
}
