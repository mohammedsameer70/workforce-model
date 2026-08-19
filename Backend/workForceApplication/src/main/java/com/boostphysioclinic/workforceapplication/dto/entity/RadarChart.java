package com.boostphysioclinic.workforceapplication.dto.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "radar_chart")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RadarChart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String metric;

    @Column(name = "\"value\"")
    private Double value;

    private String category;

    private String department;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
