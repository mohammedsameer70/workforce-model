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
        
        // Generate mock report content (text format)
        byte[] mockReportData = generateMockReportContent(type);
        
        ReportDTO report = ReportDTO.builder()
                .id(reportIdCounter.getAndIncrement())
                .name(type + " Report")
                .type(type)
                .description("Generated " + type + " report")
                .generatedAt(now.format(formatter))
                .fileSize((long) mockReportData.length)
                .filePath("/reports/" + type.toLowerCase() + "_" + System.currentTimeMillis() + ".txt")
                .status("Ready")
                .generatedBy("System")
                .fileData(mockReportData)
                .build();
        
        reports.add(0, report); // Add to beginning of list
        return report;
    }

    private byte[] generateMockReportContent(String type) {
        String content;
        
        switch (type.toLowerCase()) {
            case "analytics":
                content = generateAnalyticsReport();
                break;
            case "performance":
                content = generatePerformanceReport();
                break;
            case "attendance":
                content = generateAttendanceReport();
                break;
            case "optimization":
                content = generateOptimizationReport();
                break;
            case "forecast":
                content = generateForecastReport();
                break;
            default:
                content = generateGenericReport(type);
        }
        
        return content.getBytes();
    }

    private String generateAnalyticsReport() {
        return String.format(
            "========================================\n" +
            "       WORKFORCE ANALYTICS REPORT\n" +
            "========================================\n\n" +
            "Report ID: WF-AN-%d\n" +
            "Generated: %s\n" +
            "Period: Monthly Analysis\n\n" +
            "EXECUTIVE SUMMARY\n" +
            "----------------\n" +
            "This report provides comprehensive analytics on workforce utilization,\n" +
            "productivity metrics, and operational efficiency across all departments.\n\n" +
            "KEY METRICS\n" +
            "-----------\n" +
            "Total Employees: 247\n" +
            "Active Staff: 231 (93.5%%)\n" +
            "Average Utilization: 87.2%%\n" +
            "Productivity Index: 94.8/100\n" +
            "Overtime Hours: 1,245 (5.2%% increase)\n" +
            "Absenteeism Rate: 3.8%% (0.5%% decrease)\n\n" +
            "DEPARTMENT BREAKDOWN\n" +
            "--------------------\n" +
            "Clinical Staff: 156 employees\n" +
            "  - Utilization: 91.2%%\n" +
            "  - Patient Ratio: 1:8\n" +
            "Administrative: 45 employees\n" +
            "  - Utilization: 82.4%%\n" +
            "  - Efficiency: 89.1%%\n" +
            "Support Services: 46 employees\n" +
            "  - Utilization: 88.7%%\n" +
            "  - Response Time: 12.3 min avg\n\n" +
            "SHIFT ANALYSIS\n" +
            "-------------\n" +
            "Morning Shift (06:00-14:00): 78 staff\n" +
            "  - Peak Utilization: 94.5%%\n" +
            "Afternoon Shift (14:00-22:00): 82 staff\n" +
            "  - Peak Utilization: 89.2%%\n" +
            "Night Shift (22:00-06:00): 71 staff\n" +
            "  - Peak Utilization: 76.8%%\n\n" +
            "RECOMMENDATIONS\n" +
            "--------------\n" +
            "1. Increase night shift staffing by 8-10 staff to improve coverage\n" +
            "2. Implement cross-training program for clinical staff\n" +
            "3. Review overtime patterns in administrative department\n" +
            "4. Consider flexible scheduling for support services\n\n" +
            "========================================\n" +
            "           END OF REPORT\n" +
            "========================================\n",
            reportIdCounter.get() + 1,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    private String generatePerformanceReport() {
        return String.format(
            "========================================\n" +
            "      DEPARTMENT PERFORMANCE REPORT\n" +
            "========================================\n\n" +
            "Report ID: WF-PF-%d\n" +
            "Generated: %s\n" +
            "Period: Q4 Performance Review\n\n" +
            "EXECUTIVE SUMMARY\n" +
            "----------------\n" +
            "Quarterly performance analysis showing departmental efficiency,\n" +
            "goal achievement, and operational benchmarks.\n\n" +
            "OVERALL PERFORMANCE\n" +
            "------------------\n" +
            "Organization Score: 87.3/100\n" +
            "Goal Achievement: 92.1%%\n" +
            "Patient Satisfaction: 94.5%%\n" +
            "Staff Satisfaction: 88.7%%\n\n" +
            "DEPARTMENT SCORES\n" +
            "-----------------\n" +
            "Physiotherapy Department: 91.2/100\n" +
            "  - Patient Outcomes: 94.5%%\n" +
            "  - Treatment Efficiency: 89.3%%\n" +
            "  - Staff Performance: 89.8%%\n\n" +
            "Rehabilitation Services: 88.7/100\n" +
            "  - Recovery Rates: 87.2%%\n" +
            "  - Session Completion: 95.1%%\n" +
            "  - Staff Performance: 83.9%%\n\n" +
            "Administrative Operations: 82.4/100\n" +
            "  - Process Efficiency: 85.6%%\n" +
            "  - Response Time: 78.9%%\n" +
            "  - Staff Performance: 82.7%%\n\n" +
            "INDIVIDUAL PERFORMANCE\n" +
            "---------------------\n" +
            "Top Performers: 23 staff members\n" +
            "Exceeding Expectations: 67 staff members\n" +
            "Meeting Expectations: 142 staff members\n" +
            "Needs Improvement: 15 staff members\n\n" +
            "TRAINING NEEDS\n" +
            "-------------\n" +
            "Clinical Skills Enhancement: 18 staff\n" +
            "Leadership Development: 12 staff\n" +
            "Technical Training: 24 staff\n\n" +
            "========================================\n" +
            "           END OF REPORT\n" +
            "========================================\n",
            reportIdCounter.get() + 1,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    private String generateAttendanceReport() {
        return String.format(
            "========================================\n" +
            "      EMPLOYEE ATTENDANCE REPORT\n" +
            "========================================\n\n" +
            "Report ID: WF-AT-%d\n" +
            "Generated: %s\n" +
            "Period: Monthly Attendance Summary\n\n" +
            "EXECUTIVE SUMMARY\n" +
            "----------------\n" +
            "Comprehensive attendance analysis including leave patterns,\n" +
            "absenteeism trends, and compliance metrics.\n\n" +
            "ATTENDANCE OVERVIEW\n" +
            "-------------------\n" +
            "Total Working Days: 22\n" +
            "Total Staff: 247\n" +
            "Overall Attendance Rate: 96.2%%\n" +
            "Total Absenteeism: 3.8%%\n\n" +
            "LEAVE BREAKDOWN\n" +
            "---------------\n" +
            "Sick Leave: 127 days (5.1%%)\n" +
            "Annual Leave: 89 days (3.6%%)\n" +
            "Personal Leave: 34 days (1.4%%)\n" +
            "Unexcused Absences: 12 days (0.5%%)\n\n" +
            "DEPARTMENT ATTENDANCE\n" +
            "--------------------\n" +
            "Clinical Staff: 94.8%% attendance\n" +
            "  - Sick Leave: 4.2%%\n" +
            "  - Annual Leave: 0.8%%\n" +
            "Administrative: 97.1%% attendance\n" +
            "  - Sick Leave: 1.9%%\n" +
            "  - Annual Leave: 0.9%%\n" +
            "Support Services: 96.7%% attendance\n" +
            "  - Sick Leave: 2.3%%\n" +
            "  - Annual Leave: 0.9%%\n\n" +
            "PATTERNS & TRENDS\n" +
            "----------------\n" +
            "Monday Absences: Highest (4.2%%)\n" +
            "Friday Absences: Lowest (2.8%%)\n" +
            "Peak Sick Leave: Winter months\n" +
            "Peak Annual Leave: Summer months\n\n" +
            "COMPLIANCE STATUS\n" +
            "-----------------\n" +
            "Leave Policy Compliance: 98.2%%\n" +
            "Documentation Complete: 96.7%%\n" +
            "Approval Process: 99.1%%\n\n" +
            "========================================\n" +
            "           END OF REPORT\n" +
            "========================================\n",
            reportIdCounter.get() + 1,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    private String generateOptimizationReport() {
        return String.format(
            "========================================\n" +
            "      SHIFT OPTIMIZATION REPORT\n" +
            "========================================\n\n" +
            "Report ID: WF-OP-%d\n" +
            "Generated: %s\n" +
            "Period: Shift Allocation Analysis\n\n" +
            "EXECUTIVE SUMMARY\n" +
            "----------------\n" +
            "Analysis of current shift patterns, staffing efficiency,\n" +
            "and optimization recommendations for improved coverage.\n\n" +
            "CURRENT SHIFT CONFIGURATION\n" +
            "--------------------------\n" +
            "Morning Shift (06:00-14:00): 78 staff\n" +
            "  - Patient Demand: High\n" +
            "  - Staffing Adequacy: 94.5%%\n" +
            "  - Coverage Score: 91.2/100\n\n" +
            "Afternoon Shift (14:00-22:00): 82 staff\n" +
            "  - Patient Demand: Medium\n" +
            "  - Staffing Adequacy: 102.3%%\n" +
            "  - Coverage Score: 87.8/100\n\n" +
            "Night Shift (22:00-06:00): 71 staff\n" +
            "  - Patient Demand: Low-Medium\n" +
            "  - Staffing Adequacy: 76.8%%\n" +
            "  - Coverage Score: 72.4/100\n\n" +
            "OPTIMIZATION OPPORTUNITIES\n" +
            "------------------------\n" +
            "1. Night Shift Understaffing:\n" +
            "   - Current: 71 staff\n" +
            "   - Recommended: 85 staff\n" +
            "   - Improvement: +19.7%% coverage\n\n" +
            "2. Afternoon Shift Overstaffing:\n" +
            "   - Current: 82 staff\n" +
            "   - Recommended: 74 staff\n" +
            "   - Savings: 8 staff hours/day\n\n" +
            "3. Peak Hour Analysis:\n" +
            "   - Morning Peak (09:00-11:00): +12 staff needed\n" +
            "   - Afternoon Lull (15:00-17:00): -8 staff possible\n\n" +
            "RECOMMENDED CHANGES\n" +
            "------------------\n" +
            "• Redistribute 8 staff from afternoon to night shift\n" +
            "• Implement staggered start times for morning peak\n" +
            "• Consider part-time staff for peak coverage\n" +
            "• Review on-call protocols for night emergencies\n\n" +
            "PROJECTED IMPACT\n" +
            "----------------\n" +
            "Coverage Improvement: +15.3%%\n" +
            "Cost Savings: $12,450/month\n" +
            "Staff Satisfaction: +8.7%%\n" +
            "Patient Care Quality: +11.2%%\n\n" +
            "========================================\n" +
            "           END OF REPORT\n" +
            "========================================\n",
            reportIdCounter.get() + 1,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    private String generateForecastReport() {
        return String.format(
            "========================================\n" +
            "      FORECAST ACCURACY REPORT\n" +
            "========================================\n\n" +
            "Report ID: WF-FC-%d\n" +
            "Generated: %s\n" +
            "Period: Prediction Model Analysis\n\n" +
            "EXECUTIVE SUMMARY\n" +
            "----------------\n" +
            "Evaluation of AI forecasting model accuracy, prediction quality,\n" +
            "and recommendations for model improvement.\n\n" +
            "MODEL PERFORMANCE\n" +
            "-----------------\n" +
            "Model Type: Ensemble Learning\n" +
            "Training Data: 12 months historical\n" +
            "Prediction Horizon: 30 days\n\n" +
            "ACCURACY METRICS\n" +
            "---------------\n" +
            "Root Mean Square Error (RMSE): 4.23\n" +
            "Mean Absolute Error (MAE): 3.18\n" +
            "Mean Absolute Percentage Error (MAPE): 8.7%%\n" +
            "R-Squared (R²): 0.912\n" +
            "Overall Accuracy: 91.3%%\n\n" +
            "FORECAST CATEGORY PERFORMANCE\n" +
            "----------------------------\n" +
            "Patient Demand Forecast: 89.7%% accuracy\n" +
            "Staffing Needs: 92.4%% accuracy\n" +
            "Peak Hour Prediction: 87.2%% accuracy\n" +
            "Seasonal Trends: 94.1%% accuracy\n\n" +
            "RECENT PREDICTIONS VS ACTUAL\n" +
            "---------------------------\n" +
            "Week 1 Prediction: 245 patients | Actual: 238 | Error: 2.9%%\n" +
            "Week 2 Prediction: 251 patients | Actual: 263 | Error: -4.6%%\n" +
            "Week 3 Prediction: 248 patients | Actual: 245 | Error: 1.2%%\n" +
            "Week 4 Prediction: 253 patients | Actual: 249 | Error: 1.6%%\n\n" +
            "MODEL IMPROVEMENT RECOMMENDATIONS\n" +
            "--------------------------------\n" +
            "1. Include weather data as additional feature\n" +
            "2. Increase training data to 24 months\n" +
            "3. Implement real-time learning updates\n" +
            "4. Add holiday and event calendar data\n" +
            "5. Consider separate models for each department\n\n" +
            "VALIDATION RESULTS\n" +
            "-----------------\n" +
            "Cross-Validation Score: 0.896\n" +
            "Test Set Performance: 0.912\n" +
            "Overfitting Risk: Low\n" +
            "Model Stability: High\n\n" +
            "========================================\n" +
            "           END OF REPORT\n" +
            "========================================\n",
            reportIdCounter.get() + 1,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    private String generateGenericReport(String type) {
        return String.format(
            "========================================\n" +
            "           %s REPORT\n" +
            "========================================\n\n" +
            "Generated: %s\n" +
            "Report Type: %s\n\n" +
            "EXECUTIVE SUMMARY\n" +
            "----------------\n" +
            "This is a sample %s report generated by the Workforce Management System.\n" +
            "In a production environment, this would contain:\n\n" +
            "- Detailed analytics and metrics\n" +
            "- Charts and visualizations\n" +
            "- Performance indicators\n" +
            "- Recommendations and insights\n\n" +
            "DATA SUMMARY\n" +
            "------------\n" +
            "Total Records: %d\n" +
            "Processing Time: %.2f seconds\n" +
            "Status: Completed\n\n" +
            "========================================\n" +
            "           END OF REPORT\n" +
            "========================================\n",
            type.toUpperCase(),
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            type,
            type,
            reportIdCounter.get() + 1,
            Math.random() * 5 + 1
        );
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
                .filePath("/reports/monthly_workforce.txt")
                .status("Ready")
                .generatedBy("System")
                .fileData(generateMockReportContent("Analytics"))
                .build());
        
        reports.add(ReportDTO.builder()
                .id(reportIdCounter.getAndIncrement())
                .name("Department Performance Q4")
                .type("Performance")
                .description("Quarterly department performance metrics")
                .generatedAt(now.minusDays(3).format(formatter))
                .fileSize(180000L)
                .filePath("/reports/department_performance_q4.txt")
                .status("Ready")
                .generatedBy("System")
                .fileData(generateMockReportContent("Performance"))
                .build());
        
        reports.add(ReportDTO.builder()
                .id(reportIdCounter.getAndIncrement())
                .name("Employee Attendance Summary")
                .type("Attendance")
                .description("Employee attendance and leave summary")
                .generatedAt(now.minusDays(5).format(formatter))
                .fileSize(95000L)
                .filePath("/reports/attendance_summary.txt")
                .status("Ready")
                .generatedBy("System")
                .fileData(generateMockReportContent("Attendance"))
                .build());
        
        reports.add(ReportDTO.builder()
                .id(reportIdCounter.getAndIncrement())
                .name("Shift Optimization Report")
                .type("Optimization")
                .description("Shift allocation optimization analysis")
                .generatedAt(now.minusDays(7).format(formatter))
                .fileSize(320000L)
                .filePath("/reports/shift_optimization.txt")
                .status("Ready")
                .generatedBy("System")
                .fileData(generateMockReportContent("Optimization"))
                .build());
        
        reports.add(ReportDTO.builder()
                .id(reportIdCounter.getAndIncrement())
                .name("Forecast Accuracy Analysis")
                .type("Forecast")
                .description("Prediction model accuracy analysis")
                .generatedAt(now.minusDays(10).format(formatter))
                .fileSize(150000L)
                .filePath("/reports/forecast_accuracy.txt")
                .status("Ready")
                .generatedBy("System")
                .fileData(generateMockReportContent("Forecast"))
                .build());
    }
}
