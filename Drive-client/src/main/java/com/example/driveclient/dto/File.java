package com.example.driveclient.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;


@Data
@Builder
public class File {
    private Long id;
    private boolean isDirectory;
    private String name;
    private Long size;
    private Instant modifiedAt;
    private String owner;
}
