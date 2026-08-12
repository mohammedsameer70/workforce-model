package com.boostphysioclinic.workforceapplication.dto.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String message;

    private String type;

    private String priority;

    private Boolean isRead;

    private String icon;

    private LocalDateTime createdAt;

    private LocalDateTime readAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        isRead = false;
    }
}
