package com.boostphysioclinic.workforceapplication;
import com.boostphysioclinic.workforceapplication.Repository.AIModelRepository;
import com.boostphysioclinic.workforceapplication.Repository.ModelComparisonRepository;
import com.boostphysioclinic.workforceapplication.dto.entity.AIModel;
import com.boostphysioclinic.workforceapplication.dto.entity.ModelComparison;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TrainingService {

    @Value("${python.api.url}")
    private String pythonUrl;

    @Value("${python.download.url}")
    private String downloadUrl;

    @Autowired
    private AIModelRepository aiModelRepository;

    @Autowired
    private ModelComparisonRepository modelComparisonRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String train(
            MultipartFile file,
            List<String> algorithms
    ) throws Exception {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(
                MediaType.MULTIPART_FORM_DATA
        );

        ByteArrayResource resource =
                new ByteArrayResource(file.getBytes()) {

                    @Override
                    public String getFilename() {
                        return file.getOriginalFilename();
                    }

                };

        MultiValueMap<String, Object> body =
                new LinkedMultiValueMap<>();

        body.add("file", resource);

        body.add("algorithms", String.join(",", algorithms));

        HttpEntity<MultiValueMap<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        pythonUrl,
                        request,
                        String.class
                );

        String responseBody = response.getBody();

        // Save training results to database
        saveTrainingResults(responseBody, file.getOriginalFilename(), algorithms);

        return responseBody;

    }

    private void saveTrainingResults(String responseBody, String fileName, List<String> algorithms) {
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            // Save AI Model
            String bestModel = jsonNode.has("bestModel") ? jsonNode.get("bestModel").asText() : "Unknown";
            JsonNode metrics = jsonNode.has("metrics") ? jsonNode.get("metrics") : null;

            AIModel aiModel = AIModel.builder()
                    .name(bestModel != null ? bestModel : "Unknown Model")
                    .algorithm(bestModel)
                    .version("1.0")
                    .status("TRAINED")
                    .rmse(metrics != null && metrics.has("RMSE") ? metrics.get("RMSE").asDouble() : null)
                    .mae(metrics != null && metrics.has("MAE") ? metrics.get("MAE").asDouble() : null)
                    .mape(metrics != null && metrics.has("MAPE") ? metrics.get("MAPE").asDouble() : null)
                    .rSquared(metrics != null && metrics.has("R2") ? metrics.get("R2").asDouble() : null)
                    .lastTrained(LocalDateTime.now())
                    .build();

            aiModelRepository.save(aiModel);

            // Save Model Comparisons
            if (jsonNode.has("comparison")) {
                JsonNode comparisonArray = jsonNode.get("comparison");
                for (JsonNode comparison : comparisonArray) {
                    ModelComparison modelComparison = ModelComparison.builder()
                            .modelName(comparison.has("Model") ? comparison.get("Model").asText() : "Unknown")
                            .algorithm(comparison.has("Model") ? comparison.get("Model").asText() : "Unknown")
                            .rmse(comparison.has("RMSE") ? comparison.get("RMSE").asDouble() : null)
                            .mae(comparison.has("MAE") ? comparison.get("MAE").asDouble() : null)
                            .mape(comparison.has("MAPE") ? comparison.get("MAPE").asDouble() : null)
                            .rSquared(comparison.has("R2") ? comparison.get("R2").asDouble() : null)
                            .status(comparison.has("Model") && comparison.get("Model").asText().equals(bestModel) ? "Best" : "Good")
                            .createdAt(LocalDateTime.now())
                            .build();

                    modelComparisonRepository.save(modelComparison);
                }
            }

        } catch (Exception e) {
            System.err.println("Failed to save training results to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public byte[] downloadCleanedDataset() {

        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.getForObject(
                downloadUrl,
                byte[].class
        );
    }

}