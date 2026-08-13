package com.boostphysioclinic.workforceapplication.dto.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "latency_metric")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LatencyMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String endpoint;

    private Double p50;

    private Double p95;

    private Double p99;

    private String loadLevel;

    private LocalDateTime timestamp;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
