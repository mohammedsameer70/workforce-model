package com.boostphysioclinic.workforceapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDataDTO {
    private SummaryData summary;
    private List<DepartmentData> departments;
    private List<TrendData> trend;
    private List<RecentPredictionData> recentPredictions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SummaryData {
        private String model;
        private Long totalRecords;
        private Double averagePrediction;
        private Double maximumPrediction;
        private Double minimumPrediction;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DepartmentData {
        private String department;
        private Double averagePrediction;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TrendData {
        private String date;
        private Double predictedDemand;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecentPredictionData {
        private Long id;
        private String date;
        private String department;
        private Double actualDemand;
        private Double predictedDemand;
    }
}
