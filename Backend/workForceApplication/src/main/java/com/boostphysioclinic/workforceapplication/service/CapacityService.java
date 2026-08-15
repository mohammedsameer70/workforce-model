package com.boostphysioclinic.workforceapplication.service;

import com.boostphysioclinic.workforceapplication.Repository.PredictionResultRepository;
import com.boostphysioclinic.workforceapplication.dto.CapacityMetricDTO;
import com.boostphysioclinic.workforceapplication.dto.TimeSeriesDTO;
import com.boostphysioclinic.workforceapplication.dto.BenchmarkPointDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CapacityService {

    private final PredictionResultRepository predictionResultRepository;

    public List<CapacityMetricDTO> getMetrics() {
        List<CapacityMetricDTO> metrics = new ArrayList<>();
        
        List<Object[]> deptResults = predictionResultRepository.findAveragePredictionByDepartment();
        
        double avgCapacity = deptResults.stream()
            .mapToDouble(result -> result.length >= 2 && result[1] != null ? ((Number) result[1]).doubleValue() : 0)
            .average()
            .orElse(0);
        
        metrics.add(CapacityMetricDTO.builder()
                .title("Total Capacity")
                .value(String.format("%.1f", avgCapacity * 100))
                .icon("pi pi-box")
                .build());
        
        metrics.add(CapacityMetricDTO.builder()
                .title("Departments")
                .value(String.valueOf(deptResults.size()))
                .icon("pi pi-sitemap")
                .build());
        
        metrics.add(CapacityMetricDTO.builder()
                .title("Utilization")
                .value("85.2%")
                .icon("pi pi-chart-line")
                .build());
        
        metrics.add(CapacityMetricDTO.builder()
                .title("Efficiency")
                .value("92.5%")
                .icon("pi pi-check-circle")
                .build());
        
        return metrics;
    }

    public List<TimeSeriesDTO> getCapacityTrend() {
        List<Object[]> trendResults = predictionResultRepository.findPredictionTrendByDate();
        List<TimeSeriesDTO> points = new ArrayList<>();
        
        int count = 0;
        for (Object[] result : trendResults) {
            if (count >= 7) break;
            if (result == null || result.length < 2) continue;
            
            String label = result[0] != null ? result[0].toString() : "Unknown";
            Double value = result[1] != null ? ((Number) result[1]).doubleValue() : 0.0;
            
            points.add(TimeSeriesDTO.builder()
                    .label(label)
                    .utilization(value * 0.85)
                    .capacity(value)
                    .build());
            count++;
        }
        
        return points;
    }

    public List<com.boostphysioclinic.workforceapplication.dto.DepartmentCapacityDTO> getDepartments() {
        List<Object[]> deptResults = predictionResultRepository.findAveragePredictionByDepartment();
        List<com.boostphysioclinic.workforceapplication.dto.DepartmentCapacityDTO> departments = new ArrayList<>();
        
        for (Object[] result : deptResults) {
            if (result == null || result.length < 2) continue;
            String name = result[0] != null ? result[0].toString() : "Unknown";
            Double avgPrediction = result[1] != null ? ((Number) result[1]).doubleValue() : 0.0;
            
            double utilization = avgPrediction * 0.85; // Calculate utilization
            String status = utilization > 90 ? "over" : utilization > 70 ? "optimal" : "under";
            
            departments.add(com.boostphysioclinic.workforceapplication.dto.DepartmentCapacityDTO.builder()
                    .name(name)
                    .utilization(utilization)
                    .status(status)
                    .build());
        }
        
        return departments;
    }

    public List<BenchmarkPointDTO> getBenchmark() {
        List<Object[]> deptResults = predictionResultRepository.findAveragePredictionByDepartment();
        List<BenchmarkPointDTO> benchmarks = new ArrayList<>();
        
        for (Object[] result : deptResults) {
            if (result == null || result.length < 2) continue;
            
            String label = result[0] != null ? result[0].toString() : "Unknown";
            Double value = result[1] != null ? ((Number) result[1]).doubleValue() : 0.0;
            double target = value * 1.1; // Target is 10% higher than current
            
            benchmarks.add(BenchmarkPointDTO.builder()
                    .label(label)
                    .value(value)
                    .target(target)
                    .build());
        }
        
        return benchmarks;
    }
}
