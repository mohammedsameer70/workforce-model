package com.boostphysioclinic.workforceapplication.dto.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "prediction_result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictionResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate attendanceDate;

    private String department;

    private Double actualDemand;

    private Double predictedDemand;
    private String employee_name;
    private String attendenceStatus;
    private String CapacityUtilization;
    private String alertStatus;
    private String customerOrders;
    private String ProductivityScore;
    private String CapacityLoad;

    private String ScalingEvents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "run_id")
    private PredictionRun predictionRun;


}