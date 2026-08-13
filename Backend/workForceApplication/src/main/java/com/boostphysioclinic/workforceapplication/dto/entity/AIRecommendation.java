package com.boostphysioclinic.workforceapplication.dto.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_recommendation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String type;

    private String priority;

    private String department;

    private String shift;

    private Integer recommendedStaff;

    private Integer currentStaff;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime appliedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        status = "PENDING";
    }
}
