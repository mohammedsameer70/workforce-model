package com.boostphysioclinic.workforceapplication.dto.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Profile Settings
    @Column(name = "full_name")
    private String fullName;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "role")
    private String role;
    
    @Column(name = "department")
    private String department;

    // Appearance Settings
    @Column(name = "dark_mode")
    private Boolean darkMode;
    
    @Column(name = "compact_view")
    private Boolean compactView;
    
    @Column(name = "animations")
    private Boolean animations;

    // Notification Settings
    @Column(name = "critical_alerts")
    private Boolean criticalAlerts;
    
    @Column(name = "shift_recommendations")
    private Boolean shiftRecommendations;
    
    @Column(name = "system_monitoring")
    private Boolean systemMonitoring;
    
    @Column(name = "email_digest")
    private Boolean emailDigest;

    // Config Settings
    @Column(name = "model_name")
    private String model;
    
    @Column(name = "refresh_interval")
    private String refresh;
    
    @Column(name = "api_url")
    private String apiUrl;
    
    @Column(name = "ml_url")
    private String mlUrl;
    
    @Column(name = "data_retention")
    private Integer dataRetention;

    // AI Model Settings
    @Column(name = "active_model")
    private String activeModel;
    
    @Column(name = "model_version")
    private String modelVersion;
    
    @Column(name = "training_frequency")
    private String trainingFrequency;
    
    @Column(name = "confidence_threshold")
    private Integer confidenceThreshold;
    
    @Column(name = "auto_retrain")
    private Boolean autoRetrain;
    
    @Column(name = "monitoring_enabled")
    private Boolean monitoring;
    
    @Column(name = "feature_importance")
    private Boolean featureImportance;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
