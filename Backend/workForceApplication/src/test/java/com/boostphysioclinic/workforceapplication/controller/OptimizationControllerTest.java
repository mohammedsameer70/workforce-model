package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.config.TestSecurityConfig;
import com.boostphysioclinic.workforceapplication.dto.*;
import com.boostphysioclinic.workforceapplication.service.OptimizationService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class OptimizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OptimizationService optimizationService;

    private OptimizationResultDTO testResult;
    private List<StaffAllocationDTO> testAllocations;
    private List<ShiftAssignmentDTO> testAssignments;
    private List<EmployeeReassignmentDTO> testReassignments;
    private List<OptimizationWarningDTO> testWarnings;
    private List<OptimizationRecommendationDTO> testRecommendations;

    @BeforeEach
    void setUp() {
        testResult = createMockOptimizationResult();
        testAllocations = createMockAllocations();
        testAssignments = createMockAssignments();
        testReassignments = createMockReassignments();
        testWarnings = createMockWarnings();
        testRecommendations = createMockRecommendations();
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testRunOptimization() throws Exception {
        when(optimizationService.runOptimization()).thenReturn(testResult);

        mockMvc.perform(post("/api/optimization/run"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.optimizationScore").value(85));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetOptimizationResult() throws Exception {
        when(optimizationService.runOptimization()).thenReturn(testResult);

        mockMvc.perform(get("/api/optimization/result"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.optimizationScore").value(85));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetStaffAllocations() throws Exception {
        when(optimizationService.getStaffAllocations()).thenReturn(testAllocations);

        mockMvc.perform(get("/api/optimization/staff-allocations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(testAllocations.size()));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetShiftAssignments() throws Exception {
        when(optimizationService.getShiftAssignments()).thenReturn(testAssignments);

        mockMvc.perform(get("/api/optimization/shift-assignments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(testAssignments.size()));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetReassignments() throws Exception {
        when(optimizationService.getReassignments()).thenReturn(testReassignments);

        mockMvc.perform(get("/api/optimization/reassignments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(testReassignments.size()));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetWarnings() throws Exception {
        when(optimizationService.getWarnings()).thenReturn(testWarnings);

        mockMvc.perform(get("/api/optimization/warnings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(testWarnings.size()));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetRecommendations() throws Exception {
        when(optimizationService.getRecommendations()).thenReturn(testRecommendations);

        mockMvc.perform(get("/api/optimization/recommendations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(testRecommendations.size()));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testApplyRecommendation() throws Exception {
        OptimizationRecommendationDTO recommendation = testRecommendations.get(0);

        mockMvc.perform(post("/api/optimization/apply-recommendation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(recommendation)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testCreateAllocation() throws Exception {
        StaffAllocationDTO allocation = testAllocations.get(0);
        when(optimizationService.createAllocation(any(StaffAllocationDTO.class))).thenReturn(allocation);

        mockMvc.perform(post("/api/optimization/create-allocation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(allocation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.department").value("IT"));
    }

    private OptimizationResultDTO createMockOptimizationResult() {
        return OptimizationResultDTO.builder()
                .optimizationScore(85)
                .employeesReassigned(10)
                .departmentsOptimized(5)
                .estimatedSavingsPercentage(15.0)
                .currentUtilization(75.0)
                .optimizedUtilization(90.0)
                .dailyCostBefore(5000.0)
                .dailyCostAfter(4250.0)
                .dailySavings(750.0)
                .build();
    }

    private List<StaffAllocationDTO> createMockAllocations() {
        List<StaffAllocationDTO> allocations = new ArrayList<>();
        StaffAllocationDTO allocation = new StaffAllocationDTO();
        allocation.setDepartment("IT");
        allocation.setPredictedDemand(100.0);
        allocation.setCurrentStaff(80);
        allocation.setRecommendedStaff(90);
        allocation.setSurplus(0);
        allocation.setShortage(10);
        allocations.add(allocation);
        return allocations;
    }

    private List<ShiftAssignmentDTO> createMockAssignments() {
        List<ShiftAssignmentDTO> assignments = new ArrayList<>();
        ShiftAssignmentDTO assignment = new ShiftAssignmentDTO();
        assignment.setShift("Morning");
        assignment.setRequired(10);
        assignment.setAssigned(9);
        assignment.setGap(1);
        assignments.add(assignment);
        return assignments;
    }

    private List<EmployeeReassignmentDTO> createMockReassignments() {
        List<EmployeeReassignmentDTO> reassignments = new ArrayList<>();
        EmployeeReassignmentDTO reassignment = new EmployeeReassignmentDTO();
        reassignment.setEmployeesToMove(2);
        reassignment.setFromDepartment("IT");
        reassignment.setToDepartment("HR");
        reassignment.setReason("Workload balancing");
        reassignments.add(reassignment);
        return reassignments;
    }

    private List<OptimizationWarningDTO> createMockWarnings() {
        List<OptimizationWarningDTO> warnings = new ArrayList<>();
        OptimizationWarningDTO warning = new OptimizationWarningDTO();
        warning.setType("Understaffing");
        warning.setDepartment("IT");
        warning.setMessage("IT department is understaffed");
        warning.setSeverity("High");
        warnings.add(warning);
        return warnings;
    }

    private List<OptimizationRecommendationDTO> createMockRecommendations() {
        List<OptimizationRecommendationDTO> recommendations = new ArrayList<>();
        OptimizationRecommendationDTO recommendation = new OptimizationRecommendationDTO();
        recommendation.setAction("Hire");
        recommendation.setDetails("Hire 2 more employees for IT department");
        recommendation.setPriority("High");
        recommendations.add(recommendation);
        return recommendations;
    }
}
