package com.boostphysioclinic.workforceapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftRecommendationDTO {
    private Long id;
    private String title;
    private String priority;
    private String workers;
    private String from;
    private String to;
}
