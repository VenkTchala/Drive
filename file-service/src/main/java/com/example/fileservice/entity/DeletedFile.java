package com.example.fileservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class DeletedFile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(value = TemporalType.DATE)
    private LocalDate deletionInstant;

    @ManyToOne
    private FileUser user;

    private String name;

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY ,orphanRemoval = true)
    private FileMetadata metaData;
}
