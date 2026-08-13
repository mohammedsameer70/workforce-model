package com.boostphysioclinic.workforceapplication.dto.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_model")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String algorithm;

    private String version;

    private String status;

    private Double rmse;

    private Double mae;

    private Double mape;

    private Double rSquared;

    private Long trainingTime;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime lastTrained;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dataset_id")
    private Dataset dataset;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        status = "CREATED";
    }
}
