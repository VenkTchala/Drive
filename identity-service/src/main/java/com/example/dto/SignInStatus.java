package com.example.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignInStatus {
    private boolean sucess;
    private String errorMessage;
}
