package com.boostphysioclinic.workforceapplication.dto.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String type;

    private String description;

    private LocalDateTime generatedAt;

    private Long fileSize;

    private String filePath;

    private String status;

    private String generatedBy;

    @Lob
    @Column(name = "file_data")
    private byte[] fileData;

    @PrePersist
    public void onCreate() {
        generatedAt = LocalDateTime.now();
        status = "GENERATED";
    }
}
