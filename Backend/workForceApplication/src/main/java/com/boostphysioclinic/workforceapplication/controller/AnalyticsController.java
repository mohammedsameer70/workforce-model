package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.dto.AnalyticsMetricDTO;
import com.boostphysioclinic.workforceapplication.dto.TimeSeriesPointDTO;
import com.boostphysioclinic.workforceapplication.dto.DepartmentDistributionDTO;
import com.boostphysioclinic.workforceapplication.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/metrics")
    public List<AnalyticsMetricDTO> getMetrics() {
        return analyticsService.getMetrics();
    }

    @GetMapping("/hourly-throughput")
    public List<TimeSeriesPointDTO> getHourlyThroughput() {
        return analyticsService.getHourlyThroughput();
    }

    @GetMapping("/demand-forecast")
    public List<TimeSeriesPointDTO> getDemandForecast() {
        return analyticsService.getDemandForecast();
    }

    @GetMapping("/department-distribution")
    public List<DepartmentDistributionDTO> getDepartmentDistribution() {
        return analyticsService.getDepartmentDistribution();
    }

    @GetMapping("/weekly-comparison")
    public List<TimeSeriesPointDTO> getWeeklyComparison() {
        return analyticsService.getWeeklyComparison();
    }
}
