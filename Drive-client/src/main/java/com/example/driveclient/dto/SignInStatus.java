package com.example.driveclient.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignInStatus {
    private boolean sucess;
    private String errorMessage;
}
