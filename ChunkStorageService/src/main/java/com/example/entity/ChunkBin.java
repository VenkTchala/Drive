package com.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.Instant;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ChunkBin {
    @Id
    private Long id;
    private Instant deletionRequest;
    private String chunkName;
}
