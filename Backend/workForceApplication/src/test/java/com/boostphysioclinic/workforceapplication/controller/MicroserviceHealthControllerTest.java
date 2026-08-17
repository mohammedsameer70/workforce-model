package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.dto.entity.MicroserviceHealth;
import com.boostphysioclinic.workforceapplication.service.MicroserviceHealthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
class MicroserviceHealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MicroserviceHealthService microserviceHealthService;

    private MicroserviceHealth testHealth;

    @BeforeEach
    void setUp() {
        testHealth = createMockMicroserviceHealth();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetAllMicroserviceHealth() throws Exception {
        List<MicroserviceHealth> healthList = List.of(testHealth);
        when(microserviceHealthService.getAllMicroserviceHealth()).thenReturn(healthList);

        mockMvc.perform(get("/api/microservice-health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetByServiceNameFound() throws Exception {
        when(microserviceHealthService.getByServiceName("API")).thenReturn(Optional.of(testHealth));

        mockMvc.perform(get("/api/microservice-health/service/API"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceName").value("API"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetByServiceNameNotFound() throws Exception {
        when(microserviceHealthService.getByServiceName("NotFound")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/microservice-health/service/NotFound"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetByIdFound() throws Exception {
        when(microserviceHealthService.getById(1L)).thenReturn(Optional.of(testHealth));

        mockMvc.perform(get("/api/microservice-health/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetByIdNotFound() throws Exception {
        when(microserviceHealthService.getById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/microservice-health/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testCreateMicroserviceHealth() throws Exception {
        when(microserviceHealthService.createMicroserviceHealth(any(MicroserviceHealth.class))).thenReturn(testHealth);

        mockMvc.perform(post("/api/microservice-health")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testHealth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceName").value("API"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testUpdateMicroserviceHealth() throws Exception {
        when(microserviceHealthService.updateMicroserviceHealth(anyLong(), any(MicroserviceHealth.class))).thenReturn(testHealth);

        mockMvc.perform(put("/api/microservice-health/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testHealth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testDeleteMicroserviceHealth() throws Exception {
        mockMvc.perform(delete("/api/microservice-health/1"))
                .andExpect(status().isNoContent());
    }

    private MicroserviceHealth createMockMicroserviceHealth() {
        MicroserviceHealth health = new MicroserviceHealth();
        health.setId(1L);
        health.setServiceName("API");
        health.setStatus("Healthy");
        health.setCpuUsage(45.0);
        health.setMemoryUsage(60.0);
        health.setInstances(2);
        health.setUptime(86400L);
        health.setVersion("1.0.0");
        health.setLastChecked(java.time.LocalDateTime.now());
        return health;
    }
}
