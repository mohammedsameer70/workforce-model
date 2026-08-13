package com.boostphysioclinic.workforceapplication.dto.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "kpi_card")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KpiCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String value;

    private String trend;

    private String trendDirection;

    private String unit;

    private String category;

    private LocalDateTime lastUpdated;

    @PrePersist
    public void onCreate() {
        lastUpdated = LocalDateTime.now();
    }
}
