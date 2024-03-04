package com.example.driveclient.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogInStatus {
    private boolean sucess;
    private String token;
}
