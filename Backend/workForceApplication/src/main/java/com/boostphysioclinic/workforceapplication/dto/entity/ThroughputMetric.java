package com.boostphysioclinic.workforceapplication.dto.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "throughput_metric")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThroughputMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String processName;

    private Double throughput;

    private String unit;

    private String department;

    private LocalDateTime timestamp;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
