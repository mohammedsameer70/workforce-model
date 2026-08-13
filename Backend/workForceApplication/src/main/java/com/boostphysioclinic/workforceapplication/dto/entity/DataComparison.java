package com.boostphysioclinic.workforceapplication.dto.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "data_comparison")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataComparison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;

    private Double actualValue;

    private Double predictedValue;

    private Double difference;

    private String dataSource;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
