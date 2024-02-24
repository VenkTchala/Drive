package com.example.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;



@Data
@Builder
public class AuthDto implements Serializable {
    private String token;
    private UserDto user;
}
