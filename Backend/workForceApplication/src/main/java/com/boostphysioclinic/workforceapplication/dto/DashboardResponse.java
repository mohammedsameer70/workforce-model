package com.boostphysioclinic.workforceapplication.dto;


public class DashboardResponse {

    private String model;

    private Integer totalRecords;

    private Double averagePrediction;

    private Double maximumPrediction;

    private Double minimumPrediction;

    public DashboardResponse() {
    }

    public DashboardResponse(
            String model,
            Integer totalRecords,
            Double averagePrediction,
            Double maximumPrediction,
            Double minimumPrediction
    ) {
        this.model = model;
        this.totalRecords = totalRecords;
        this.averagePrediction = averagePrediction;
        this.maximumPrediction = maximumPrediction;
        this.minimumPrediction = minimumPrediction;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Integer totalRecords) {
        this.totalRecords = totalRecords;
    }

    public Double getAveragePrediction() {
        return averagePrediction;
    }

    public void setAveragePrediction(Double averagePrediction) {
        this.averagePrediction = averagePrediction;
    }

    public Double getMaximumPrediction() {
        return maximumPrediction;
    }

    public void setMaximumPrediction(Double maximumPrediction) {
        this.maximumPrediction = maximumPrediction;
    }

    public Double getMinimumPrediction() {
        return minimumPrediction;
    }

    public void setMinimumPrediction(Double minimumPrediction) {
        this.minimumPrediction = minimumPrediction;
    }
}