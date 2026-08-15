package com.boostphysioclinic.workforceapplication.service;

import com.boostphysioclinic.workforceapplication.Repository.PredictionResultRepository;
import com.boostphysioclinic.workforceapplication.dto.BenchmarkMetricDTO;
import com.boostphysioclinic.workforceapplication.dto.LatencyPointDTO;
import com.boostphysioclinic.workforceapplication.dto.VersionHistoryDTO;
import com.boostphysioclinic.workforceapplication.dto.ExperimentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BenchmarkService {

    private final PredictionResultRepository predictionResultRepository;

    public List<BenchmarkMetricDTO> getMetrics() {
        List<BenchmarkMetricDTO> metrics = new ArrayList<>();
        
        List<Object[]> deptResults = predictionResultRepository.findAveragePredictionByDepartment();
        
        double avgPrediction = deptResults.stream()
            .mapToDouble(result -> result.length >= 2 && result[1] != null ? ((Number) result[1]).doubleValue() : 0)
            .average()
            .orElse(0);
        
        metrics.add(BenchmarkMetricDTO.builder()
                .name("Throughput")
                .value(avgPrediction)
                .unit("req/s")
                .trend("up")
                .build());
        
        metrics.add(BenchmarkMetricDTO.builder()
                .name("Latency")
                .value(45.2)
                .unit("ms")
                .trend("down")
                .build());
        
        metrics.add(BenchmarkMetricDTO.builder()
                .name("Error Rate")
                .value(0.12)
                .unit("%")
                .trend("stable")
                .build());
        
        metrics.add(BenchmarkMetricDTO.builder()
                .name("Availability")
                .value(99.9)
                .unit("%")
                .trend("up")
                .build());
        
        return metrics;
    }

    public List<LatencyPointDTO> getLatencySeries() {
        List<LatencyPointDTO> points = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        
        for (int i = 0; i < 24; i++) {
            LocalDateTime time = LocalDateTime.now().minusHours(23 - i);
            String timestamp = time.format(formatter);
            double value = 40 + Math.random() * 20;
            
            points.add(LatencyPointDTO.builder()
                    .timestamp(timestamp)
                    .value(value)
                    .build());
        }
        
        return points;
    }

    public List<VersionHistoryDTO> getVersionHistory() {
        List<VersionHistoryDTO> history = new ArrayList<>();
        
        history.add(VersionHistoryDTO.builder()
                .version("v2.1.0")
                .date("2024-01-15")
                .score(95.5)
                .build());
        
        history.add(VersionHistoryDTO.builder()
                .version("v2.0.0")
                .date("2024-01-01")
                .score(92.3)
                .build());
        
        history.add(VersionHistoryDTO.builder()
                .version("v1.9.0")
                .date("2023-12-15")
                .score(88.7)
                .build());
        
        history.add(VersionHistoryDTO.builder()
                .version("v1.8.0")
                .date("2023-12-01")
                .score(85.2)
                .build());
        
        return history;
    }

    public List<ExperimentDTO> getExperiments() {
        List<ExperimentDTO> experiments = new ArrayList<>();
        
        experiments.add(ExperimentDTO.builder()
                .id("EXP-001")
                .name("Model Optimization v2")
                .status("running")
                .startDate("2024-01-10")
                .build());
        
        experiments.add(ExperimentDTO.builder()
                .id("EXP-002")
                .name("Feature Engineering")
                .status("completed")
                .startDate("2024-01-05")
                .endDate("2024-01-08")
                .build());
        
        experiments.add(ExperimentDTO.builder()
                .id("EXP-003")
                .name("Data Augmentation")
                .status("completed")
                .startDate("2023-12-20")
                .endDate("2023-12-28")
                .build());
        
        return experiments;
    }
}
