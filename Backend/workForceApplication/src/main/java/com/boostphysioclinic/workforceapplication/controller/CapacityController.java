package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.dto.CapacityMetricDTO;
import com.boostphysioclinic.workforceapplication.dto.TimeSeriesDTO;
import com.boostphysioclinic.workforceapplication.dto.BenchmarkPointDTO;
import com.boostphysioclinic.workforceapplication.dto.DepartmentCapacityDTO;
import com.boostphysioclinic.workforceapplication.service.CapacityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/capacity")
@RequiredArgsConstructor
public class CapacityController {

    private final CapacityService capacityService;

    @GetMapping("/metrics")
    public List<CapacityMetricDTO> getMetrics() {
        return capacityService.getMetrics();
    }

    @GetMapping("/trend")
    public List<TimeSeriesDTO> getCapacityTrend() {
        return capacityService.getCapacityTrend();
    }

    @GetMapping("/departments")
    public List<DepartmentCapacityDTO> getDepartments() {
        return capacityService.getDepartments();
    }

    @GetMapping("/benchmark")
    public List<BenchmarkPointDTO> getBenchmark() {
        return capacityService.getBenchmark();
    }
}
