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
        ReportDTO report = reportsService.getReports().stream()
                .filter(r -> r.getId().equals(id.intValue()))
                .findFirst()
                .orElse(null);
        
        if (report != null && report.getFileData() != null) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + report.getName() + ".txt\"")
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(report.getFileData());
        }
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
