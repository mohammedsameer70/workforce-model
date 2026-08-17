package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.dto.AnalyticsMetricDTO;
import com.boostphysioclinic.workforceapplication.dto.TimeSeriesPointDTO;
import com.boostphysioclinic.workforceapplication.dto.DepartmentDistributionDTO;
import com.boostphysioclinic.workforceapplication.service.AnalyticsService;
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
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalyticsService analyticsService;

    private List<AnalyticsMetricDTO> mockMetrics;
    private List<TimeSeriesPointDTO> mockTimeSeries;
    private List<DepartmentDistributionDTO> mockDistribution;

    @BeforeEach
    void setUp() {
        mockMetrics = createMockMetrics();
        mockTimeSeries = createMockTimeSeries();
        mockDistribution = createMockDistribution();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetMetrics() throws Exception {
        when(analyticsService.getMetrics()).thenReturn(mockMetrics);

        mockMvc.perform(get("/api/analytics/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(mockMetrics.size()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetMetricsEmpty() throws Exception {
        when(analyticsService.getMetrics()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/analytics/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetHourlyThroughput() throws Exception {
        when(analyticsService.getHourlyThroughput()).thenReturn(mockTimeSeries);

        mockMvc.perform(get("/api/analytics/hourly-throughput"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(mockTimeSeries.size()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetDemandForecast() throws Exception {
        when(analyticsService.getDemandForecast()).thenReturn(mockTimeSeries);

        mockMvc.perform(get("/api/analytics/demand-forecast"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(mockTimeSeries.size()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetDepartmentDistribution() throws Exception {
        when(analyticsService.getDepartmentDistribution()).thenReturn(mockDistribution);

        mockMvc.perform(get("/api/analytics/department-distribution"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(mockDistribution.size()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetWeeklyComparison() throws Exception {
        when(analyticsService.getWeeklyComparison()).thenReturn(mockTimeSeries);

        mockMvc.perform(get("/api/analytics/weekly-comparison"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(mockTimeSeries.size()));
    }

    private List<AnalyticsMetricDTO> createMockMetrics() {
        List<AnalyticsMetricDTO> metrics = new ArrayList<>();
        AnalyticsMetricDTO metric1 = new AnalyticsMetricDTO();
        metric1.setName("Total Employees");
        metric1.setValue("150");
        metric1.setChange("+5%");
        metrics.add(metric1);

        AnalyticsMetricDTO metric2 = new AnalyticsMetricDTO();
        metric2.setName("Attendance Rate");
        metric2.setValue("92%");
        metric2.setChange("+2%");
        metrics.add(metric2);

        return metrics;
    }

    private List<TimeSeriesPointDTO> createMockTimeSeries() {
        List<TimeSeriesPointDTO> points = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            TimeSeriesPointDTO point = new TimeSeriesPointDTO();
            point.setLabel("2024-01-" + String.format("%02d", i + 1));
            point.setValue(100.0 + i * 10);
            points.add(point);
        }
        return points;
    }

    private List<DepartmentDistributionDTO> createMockDistribution() {
        List<DepartmentDistributionDTO> distribution = new ArrayList<>();
        DepartmentDistributionDTO dept1 = new DepartmentDistributionDTO();
        dept1.setDepartment("IT");
        dept1.setValue(50.0);

        DepartmentDistributionDTO dept2 = new DepartmentDistributionDTO();
        dept2.setDepartment("HR");
        dept2.setValue(30.0);

        return distribution;
    }
}
