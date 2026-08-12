package com.boostphysioclinic.workforceapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptimizationResultDTO {
    private Integer optimizationScore;
    private Integer employeesReassigned;
    private Integer departmentsOptimized;
    private Double estimatedSavingsPercentage;
    private Double currentUtilization;
    private Double optimizedUtilization;
    private Double dailyCostBefore;
    private Double dailyCostAfter;
    private Double dailySavings;
}
