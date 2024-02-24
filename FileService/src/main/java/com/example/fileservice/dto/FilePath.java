package com.example.fileservice.dto;

import lombok.*;

import java.io.Serializable;


@AllArgsConstructor
@Getter
@Setter
@Builder
public class FilePath implements Serializable {
    private String path;
}
