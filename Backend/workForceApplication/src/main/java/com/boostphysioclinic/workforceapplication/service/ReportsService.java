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
    private final PdfGenerationService pdfGenerationService;
    private final PdfTemplateService pdfTemplateService;
    private final AtomicInteger reportIdCounter = new AtomicInteger(1);

    private List<ReportDTO> reports = new ArrayList<>();

    public List<ReportDTO> getReports() {
        // Initialize with sample reports if empty
        if (reports.isEmpty()) {
            // Temporarily disabled to prevent OutOfMemoryError during startup
            // initializeSampleReports();
        }
        return reports;
    }

    public ReportDTO generateReport(String type) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String reportId = String.format("WF-%s-%d", type.substring(0, 2).toUpperCase(), reportIdCounter.getAndIncrement());
        
        byte[] pdfData;
        try {
            switch (type.toLowerCase()) {
                case "analytics":
                    // Use new template-based service for analytics
                    pdfData = pdfTemplateService.generateAnalyticsPdf(reportId);
                    break;
                case "performance":
                    pdfData = pdfGenerationService.generatePerformancePdf(reportId);
                    break;
                case "attendance":
                    pdfData = pdfGenerationService.generateAttendancePdf(reportId);
                    break;
                case "optimization":
                    pdfData = pdfGenerationService.generateOptimizationPdf(reportId);
                    break;
                case "forecast":
                    pdfData = pdfGenerationService.generateForecastPdf(reportId);
                    break;
                default:
                    pdfData = pdfTemplateService.generateAnalyticsPdf(reportId);
            }
            System.err.println("PDF generated successfully. Size: " + pdfData.length + " bytes for report type: " + type);
            System.err.println("PDF data null check: " + (pdfData == null));
            if (pdfData != null) {
                System.err.println("PDF data first 10 bytes: " + java.util.Arrays.copyOf(pdfData, Math.min(10, pdfData.length)));
            }
        } catch (Exception e) {
            System.err.println("Error generating PDF: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to generate PDF report", e);
        }
        
        ReportDTO report = ReportDTO.builder()
                .id(reportIdCounter.get())
                .name(type + " Report")
                .type(type)
                .description("Generated " + type + " report")
                .generatedAt(now.format(formatter))
                .fileSize((long) pdfData.length)
                .filePath("/reports/" + type.toLowerCase() + "_" + System.currentTimeMillis() + ".pdf")
                .status("Ready")
                .generatedBy("System")
                .fileData(pdfData)
                .build();
        
        System.err.println("Report created with fileData length: " + report.getFileData().length);
        reports.add(0, report); // Add to beginning of list
        System.err.println("Report added to list. Total reports: " + reports.size());
        return report;
    }

    private void initializeSampleReports() {
        // Temporarily disabled to prevent OutOfMemoryError during startup
        // PDF generation is causing memory issues with table rendering
        // Users can generate reports on-demand instead
        System.err.println("Sample reports initialization disabled to prevent OutOfMemoryError");
    }
}
