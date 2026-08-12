package com.boostphysioclinic.workforceapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffingHeatmapDTO {
    private String department;
    private Integer morning;
    private Integer afternoon;
    private Integer night;
    private Integer total;
}
