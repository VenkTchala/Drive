package com.example.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenDto {
    private boolean sucess;
    private String token;
}
