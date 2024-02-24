package com.example.fileservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class FileMetaData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long size;
    @OneToOne(cascade = CascadeType.ALL , fetch = FetchType.LAZY)
    private DriveFile file;
    @ManyToOne(cascade = CascadeType.ALL , fetch = FetchType.EAGER)
    @JoinColumn()
    private FileUser owner;
    private Instant creationDate;
    private Instant modificationDate;
}
