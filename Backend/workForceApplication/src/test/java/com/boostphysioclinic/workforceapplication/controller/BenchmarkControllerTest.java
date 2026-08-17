package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.dto.BenchmarkMetricDTO;
import com.boostphysioclinic.workforceapplication.dto.LatencyPointDTO;
import com.boostphysioclinic.workforceapplication.dto.VersionHistoryDTO;
import com.boostphysioclinic.workforceapplication.dto.ExperimentDTO;
import com.boostphysioclinic.workforceapplication.service.BenchmarkService;
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
class BenchmarkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BenchmarkService benchmarkService;

    private List<BenchmarkMetricDTO> mockMetrics;
    private List<LatencyPointDTO> mockLatency;
    private List<VersionHistoryDTO> mockVersions;
    private List<ExperimentDTO> mockExperiments;

    @BeforeEach
    void setUp() {
        mockMetrics = createMockMetrics();
        mockLatency = createMockLatency();
        mockVersions = createMockVersions();
        mockExperiments = createMockExperiments();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetMetrics() throws Exception {
        when(benchmarkService.getMetrics()).thenReturn(mockMetrics);

        mockMvc.perform(get("/api/benchmark/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(mockMetrics.size()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetMetricsEmpty() throws Exception {
        when(benchmarkService.getMetrics()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/benchmark/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetLatencySeries() throws Exception {
        when(benchmarkService.getLatencySeries()).thenReturn(mockLatency);

        mockMvc.perform(get("/api/benchmark/latency"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(mockLatency.size()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetVersionHistory() throws Exception {
        when(benchmarkService.getVersionHistory()).thenReturn(mockVersions);

        mockMvc.perform(get("/api/benchmark/versions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(mockVersions.size()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetExperiments() throws Exception {
        when(benchmarkService.getExperiments()).thenReturn(mockExperiments);

        mockMvc.perform(get("/api/benchmark/experiments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(mockExperiments.size()));
    }

    private List<BenchmarkMetricDTO> createMockMetrics() {
        List<BenchmarkMetricDTO> metrics = new ArrayList<>();
        BenchmarkMetricDTO metric1 = new BenchmarkMetricDTO();
        metric1.setName("Response Time");
        metric1.setValue(150.0);
        metric1.setUnit("ms");
        metric1.setTrend("+5%");
        metrics.add(metric1);

        BenchmarkMetricDTO metric2 = new BenchmarkMetricDTO();
        metric2.setName("Throughput");
        metric2.setValue(1000.0);
        metric2.setUnit("req/s");
        metric2.setTrend("+10%");
        metrics.add(metric2);

        return metrics;
    }

    private List<LatencyPointDTO> createMockLatency() {
        List<LatencyPointDTO> points = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            LatencyPointDTO point = new LatencyPointDTO();
            point.setTimestamp("2024-01-" + String.format("%02d", i + 1));
            point.setValue(100.0 + i * 5);
            points.add(point);
        }
        return points;
    }

    private List<VersionHistoryDTO> createMockVersions() {
        List<VersionHistoryDTO> versions = new ArrayList<>();
        VersionHistoryDTO version1 = new VersionHistoryDTO();
        version1.setVersion("1.0.0");
        version1.setDate("2024-01-01");
        version1.setScore(85.0);
        versions.add(version1);

        VersionHistoryDTO version2 = new VersionHistoryDTO();
        version2.setVersion("1.1.0");
        version2.setDate("2024-02-01");
        version2.setScore(90.0);
        versions.add(version2);

        return versions;
    }

    private List<ExperimentDTO> createMockExperiments() {
        List<ExperimentDTO> experiments = new ArrayList<>();
        ExperimentDTO exp1 = new ExperimentDTO();
        exp1.setId("EXP001");
        exp1.setName("Experiment A");
        exp1.setStatus("Completed");
        exp1.setStartDate("2024-01-01");
        exp1.setEndDate("2024-01-15");
        experiments.add(exp1);

        ExperimentDTO exp2 = new ExperimentDTO();
        exp2.setId("EXP002");
        exp2.setName("Experiment B");
        exp2.setStatus("Running");
        exp2.setStartDate("2024-01-16");
        exp2.setEndDate("2024-01-30");
        experiments.add(exp2);

        return experiments;
    }
}
