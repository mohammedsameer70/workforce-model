package com.boostphysioclinic.workforceapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyForecastDTO {
    private Long id;
    private String dayOfWeek;
    private Double predictedDemand;
    private Double actualDemand;
    private Double variance;
    private String department;
    private String weekStartDate;
}
