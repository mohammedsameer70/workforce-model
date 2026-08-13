package com.boostphysioclinic.workforceapplication.dto.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "version_performance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VersionPerformance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String version;

    private String serviceName;

    private Double performanceScore;

    private Double responseTime;

    private Double errorRate;

    private String status;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
