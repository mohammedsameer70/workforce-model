package com.boostphysioclinic.workforceapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeReassignmentDTO {
    private Integer employeesToMove;
    private String fromDepartment;
    private String toDepartment;
    private String reason;
}
