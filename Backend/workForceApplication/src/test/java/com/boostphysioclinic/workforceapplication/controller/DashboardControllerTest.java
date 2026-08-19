package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.Repository.AIModelRepository;
import com.boostphysioclinic.workforceapplication.dto.PredictionRecord;
import com.boostphysioclinic.workforceapplication.service.CLPredictionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CLPredictionService predictionCsvService;

    @MockBean
    private AIModelRepository aiModelRepository;

    private List<PredictionRecord> mockPredictions;

    @BeforeEach
    void setUp() {
        mockPredictions = createMockPredictions();
        when(aiModelRepository.findFirstByOrderByLastTrainedDesc())
                .thenReturn(Optional.empty());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetPredictions() throws Exception {
        when(predictionCsvService.getPredictions()).thenReturn(mockPredictions);

        mockMvc.perform(get("/api/dashboard/predictions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(mockPredictions.size()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetPredictionsEmpty() throws Exception {
        when(predictionCsvService.getPredictions()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/dashboard/predictions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetDashboardWithData() throws Exception {

        when(predictionCsvService.getPredictions()).thenReturn(mockPredictions);

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metrics").exists())
                .andExpect(jsonPath("$.charts").exists())
                .andExpect(jsonPath("$.metrics[\"Model Name\"]").value("No Model Trained"))
                .andExpect(jsonPath("$.metrics[\"Status\"]").value("Not Trained"))
                .andExpect(jsonPath("$.metrics[\"Total Predictions\"]").value(mockPredictions.size()))
                .andExpect(jsonPath("$.charts.lineChart").exists())
                .andExpect(jsonPath("$.charts.barChart").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetDashboardEmptyData() throws Exception {

        when(predictionCsvService.getPredictions()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metrics").exists())
                .andExpect(jsonPath("$.charts").exists())
                .andExpect(jsonPath("$.metrics[\"Model Name\"]").value("No Model Trained"))
                .andExpect(jsonPath("$.metrics[\"Status\"]").value("Not Trained"))
                .andExpect(jsonPath("$.metrics[\"Total Predictions\"]").value(0))
                .andExpect(jsonPath("$.charts.lineChart").exists())
                .andExpect(jsonPath("$.charts.barChart").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetDashboardWithError() throws Exception {

        when(predictionCsvService.getPredictions())
                .thenThrow(new IOException("Test error"));
        when(aiModelRepository.findFirstByOrderByLastTrainedDesc())
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metrics").exists())
                .andExpect(jsonPath("$.charts").exists())
                .andExpect(jsonPath("$.metrics[\"Model Name\"]").value("Error loading data"))
                .andExpect(jsonPath("$.metrics[\"Status\"]").value("Error"))
                .andExpect(jsonPath("$.charts.lineChart").exists())
                .andExpect(jsonPath("$.charts.barChart").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetOverview() throws Exception {

        when(predictionCsvService.getPredictions()).thenReturn(mockPredictions);

        mockMvc.perform(get("/api/dashboard/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metrics").exists())
                .andExpect(jsonPath("$.charts").exists());
    }

    private List<PredictionRecord> createMockPredictions() {

        List<PredictionRecord> predictions = new ArrayList<>();

        for (int i = 0; i < 10; i++) {

            PredictionRecord record = new PredictionRecord();
            record.setAttendanceDate("2024-01-" + String.format("%02d", i + 1));
            record.setHistoricalDemand(100.0 + i * 10);
            record.setPredictedDemand(95.0 + i * 10);
            record.setDepartment(i % 2 == 0 ? "Department A" : "Department B");

            predictions.add(record);
        }

        return predictions;
    }/**/
}