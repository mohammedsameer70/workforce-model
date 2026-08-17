package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.Repository.PredictionResultRepository;
import com.boostphysioclinic.workforceapplication.Repository.PredictionRunRepository;
import com.boostphysioclinic.workforceapplication.config.TestSecurityConfig;
import com.boostphysioclinic.workforceapplication.dto.PredictionResponse;
import com.boostphysioclinic.workforceapplication.dto.PredictionResultDTO;
import com.boostphysioclinic.workforceapplication.dto.entity.PredictionResult;
import com.boostphysioclinic.workforceapplication.dto.entity.PredictionRun;
import com.boostphysioclinic.workforceapplication.service.PredictionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class PredictionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PredictionService predictionService;

    @MockBean
    private PredictionRunRepository predictionRunRepository;

    @MockBean
    private PredictionResultRepository predictionResultRepository;

    private PredictionRun testRun;
    private List<PredictionResult> testResults;

    @BeforeEach
    void setUp() {
        testRun = createMockPredictionRun();
        testResults = createMockPredictionResults();
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testPredict() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                "date,department,demand\n2024-01-01,IT,100".getBytes());

        when(predictionService.predict(any())).thenReturn("Prediction completed successfully");

        mockMvc.perform(multipart("/api/predict")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("Prediction completed successfully"));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testFetchData() throws Exception {
        when(predictionService.fetchData()).thenReturn("Data fetched successfully");

        mockMvc.perform(get("/api/predict/fetch-data"))
                .andExpect(status().isOk())
                .andExpect(content().string("Data fetched successfully"));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetLatestPredictionFound() throws Exception {
        when(predictionRunRepository.findFirstByOrderByCreatedAtDesc()).thenReturn(testRun);
        when(predictionResultRepository.findTop50ByPredictionRunIdOrderByAttendanceDateDescIdDesc(testRun.getId()))
                .thenReturn(testResults);

        mockMvc.perform(get("/api/predict/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model").value("Test Model"))
                .andExpect(jsonPath("$.total_records").value(100))
                .andExpect(jsonPath("$.average_prediction").value(50.0))
                .andExpect(jsonPath("$.maximum_prediction").value(100.0))
                .andExpect(jsonPath("$.minimum_prediction").value(10.0))
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results.length()").value(testResults.size()));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetLatestPredictionNotFound() throws Exception {
        when(predictionRunRepository.findFirstByOrderByCreatedAtDesc()).thenReturn(null);

        mockMvc.perform(get("/api/predict/latest"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetLatestPredictionEmptyResults() throws Exception {
        when(predictionRunRepository.findFirstByOrderByCreatedAtDesc()).thenReturn(testRun);
        when(predictionResultRepository.findTop50ByPredictionRunIdOrderByAttendanceDateDescIdDesc(testRun.getId()))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/predict/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model").value("Test Model"))
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results.length()").value(0));
    }

    private PredictionRun createMockPredictionRun() {
        PredictionRun run = new PredictionRun();
        run.setId(1L);
        run.setModelName("Test Model");
        run.setTotalRecords(100);
        run.setAveragePrediction(50.0);
        run.setMaximumPrediction(100.0);
        run.setMinimumPrediction(10.0);
        run.setCreatedAt(java.time.LocalDateTime.now());
        return run;
    }

    private List<PredictionResult> createMockPredictionResults() {
        List<PredictionResult> results = new ArrayList<>();

        PredictionResult result1 = new PredictionResult();
        result1.setId(1L);
        result1.setPredictionRun(testRun);
        result1.setAttendanceDate(LocalDate.of(2024, 1, 1));
        result1.setDepartment("IT");
        result1.setActualDemand(100.0);
        result1.setPredictedDemand(95.0);
        results.add(result1);

        PredictionResult result2 = new PredictionResult();
        result2.setId(2L);
        result2.setPredictionRun(testRun);
        result2.setAttendanceDate(LocalDate.of(2024, 1, 2));
        result2.setDepartment("HR");
        result2.setActualDemand(80.0);
        result2.setPredictedDemand(78.0);
        results.add(result2);

        return results;
    }
}
