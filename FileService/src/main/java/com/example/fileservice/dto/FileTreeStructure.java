package com.example.fileservice.dto;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class FileTreeStructure implements Serializable {
    private String fileName;
    private String path;
    private Boolean isDirectory;
    private Long size;
    private String parent;
    private String parentPath;
    private List<FileTreeStructure> children;
}