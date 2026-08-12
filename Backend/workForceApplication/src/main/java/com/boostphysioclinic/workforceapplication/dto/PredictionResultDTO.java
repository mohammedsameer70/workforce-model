package com.boostphysioclinic.workforceapplication.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictionResultDTO {

    @JsonProperty("AttendanceDate")
    private String attendanceDate;

    @JsonProperty("Department")
    private String department;

    @JsonProperty("ActualDemand")
    private Double actualDemand;

    @JsonProperty("PredictedDemand")
    private Double predictedDemand;

}