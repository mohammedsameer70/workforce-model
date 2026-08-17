package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.config.TestSecurityConfig;
import com.boostphysioclinic.workforceapplication.dto.entity.Report;
import com.boostphysioclinic.workforceapplication.dto.ReportDTO;
import com.boostphysioclinic.workforceapplication.service.ReportService;
import com.boostphysioclinic.workforceapplication.service.ReportsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReportService reportService;

    @MockBean
    private ReportsService reportsService;

    private Report testReport;
    private ReportDTO testReportDTO;

    @BeforeEach
    void setUp() {
        testReport = createMockReport();
        testReportDTO = createMockReportDTO();
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetAllReports() throws Exception {
        List<ReportDTO> reports = List.of(testReportDTO);
        when(reportsService.getReports()).thenReturn(reports);

        mockMvc.perform(get("/api/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetReportsByType() throws Exception {
        List<Report> reports = List.of(testReport);
        when(reportService.getReportsByType("PDF")).thenReturn(reports);

        mockMvc.perform(get("/api/reports/type/PDF"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetReportsByStatus() throws Exception {
        List<Report> reports = List.of(testReport);
        when(reportService.getReportsByStatus("Completed")).thenReturn(reports);

        mockMvc.perform(get("/api/reports/status/Completed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetReportByIdFound() throws Exception {
        when(reportService.getReportById(1L)).thenReturn(Optional.of(testReport));

        mockMvc.perform(get("/api/reports/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetReportByIdNotFound() throws Exception {
        when(reportService.getReportById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/reports/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testDownloadReportFound() throws Exception {
        List<ReportDTO> reports = List.of(testReportDTO);
        when(reportsService.getReports()).thenReturn(reports);

        mockMvc.perform(get("/api/reports/1/download"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(header().string("Content-Type", "application/pdf"));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testDownloadReportNotFound() throws Exception {
        when(reportsService.getReports()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/reports/999/download"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testCreateReport() throws Exception {
        when(reportService.createReport(any(Report.class))).thenReturn(testReport);

        mockMvc.perform(post("/api/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testReport)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGenerateReport() throws Exception {
        when(reportsService.generateReport("PDF")).thenReturn(testReportDTO);

        mockMvc.perform(post("/api/reports/generate")
                .param("type", "PDF"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testUpdateReport() throws Exception {
        when(reportService.updateReport(anyLong(), any(Report.class))).thenReturn(testReport);

        mockMvc.perform(put("/api/reports/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testReport)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testDeleteReport() throws Exception {
        mockMvc.perform(delete("/api/reports/1"))
                .andExpect(status().isNoContent());
    }

    private Report createMockReport() {
        Report report = new Report();
        report.setId(1L);
        report.setName("Monthly Report");
        report.setType("PDF");
        report.setStatus("Completed");
        report.setGeneratedAt(java.time.LocalDateTime.now());
        return report;
    }

    private ReportDTO createMockReportDTO() {
        ReportDTO dto = new ReportDTO();
        dto.setId(1);
        dto.setName("Monthly Report");
        dto.setType("PDF");
        dto.setStatus("Completed");
        dto.setGeneratedAt("2024-01-01T12:00:00");
        dto.setFileData(new byte[] { 1, 2, 3, 4, 5 }); // Mock PDF data
        return dto;
    }
}
