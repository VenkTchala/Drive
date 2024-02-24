package com.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DriveUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
