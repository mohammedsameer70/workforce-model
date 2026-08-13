package com.boostphysioclinic.workforceapplication.dto.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "dataset")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dataset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String fileName;

    private String filePath;

    private Long fileSize;

    private Integer recordCount;

    private String status;

    private String description;

    private LocalDateTime uploadedAt;

    private LocalDateTime processedAt;

    @PrePersist
    public void onCreate() {
        uploadedAt = LocalDateTime.now();
        status = "UPLOADED";
    }
}
