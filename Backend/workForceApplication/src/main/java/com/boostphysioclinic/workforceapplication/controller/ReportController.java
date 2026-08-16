package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.dto.entity.Report;
import com.boostphysioclinic.workforceapplication.dto.ReportDTO;
import com.boostphysioclinic.workforceapplication.service.ReportService;
import com.boostphysioclinic.workforceapplication.service.ReportsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final ReportsService reportsService;

    @GetMapping
    public List<ReportDTO> getAllReports() {
        return reportsService.getReports();
    }

    @GetMapping("/type/{type}")
    public List<Report> getReportsByType(@PathVariable String type) {
        return reportService.getReportsByType(type);
    }

    @GetMapping("/status/{status}")
    public List<Report> getReportsByStatus(@PathVariable String status) {
        return reportService.getReportsByStatus(status);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Report> getReportById(@PathVariable Long id) {
        return reportService.getReportById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadReport(@PathVariable Long id) {
        System.err.println("Download request for report ID: " + id);
        ReportDTO report = reportsService.getReports().stream()
                .filter(r -> r.getId().equals(id.intValue()))
                .findFirst()
                .orElse(null);
        
        System.err.println("Report found: " + (report != null));
        if (report != null) {
            System.err.println("Report name: " + report.getName());
            System.err.println("File data null: " + (report.getFileData() == null));
            if (report.getFileData() != null) {
                System.err.println("File data length: " + report.getFileData().length);
            }
        }
        
        if (report != null && report.getFileData() != null && report.getFileData().length > 0) {
            String filename = report.getName() != null ? report.getName().replaceAll("[^a-zA-Z0-9\\s_-]", "") : "report";
            System.err.println("Returning PDF with size: " + report.getFileData().length + " bytes");
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + ".pdf\"")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(report.getFileData().length))
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                    .header(HttpHeaders.PRAGMA, "no-cache")
                    .header(HttpHeaders.EXPIRES, "0")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(report.getFileData());
        }
        System.err.println("Failed to return PDF - report null or file data empty");
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public Report createReport(@RequestBody Report report) {
        return reportService.createReport(report);
    }

    @PostMapping("/generate")
    public ReportDTO generateReport(@RequestParam String type) {
        return reportsService.generateReport(type);
    }

    @PutMapping("/{id}")
    public Report updateReport(@PathVariable Long id, @RequestBody Report report) {
        return reportService.updateReport(id, report);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}
