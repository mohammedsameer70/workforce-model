package com.boostphysioclinic.workforceapplication.service;

import com.boostphysioclinic.workforceapplication.Repository.PredictionResultRepository;
import com.boostphysioclinic.workforceapplication.dto.ReportDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class ReportsService {

    private final PredictionResultRepository predictionResultRepository;
    private final AtomicInteger reportIdCounter = new AtomicInteger(1);

    private List<ReportDTO> reports = new ArrayList<>();

    public List<ReportDTO> getReports() {
        // Initialize with sample reports if empty
        if (reports.isEmpty()) {
            initializeSampleReports();
        }
        return reports;
    }

    public ReportDTO generateReport(String type) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        
        ReportDTO report = ReportDTO.builder()
                .id(reportIdCounter.getAndIncrement())
                .name(type + " Report")
                .type(type)
                .description("Generated " + type + " report")
                .generatedAt(now.format(formatter))
                .fileSize((long) (100 + Math.random() * 500))
                .filePath("/reports/" + type.toLowerCase() + "_" + System.currentTimeMillis() + ".pdf")
                .status("Ready")
                .generatedBy("System")
                .build();
        
        reports.add(0, report); // Add to beginning of list
        return report;
    }

    private void initializeSampleReports() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        
        reports.add(ReportDTO.builder()
                .id(reportIdCounter.getAndIncrement())
                .name("Monthly Workforce Analysis")
                .type("Analytics")
                .description("Comprehensive monthly workforce analysis report")
                .generatedAt(now.minusDays(1).format(formatter))
                .fileSize(245000L)
                .filePath("/reports/monthly_workforce.pdf")
                .status("Ready")
                .generatedBy("System")
                .build());
        
        reports.add(ReportDTO.builder()
                .id(reportIdCounter.getAndIncrement())
                .name("Department Performance Q4")
                .type("Performance")
                .description("Quarterly department performance metrics")
                .generatedAt(now.minusDays(3).format(formatter))
                .fileSize(180000L)
                .filePath("/reports/department_performance_q4.pdf")
                .status("Ready")
                .generatedBy("System")
                .build());
        
        reports.add(ReportDTO.builder()
                .id(reportIdCounter.getAndIncrement())
                .name("Employee Attendance Summary")
                .type("Attendance")
                .description("Employee attendance and leave summary")
                .generatedAt(now.minusDays(5).format(formatter))
                .fileSize(95000L)
                .filePath("/reports/attendance_summary.pdf")
                .status("Ready")
                .generatedBy("System")
                .build());
        
        reports.add(ReportDTO.builder()
                .id(reportIdCounter.getAndIncrement())
                .name("Shift Optimization Report")
                .type("Optimization")
                .description("Shift allocation optimization analysis")
                .generatedAt(now.minusDays(7).format(formatter))
                .fileSize(320000L)
                .filePath("/reports/shift_optimization.pdf")
                .status("Ready")
                .generatedBy("System")
                .build());
        
        reports.add(ReportDTO.builder()
                .id(reportIdCounter.getAndIncrement())
                .name("Forecast Accuracy Analysis")
                .type("Forecast")
                .description("Prediction model accuracy analysis")
                .generatedAt(now.minusDays(10).format(formatter))
                .fileSize(150000L)
                .filePath("/reports/forecast_accuracy.pdf")
                .status("Ready")
                .generatedBy("System")
                .build());
    }
}
