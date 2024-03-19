package com.example.fileservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Status {
    private boolean status;
    private String errMsg;
}
