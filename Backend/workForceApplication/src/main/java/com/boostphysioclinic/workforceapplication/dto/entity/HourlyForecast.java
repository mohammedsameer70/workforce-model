package com.boostphysioclinic.workforceapplication.dto.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "hourly_forecast")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HourlyForecast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hour;

    private Double predictedDemand;

    private Double confidenceInterval;

    private String department;

    private LocalDateTime forecastDate;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
