package com.boostphysioclinic.workforceapplication.dto.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_health")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceHealth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serviceName;

    private String status;

    private Double cpuUsage;

    private Double memoryUsage;

    private Integer instances;

    private Long uptime;

    private String version;

    private String endpoint;

    private LocalDateTime lastChecked;

    @PrePersist
    public void onCreate() {
        lastChecked = LocalDateTime.now();
    }
}
