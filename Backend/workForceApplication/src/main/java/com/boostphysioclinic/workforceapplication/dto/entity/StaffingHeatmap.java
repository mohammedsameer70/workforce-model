package com.boostphysioclinic.workforceapplication.dto.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "staffing_heatmap")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffingHeatmap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String department;

    private String timeSlot;

    private String dayOfWeek;

    private Double staffingLevel;

    private Double demandLevel;

    private String status;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
