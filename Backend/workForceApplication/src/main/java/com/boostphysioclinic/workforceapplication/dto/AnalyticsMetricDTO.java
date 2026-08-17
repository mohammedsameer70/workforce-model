package com.boostphysioclinic.workforceapplication.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class AnalyticsMetricDTO {
    private String name;
    private String value;
    private String title;
    private String icon;
    private String change;


}
