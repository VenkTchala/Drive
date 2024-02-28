package com.example.driveclient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSignIn {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
