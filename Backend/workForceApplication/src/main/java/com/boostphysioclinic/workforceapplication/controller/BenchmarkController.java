package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.dto.BenchmarkMetricDTO;
import com.boostphysioclinic.workforceapplication.dto.LatencyPointDTO;
import com.boostphysioclinic.workforceapplication.dto.VersionHistoryDTO;
import com.boostphysioclinic.workforceapplication.dto.ExperimentDTO;
import com.boostphysioclinic.workforceapplication.service.BenchmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/benchmark")
@RequiredArgsConstructor
public class BenchmarkController {

    private final BenchmarkService benchmarkService;

    @GetMapping("/metrics")
    public List<BenchmarkMetricDTO> getMetrics() {
        return benchmarkService.getMetrics();
    }

    @GetMapping("/latency")
    public List<LatencyPointDTO> getLatencySeries() {
        return benchmarkService.getLatencySeries();
    }

    @GetMapping("/versions")
    public List<VersionHistoryDTO> getVersionHistory() {
        return benchmarkService.getVersionHistory();
    }

    @GetMapping("/experiments")
    public List<ExperimentDTO> getExperiments() {
        return benchmarkService.getExperiments();
    }
}
