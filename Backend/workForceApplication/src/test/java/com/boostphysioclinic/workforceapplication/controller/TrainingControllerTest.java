package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.Repository.AIModelRepository;
import com.boostphysioclinic.workforceapplication.Repository.ModelComparisonRepository;
import com.boostphysioclinic.workforceapplication.TrainingService;
import com.boostphysioclinic.workforceapplication.dto.entity.AIModel;
import com.boostphysioclinic.workforceapplication.dto.entity.ModelComparison;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TrainingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainingService trainingService;

    @MockBean
    private AIModelRepository aiModelRepository;

    @MockBean
    private ModelComparisonRepository modelComparisonRepository;

    private AIModel testModel;
    private List<ModelComparison> testComparisons;

    @BeforeEach
    void setUp() {
        testModel = createMockModel();
        testComparisons = createMockComparisons();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testTrainModel() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                "date,department,demand\n2024-01-01,IT,100".getBytes()
        );

        when(trainingService.train(any(), anyList())).thenReturn("Training completed successfully");

        mockMvc.perform(multipart("/api/train")
                .file(file)
                .param("algorithms", "RandomForest", "LinearRegression"))
                .andExpect(status().isOk())
                .andExpect(content().string("Training completed successfully"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testDownloadCleanedDataset() throws Exception {
        byte[] csvData = "date,department,demand\n2024-01-01,IT,100".getBytes();
        when(trainingService.downloadCleanedDataset()).thenReturn(csvData);

        mockMvc.perform(get("/api/train/cleaned-dataset"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=cleaned_dataset.csv"))
                .andExpect(content().contentType("text/csv"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetLatestModelFound() throws Exception {
        when(aiModelRepository.findFirstByOrderByLastTrainedDesc()).thenReturn(Optional.of(testModel));

        mockMvc.perform(get("/api/train/latest-model"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Model"))
                .andExpect(jsonPath("$.algorithm").value("RandomForest"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetLatestModelNotFound() throws Exception {
        when(aiModelRepository.findFirstByOrderByLastTrainedDesc()).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/train/latest-model"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetModelComparisons() throws Exception {
        when(modelComparisonRepository.findAll()).thenReturn(testComparisons);

        mockMvc.perform(get("/api/train/model-comparisons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(testComparisons.size()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetModelComparisonsEmpty() throws Exception {
        when(modelComparisonRepository.findAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/train/model-comparisons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    private AIModel createMockModel() {
        AIModel model = new AIModel();
        model.setId(1L);
        model.setName("Test Model");
        model.setAlgorithm("RandomForest");
        model.setRSquared(0.85);
        model.setLastTrained(java.time.LocalDateTime.now());
        return model;
    }

    private List<ModelComparison> createMockComparisons() {
        List<ModelComparison> comparisons = new ArrayList<>();
        
        ModelComparison comp1 = new ModelComparison();
        comp1.setId(1L);
        comp1.setModelName("RandomForest vs LinearRegression");
        comp1.setAlgorithm("RandomForest");
        comp1.setRSquared(0.85);
        comp1.setCreatedAt(java.time.LocalDateTime.now());
        comparisons.add(comp1);

        ModelComparison comp2 = new ModelComparison();
        comp2.setId(2L);
        comp2.setModelName("XGBoost vs RandomForest");
        comp2.setAlgorithm("XGBoost");
        comp2.setRSquared(0.90);
        comp2.setCreatedAt(java.time.LocalDateTime.now());
        comparisons.add(comp2);

        return comparisons;
    }
}
