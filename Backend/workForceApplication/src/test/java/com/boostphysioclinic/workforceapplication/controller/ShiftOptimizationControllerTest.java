package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.config.TestSecurityConfig;
import com.boostphysioclinic.workforceapplication.dto.entity.ShiftStaffing;
import com.boostphysioclinic.workforceapplication.dto.entity.AIRecommendation;
import com.boostphysioclinic.workforceapplication.dto.entity.ShiftCoverageMatrix;
import com.boostphysioclinic.workforceapplication.dto.ShiftMetricDTO;
import com.boostphysioclinic.workforceapplication.dto.ShiftRecommendationDTO;
import com.boostphysioclinic.workforceapplication.dto.ShiftCoverageDTO;
import com.boostphysioclinic.workforceapplication.dto.StaffingBarPointDTO;
import com.boostphysioclinic.workforceapplication.service.ShiftOptimizationService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class ShiftOptimizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShiftOptimizationService shiftOptimizationService;

    private List<ShiftMetricDTO> mockMetrics;
    private List<StaffingBarPointDTO> mockStaffing;
    private List<ShiftRecommendationDTO> mockRecommendations;
    private List<ShiftCoverageDTO> mockCoverage;
    private List<ShiftStaffing> mockShiftStaffing;
    private List<AIRecommendation> mockAIRecommendations;
    private List<ShiftCoverageMatrix> mockCoverageMatrices;

    @BeforeEach
    void setUp() {
        mockMetrics = createMockMetrics();
        mockStaffing = createMockStaffing();
        mockRecommendations = createMockRecommendations();
        mockCoverage = createMockCoverage();
        mockShiftStaffing = createMockShiftStaffing();
        mockAIRecommendations = createMockAIRecommendations();
        mockCoverageMatrices = createMockCoverageMatrices();
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetMetrics() throws Exception {
        when(shiftOptimizationService.getShiftMetricsDTO()).thenReturn(mockMetrics);

        mockMvc.perform(get("/api/shift-optimization/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(mockMetrics.size()));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetStaffingByDepartment() throws Exception {
        when(shiftOptimizationService.getStaffingByDepartmentDTO()).thenReturn(mockStaffing);

        mockMvc.perform(get("/api/shift-optimization/staffing-by-department"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(mockStaffing.size()));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetRecommendations() throws Exception {
        when(shiftOptimizationService.getShiftRecommendationsDTO()).thenReturn(mockRecommendations);

        mockMvc.perform(get("/api/shift-optimization/recommendations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(mockRecommendations.size()));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetCoverage() throws Exception {
        when(shiftOptimizationService.getShiftCoverageDTO()).thenReturn(mockCoverage);

        mockMvc.perform(get("/api/shift-optimization/coverage"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(mockCoverage.size()));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetAllShiftStaffing() throws Exception {
        when(shiftOptimizationService.getAllShiftStaffing()).thenReturn(mockShiftStaffing);

        mockMvc.perform(get("/api/shift-optimization/shift-staffing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(mockShiftStaffing.size()));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetShiftStaffingByDepartment() throws Exception {
        when(shiftOptimizationService.getShiftStaffingByDepartment("IT")).thenReturn(mockShiftStaffing);

        mockMvc.perform(get("/api/shift-optimization/shift-staffing/department/IT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(mockShiftStaffing.size()));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetShiftStaffingByShift() throws Exception {
        when(shiftOptimizationService.getShiftStaffingByShift("Morning")).thenReturn(mockShiftStaffing);

        mockMvc.perform(get("/api/shift-optimization/shift-staffing/shift/Morning"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(mockShiftStaffing.size()));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testCreateShiftStaffing() throws Exception {
        ShiftStaffing staffing = mockShiftStaffing.get(0);
        when(shiftOptimizationService.createShiftStaffing(any(ShiftStaffing.class))).thenReturn(staffing);

        mockMvc.perform(post("/api/shift-optimization/shift-staffing")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(staffing)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.department").value("IT"));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetAllAIRecommendations() throws Exception {
        when(shiftOptimizationService.getAllAIRecommendations()).thenReturn(mockAIRecommendations);

        mockMvc.perform(get("/api/shift-optimization/ai-recommendations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(mockAIRecommendations.size()));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetAIRecommendationsByStatus() throws Exception {
        when(shiftOptimizationService.getAIRecommendationsByStatus("Pending")).thenReturn(mockAIRecommendations);

        mockMvc.perform(get("/api/shift-optimization/ai-recommendations/status/Pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(mockAIRecommendations.size()));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetAIRecommendationsByDepartment() throws Exception {
        when(shiftOptimizationService.getAIRecommendationsByDepartment("IT")).thenReturn(mockAIRecommendations);

        mockMvc.perform(get("/api/shift-optimization/ai-recommendations/department/IT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(mockAIRecommendations.size()));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetAIRecommendationsByPriority() throws Exception {
        when(shiftOptimizationService.getAIRecommendationsByPriority("High")).thenReturn(mockAIRecommendations);

        mockMvc.perform(get("/api/shift-optimization/ai-recommendations/priority/High"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(mockAIRecommendations.size()));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testCreateAIRecommendation() throws Exception {
        AIRecommendation recommendation = mockAIRecommendations.get(0);
        when(shiftOptimizationService.createAIRecommendation(any(AIRecommendation.class))).thenReturn(recommendation);

        mockMvc.perform(post("/api/shift-optimization/ai-recommendations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(recommendation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Increase staffing"));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testApplyAIRecommendation() throws Exception {
        mockMvc.perform(patch("/api/shift-optimization/ai-recommendations/1/apply"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetAllShiftCoverageMatrices() throws Exception {
        when(shiftOptimizationService.getAllShiftCoverageMatrices()).thenReturn(mockCoverageMatrices);

        mockMvc.perform(get("/api/shift-optimization/shift-coverage"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(mockCoverageMatrices.size()));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetShiftCoverageMatricesByDepartment() throws Exception {
        when(shiftOptimizationService.getShiftCoverageMatricesByDepartment("IT")).thenReturn(mockCoverageMatrices);

        mockMvc.perform(get("/api/shift-optimization/shift-coverage/department/IT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(mockCoverageMatrices.size()));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testCreateShiftCoverageMatrix() throws Exception {
        ShiftCoverageMatrix matrix = mockCoverageMatrices.get(0);
        when(shiftOptimizationService.createShiftCoverageMatrix(any(ShiftCoverageMatrix.class))).thenReturn(matrix);

        mockMvc.perform(post("/api/shift-optimization/shift-coverage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(matrix)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.department").value("IT"));
    }

    private List<ShiftMetricDTO> createMockMetrics() {
        List<ShiftMetricDTO> metrics = new ArrayList<>();
        ShiftMetricDTO metric1 = new ShiftMetricDTO();
        metric1.setTitle("Total Shifts");
        metric1.setValue("50");
        metric1.setIcon("shift");
        metrics.add(metric1);

        ShiftMetricDTO metric2 = new ShiftMetricDTO();
        metric2.setTitle("Coverage Rate");
        metric2.setValue("92%");
        metric2.setIcon("coverage");
        metrics.add(metric2);

        return metrics;
    }

    private List<StaffingBarPointDTO> createMockStaffing() {
        List<StaffingBarPointDTO> staffing = new ArrayList<>();
        StaffingBarPointDTO point1 = new StaffingBarPointDTO();
        point1.setDepartment("IT");
        point1.setCurrent(80.0);
        point1.setOptimal(90.0);
        staffing.add(point1);

        StaffingBarPointDTO point2 = new StaffingBarPointDTO();
        point2.setDepartment("HR");
        point2.setCurrent(30.0);
        point2.setOptimal(35.0);
        staffing.add(point2);

        return staffing;
    }

    private List<ShiftRecommendationDTO> createMockRecommendations() {
        List<ShiftRecommendationDTO> recommendations = new ArrayList<>();
        ShiftRecommendationDTO rec1 = new ShiftRecommendationDTO();
        rec1.setId(1L);
        rec1.setTitle("Add 2 staff to IT Morning Shift");
        rec1.setPriority("High");
        rec1.setWorkers("2");
        rec1.setFrom("HR");
        rec1.setTo("IT");
        recommendations.add(rec1);

        return recommendations;
    }

    private List<ShiftCoverageDTO> createMockCoverage() {
        List<ShiftCoverageDTO> coverage = new ArrayList<>();
        ShiftCoverageDTO cov1 = new ShiftCoverageDTO();
        cov1.setDepartment("IT");
        cov1.setMorning("92%");
        cov1.setAfternoon("88%");
        cov1.setNight("85%");
        cov1.setStatus("Good");
        coverage.add(cov1);

        return coverage;
    }

    private List<ShiftStaffing> createMockShiftStaffing() {
        List<ShiftStaffing> staffing = new ArrayList<>();
        ShiftStaffing staff1 = new ShiftStaffing();
        staff1.setId(1L);
        staff1.setDepartment("IT");
        staff1.setShift("Morning");
        staff1.setRequiredStaff(10);
        staff1.setCurrentStaff(9);
        staffing.add(staff1);

        return staffing;
    }

    private List<AIRecommendation> createMockAIRecommendations() {
        List<AIRecommendation> recommendations = new ArrayList<>();
        AIRecommendation rec1 = new AIRecommendation();
        rec1.setId(1L);
        rec1.setDepartment("IT");
        rec1.setShift("Morning");
        rec1.setDescription("Increase staffing");
        rec1.setPriority("High");
        rec1.setStatus("Pending");
        recommendations.add(rec1);

        return recommendations;
    }

    private List<ShiftCoverageMatrix> createMockCoverageMatrices() {
        List<ShiftCoverageMatrix> matrices = new ArrayList<>();
        ShiftCoverageMatrix matrix1 = new ShiftCoverageMatrix();
        matrix1.setId(1L);
        matrix1.setDepartment("IT");
        matrix1.setDayOfWeek("Monday");
        matrix1.setShift("Morning");
        matrix1.setCoveragePercentage(92.0);
        matrices.add(matrix1);

        return matrices;
    }
}
