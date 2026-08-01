package com.boostphysioclinic.workforceapplication.dto;

public class PredictionRecord {


    private String attendanceDate;
    private String department;
    private String team;
    private String shift;

    private double workforceDemand;
    private double predictedDemand;

    private double currentCapacity;
    private double requiredCapacity;
    private double availableHeadroom;
    private double capacityLoad;

    private double capacityUtilization;
    private double utilizationRate;

    private double efficiencyScore;
    private double productivityScore;

    private double historicalDemand;

    private double peakUtilization;
    private double scalingEvents;

    private String workforceStatus;

    private int dayOfWeek;
    private int month;
    private int quarter;
    private int year;
    public PredictionRecord() {
        this.attendanceDate = attendanceDate;
        this.department = department;
        this.team = team;
        this.shift = shift;
        this.workforceDemand = workforceDemand;
        this.predictedDemand = predictedDemand;
        this.currentCapacity = currentCapacity;
        this.requiredCapacity = requiredCapacity;
        this.availableHeadroom = availableHeadroom;
        this.capacityLoad = capacityLoad;
        this.capacityUtilization = capacityUtilization;
        this.utilizationRate = utilizationRate;
        this.efficiencyScore = efficiencyScore;
        this.productivityScore = productivityScore;
        this.historicalDemand = historicalDemand;
        this.peakUtilization = peakUtilization;
        this.scalingEvents = scalingEvents;
        this.workforceStatus = workforceStatus;
        this.dayOfWeek = dayOfWeek;
        this.month = month;
        this.quarter = quarter;
        this.year = year;
    }

    public String getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(String attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public double getWorkforceDemand() {
        return workforceDemand;
    }

    public void setWorkforceDemand(double workforceDemand) {
        this.workforceDemand = workforceDemand;
    }

    public double getPredictedDemand() {
        return predictedDemand;
    }

    public void setPredictedDemand(double predictedDemand) {
        this.predictedDemand = predictedDemand;
    }

    public double getCurrentCapacity() {
        return currentCapacity;
    }

    public void setCurrentCapacity(double currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    public double getRequiredCapacity() {
        return requiredCapacity;
    }

    public void setRequiredCapacity(double requiredCapacity) {
        this.requiredCapacity = requiredCapacity;
    }

    public double getAvailableHeadroom() {
        return availableHeadroom;
    }

    public void setAvailableHeadroom(double availableHeadroom) {
        this.availableHeadroom = availableHeadroom;
    }

    public double getCapacityLoad() {
        return capacityLoad;
    }

    public void setCapacityLoad(double capacityLoad) {
        this.capacityLoad = capacityLoad;
    }

    public double getCapacityUtilization() {
        return capacityUtilization;
    }

    public void setCapacityUtilization(double capacityUtilization) {
        this.capacityUtilization = capacityUtilization;
    }

    public double getUtilizationRate() {
        return utilizationRate;
    }

    public void setUtilizationRate(double utilizationRate) {
        this.utilizationRate = utilizationRate;
    }

    public double getEfficiencyScore() {
        return efficiencyScore;
    }

    public void setEfficiencyScore(double efficiencyScore) {
        this.efficiencyScore = efficiencyScore;
    }

    public double getProductivityScore() {
        return productivityScore;
    }

    public void setProductivityScore(double productivityScore) {
        this.productivityScore = productivityScore;
    }

    public double getHistoricalDemand() {
        return historicalDemand;
    }

    public void setHistoricalDemand(double historicalDemand) {
        this.historicalDemand = historicalDemand;
    }

    public double getPeakUtilization() {
        return peakUtilization;
    }

    public void setPeakUtilization(double peakUtilization) {
        this.peakUtilization = peakUtilization;
    }

    public double getScalingEvents() {
        return scalingEvents;
    }

    public void setScalingEvents(double scalingEvents) {
        this.scalingEvents = scalingEvents;
    }

    public String getWorkforceStatus() {
        return workforceStatus;
    }

    public void setWorkforceStatus(String workforceStatus) {
        this.workforceStatus = workforceStatus;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getQuarter() {
        return quarter;
    }

    public void setQuarter(int quarter) {
        this.quarter = quarter;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
    // getters and setters
}