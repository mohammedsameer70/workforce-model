package com.boostphysioclinic.workforceapplication.dto.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String employeeId;

    private String name;

    private String gender;

    private LocalDate dateOfBirth;

    private Double age;

    private String email;

    private String phoneNumber;

    private String department;

    private String role;

    private String employmentType;

    private String team;

    private String manager;

    private String branch;

    private String location;

    private String shift;

    private String preferredShift;

    private Double experienceYears;

    private LocalDate joinDate;

    private String status;

    private String attendance;

    private Double utilization;

    private LocalDateTime lastUpdated;

    @PrePersist
    public void onCreate() {
        lastUpdated = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}