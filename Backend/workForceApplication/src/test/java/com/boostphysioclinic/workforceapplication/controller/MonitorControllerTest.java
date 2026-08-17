package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.config.TestSecurityConfig;
import com.boostphysioclinic.workforceapplication.dto.entity.ServiceHealth;
import com.boostphysioclinic.workforceapplication.dto.entity.MonitoringMetric;
import com.boostphysioclinic.workforceapplication.service.MonitorService;
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
class MonitorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MonitorService monitorService;

    private ServiceHealth testServiceHealth;
    private MonitoringMetric testMonitoringMetric;

    @BeforeEach
    void setUp() {
        testServiceHealth = createMockServiceHealth();
        testMonitoringMetric = createMockMonitoringMetric();
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetAllServiceHealth() throws Exception {
        List<ServiceHealth> healthList = List.of(testServiceHealth);
        when(monitorService.getAllServiceHealth()).thenReturn(healthList);

        mockMvc.perform(get("/api/monitor/service-health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetServiceHealthByServiceNameFound() throws Exception {
        when(monitorService.getServiceHealthByServiceName("API")).thenReturn(Optional.of(testServiceHealth));

        mockMvc.perform(get("/api/monitor/service-health/service/API"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceName").value("API"));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetServiceHealthByServiceNameNotFound() throws Exception {
        when(monitorService.getServiceHealthByServiceName("NotFound")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/monitor/service-health/service/NotFound"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testCreateServiceHealth() throws Exception {
        when(monitorService.createServiceHealth(any(ServiceHealth.class))).thenReturn(testServiceHealth);

        mockMvc.perform(post("/api/monitor/service-health")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testServiceHealth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceName").value("API"));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testUpdateServiceHealth() throws Exception {
        when(monitorService.updateServiceHealth(anyLong(), any(ServiceHealth.class))).thenReturn(testServiceHealth);

        mockMvc.perform(put("/api/monitor/service-health/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testServiceHealth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetAllMonitoringMetrics() throws Exception {
        List<MonitoringMetric> metrics = List.of(testMonitoringMetric);
        when(monitorService.getAllMonitoringMetrics()).thenReturn(metrics);

        mockMvc.perform(get("/api/monitor/monitoring-metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetMonitoringMetricsByServiceName() throws Exception {
        List<MonitoringMetric> metrics = List.of(testMonitoringMetric);
        when(monitorService.getMonitoringMetricsByServiceName("API")).thenReturn(metrics);

        mockMvc.perform(get("/api/monitor/monitoring-metrics/service/API"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetMonitoringMetricsByCategory() throws Exception {
        List<MonitoringMetric> metrics = List.of(testMonitoringMetric);
        when(monitorService.getMonitoringMetricsByCategory("Performance")).thenReturn(metrics);

        mockMvc.perform(get("/api/monitor/monitoring-metrics/category/Performance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testCreateMonitoringMetric() throws Exception {
        when(monitorService.createMonitoringMetric(any(MonitoringMetric.class))).thenReturn(testMonitoringMetric);

        mockMvc.perform(post("/api/monitor/monitoring-metrics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMonitoringMetric)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metricName").value("Response Time"));
    }

    private ServiceHealth createMockServiceHealth() {
        ServiceHealth health = new ServiceHealth();
        health.setId(1L);
        health.setServiceName("API");
        health.setStatus("Healthy");
        health.setCpuUsage(45.0);
        health.setMemoryUsage(60.0);
        health.setInstances(2);
        health.setUptime(86400L);
        health.setVersion("1.0.0");
        health.setEndpoint("/api/health");
        health.setLastChecked(java.time.LocalDateTime.now());
        return health;
    }

    private MonitoringMetric createMockMonitoringMetric() {
        MonitoringMetric metric = new MonitoringMetric();
        metric.setId(1L);
        metric.setServiceName("API");
        metric.setMetricName("Response Time");
        metric.setCategory("Performance");
        metric.setValue(150.0);
        metric.setUnit("ms");
        metric.setTimestamp(java.time.LocalDateTime.now());
        return metric;
    }
}
