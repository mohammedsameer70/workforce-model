package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.dto.StaffingHeatmapDTO;
import com.boostphysioclinic.workforceapplication.dto.ForecastTrendDTO;
import com.boostphysioclinic.workforceapplication.dto.WeeklyForecastDTO;
import com.boostphysioclinic.workforceapplication.dto.ForecastMetricDTO;
import com.boostphysioclinic.workforceapplication.dto.entity.HourlyForecast;
import com.boostphysioclinic.workforceapplication.dto.entity.WeeklyForecast;
import com.boostphysioclinic.workforceapplication.dto.entity.RadarChart;
import com.boostphysioclinic.workforceapplication.dto.entity.StaffingHeatmap;
import com.boostphysioclinic.workforceapplication.service.ForecastingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forecasting")
@RequiredArgsConstructor
public class ForecastingController {

    private final ForecastingService forecastingService;

    @GetMapping("/hourly")
    public List<ForecastTrendDTO> getAllHourlyForecasts() {
        return forecastingService.getHourlyForecastDTO();
    }

    @GetMapping("/hourly/department/{department}")
    public List<HourlyForecast> getHourlyForecastsByDepartment(@PathVariable String department) {
        return forecastingService.getHourlyForecastsByDepartment(department);
    }

    @PostMapping("/hourly")
    public HourlyForecast createHourlyForecast(@RequestBody HourlyForecast forecast) {
        return forecastingService.createHourlyForecast(forecast);
    }

    @GetMapping("/weekly")
    public List<WeeklyForecastDTO> getAllWeeklyForecasts() {
        return forecastingService.getWeeklyForecastDTO();
    }

    @GetMapping("/weekly/department/{department}")
    public List<WeeklyForecast> getWeeklyForecastsByDepartment(@PathVariable String department) {
        return forecastingService.getWeeklyForecastsByDepartment(department);
    }

    @PostMapping("/weekly")
    public WeeklyForecast createWeeklyForecast(@RequestBody WeeklyForecast forecast) {
        return forecastingService.createWeeklyForecast(forecast);
    }

    @GetMapping("/radar")
    public List<ForecastMetricDTO> getAllRadarCharts() {
        return forecastingService.getForecastMetricsDTO();
    }

    @GetMapping("/radar/department/{department}")
    public List<RadarChart> getRadarChartsByDepartment(@PathVariable String department) {
        return forecastingService.getRadarChartsByDepartment(department);
    }

    @PostMapping("/radar")
    public RadarChart createRadarChart(@RequestBody RadarChart radarChart) {
        return forecastingService.createRadarChart(radarChart);
    }

    @GetMapping("/staffing-heatmap")
    public List<StaffingHeatmapDTO> getAllStaffingHeatmaps() {
        return forecastingService.getStaffingHeatmapData();
    }

    @GetMapping("/staffing-heatmap/department/{department}")
    public List<StaffingHeatmap> getStaffingHeatmapsByDepartment(@PathVariable String department) {
        return forecastingService.getStaffingHeatmapsByDepartment(department);
    }

    @PostMapping("/staffing-heatmap")
    public StaffingHeatmap createStaffingHeatmap(@RequestBody StaffingHeatmap heatmap) {
        return forecastingService.createStaffingHeatmap(heatmap);
    }
}
