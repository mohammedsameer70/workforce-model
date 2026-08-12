package com.boostphysioclinic.workforceapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftCoverageDTO {
    private String department;
    private String morning;
    private String afternoon;
    private String night;
    private String status;
}
