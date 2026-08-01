package com.boostphysioclinic.workforceapplication.dto.dashboard;

public class DashboardResponse {
    private String model;
    private Integer totalRecords;

    public DashboardResponse() {
    }

    public DashboardResponse(String model, Integer totalRecords) {
        this.model = model;
        this.totalRecords = totalRecords;
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
}
