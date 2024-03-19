package com.example.fileservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DriveFile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String fileName;
    @ManyToOne
    private FileUser user;
    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private FileMetadata metaData;
}
