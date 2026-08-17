package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.dto.CapacityMetricDTO;
import com.boostphysioclinic.workforceapplication.dto.TimeSeriesDTO;
import com.boostphysioclinic.workforceapplication.dto.BenchmarkPointDTO;
import com.boostphysioclinic.workforceapplication.dto.DepartmentCapacityDTO;
import com.boostphysioclinic.workforceapplication.service.CapacityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CapacityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CapacityService capacityService;

    private List<CapacityMetricDTO> mockMetrics;
    private List<TimeSeriesDTO> mockTrend;
    private List<DepartmentCapacityDTO> mockDepartments;
    private List<BenchmarkPointDTO> mockBenchmark;

    @BeforeEach
    void setUp() {
        mockMetrics = createMockMetrics();
        mockTrend = createMockTrend();
        mockDepartments = createMockDepartments();
        mockBenchmark = createMockBenchmark();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetMetrics() throws Exception {
        when(capacityService.getMetrics()).thenReturn(mockMetrics);

        mockMvc.perform(get("/api/capacity/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(mockMetrics.size()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetCapacityTrend() throws Exception {
        when(capacityService.getCapacityTrend()).thenReturn(mockTrend);

        mockMvc.perform(get("/api/capacity/trend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(mockTrend.size()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetDepartments() throws Exception {
        when(capacityService.getDepartments()).thenReturn(mockDepartments);

        mockMvc.perform(get("/api/capacity/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(mockDepartments.size()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetBenchmark() throws Exception {
        when(capacityService.getBenchmark()).thenReturn(mockBenchmark);

        mockMvc.perform(get("/api/capacity/benchmark"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(mockBenchmark.size()));
    }

    private List<CapacityMetricDTO> createMockMetrics() {
        List<CapacityMetricDTO> metrics = new ArrayList<>();
        CapacityMetricDTO metric1 = new CapacityMetricDTO();
        metric1.setTitle("Total Capacity");
        metric1.setValue("500");
        metric1.setIcon("users");
        metrics.add(metric1);

        CapacityMetricDTO metric2 = new CapacityMetricDTO();
        metric2.setTitle("Utilization");
        metric2.setValue("85%");
        metric2.setIcon("chart");
        metrics.add(metric2);

        return metrics;
    }

    private List<TimeSeriesDTO> createMockTrend() {
        List<TimeSeriesDTO> trend = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            TimeSeriesDTO point = new TimeSeriesDTO();
            point.setLabel("2024-01-" + String.format("%02d", i + 1));
            point.setUtilization(400.0 + i * 10);
            point.setCapacity(500.0);
            trend.add(point);
        }
        return trend;
    }

    private List<DepartmentCapacityDTO> createMockDepartments() {
        List<DepartmentCapacityDTO> departments = new ArrayList<>();
        DepartmentCapacityDTO dept1 = new DepartmentCapacityDTO();
        dept1.setName("IT");
        dept1.setUtilization(90.0);
        dept1.setStatus("Good");
        departments.add(dept1);

        DepartmentCapacityDTO dept2 = new DepartmentCapacityDTO();
        dept2.setName("HR");
        dept2.setUtilization(80.0);
        dept2.setStatus("Good");
        departments.add(dept2);

        return departments;
    }

    private List<BenchmarkPointDTO> createMockBenchmark() {
        List<BenchmarkPointDTO> benchmark = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            BenchmarkPointDTO point = new BenchmarkPointDTO();
            point.setLabel("Q" + (i + 1));
            point.setValue(80.0 + i * 5);
            point.setTarget(85.0 + i * 5);
            benchmark.add(point);
        }
        return benchmark;
    }
}
