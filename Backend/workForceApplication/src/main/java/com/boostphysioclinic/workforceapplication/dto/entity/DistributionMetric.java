package com.boostphysioclinic.workforceapplication.dto.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "distribution_metric")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DistributionMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;

    private Double value;

    private Double percentage;

    private String metricType;

    private String department;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
