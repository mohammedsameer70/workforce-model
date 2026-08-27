package com.boostphysioclinic.workforceapplication.dto.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "model_comparison")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelComparison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String modelName;

    private String algorithm;

    private Double rmse;

    private Double mae;

    private Double mape;

    @JsonProperty("r2")
    private Double rSquared;

    private Long trainingTime;

    private String status;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
