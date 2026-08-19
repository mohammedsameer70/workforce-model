package com.boostphysioclinic.workforceapplication.dto;

import com.boostphysioclinic.workforceapplication.dto.PredictionResultDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PredictionResponse {

    private String model;

    private Integer total_records;

    private Double average_prediction;

    private Double maximum_prediction;

    private Double minimum_prediction;
    private String employee_name;
    private String attendenceStatus;
    private String CapacityUtilization;
    private String alertStatus;
    private String customerOrders;
    private String ProductivityScore;
    private String CapacityLoad;

    private String ScalingEvents;

    private List<PredictionResultDTO> results;

    @JsonDeserialize(using = PredictionListDeserializer.class)
    private List<PredictionResultDTO> predictions;

}