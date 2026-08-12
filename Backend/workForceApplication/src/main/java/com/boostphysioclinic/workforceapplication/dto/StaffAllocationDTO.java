package com.boostphysioclinic.workforceapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffAllocationDTO {
    private String department;
    private Double predictedDemand;
    private Integer currentStaff;
    private Integer recommendedStaff;
    private Integer surplus;
    private Integer shortage;
}
