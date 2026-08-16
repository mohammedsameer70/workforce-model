package com.boostphysioclinic.workforceapplication.service;

import com.boostphysioclinic.workforceapplication.Repository.PredictionResultRepository;
import com.boostphysioclinic.workforceapplication.Repository.PredictionRunRepository;
import com.boostphysioclinic.workforceapplication.dto.PredictionResponse;
import com.boostphysioclinic.workforceapplication.dto.PredictionResultDTO;
import com.boostphysioclinic.workforceapplication.dto.entity.PredictionResult;
import com.boostphysioclinic.workforceapplication.dto.entity.PredictionRun;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PredictionService {

    private final PredictionRunRepository predictionRunRepository;

    private final PredictionResultRepository predictionResultRepository;

    private final CSVImportService csvImportService;

    private final RestTemplate restTemplate = new RestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper();


    // ============================================================
    // PREDICT
    // ============================================================

    public String predict(MultipartFile file) throws Exception {

        if (file == null || file.isEmpty()) {

            throw new IllegalArgumentException(
                    "CSV file is empty or was not provided"
            );
        }

        File tempFile = null;

        try {

            System.out.println("==========================================");
            System.out.println("STARTING PREDICTION");
            System.out.println(
                    "File: " + file.getOriginalFilename()
            );
            System.out.println(
                    "Original size: " + file.getSize() + " bytes"
            );
            System.out.println("==========================================");


            // ========================================================
            // 1. CREATE REAL TEMPORARY FILE
            // ========================================================

            tempFile = File.createTempFile(
                    "workforce-prediction-",
                    ".csv"
            );

            file.transferTo(tempFile);

            System.out.println(
                    "Temporary file created: "
                            + tempFile.getAbsolutePath()
            );

            System.out.println(
                    "Temporary file exists: "
                            + tempFile.exists()
            );

            System.out.println(
                    "Temporary file size: "
                            + tempFile.length()
                            + " bytes"
            );


            // ========================================================
            // 2. SEND CSV TO PYTHON
            // ========================================================

            MultiValueMap<String, Object> body =
                    new LinkedMultiValueMap<>();

            body.add(
                    "file",
                    new FileSystemResource(tempFile)
            );


            HttpHeaders headers =
                    new HttpHeaders();

            headers.setContentType(
                    MediaType.MULTIPART_FORM_DATA
            );


            HttpEntity<MultiValueMap<String, Object>> request =
                    new HttpEntity<>(
                            body,
                            headers
                    );


            System.out.println(
                    "Calling Python prediction service..."
            );


            ResponseEntity<String> response =
                    restTemplate.postForEntity(
                            "http://localhost:8000/predict",
                            request,
                            String.class
                    );


            String json =
                    response.getBody();


            if (json == null || json.isBlank()) {

                throw new RuntimeException(
                        "Python prediction service returned an empty response"
                );
            }


            System.out.println(
                    "Python prediction completed successfully."
            );


            // ========================================================
            // 3. SAVE PYTHON PREDICTION
            // ========================================================

            System.out.println(
                    "Saving prediction results..."
            );


            savePrediction(
                    json,
                    file.getOriginalFilename()
            );


            System.out.println(
                    "Prediction results saved."
            );


            // ========================================================
            // 4. IMPORT CLEANED CSV INTO DATABASE
            // ========================================================

            System.out.println(
                    "=========================================="
            );

            System.out.println(
                    "STARTING DATABASE CSV IMPORT"
            );

            System.out.println(
                    "Using file: "
                            + tempFile.getAbsolutePath()
            );


            /*
             * IMPORTANT:
             *
             * Pass the REAL temporary File.
             *
             * Do not pass MultipartFile here.
             *
             * This prevents the Tomcat temporary upload file
             * from disappearing before CSVImportService reads it.
             */

            String importResult =
                    csvImportService.importWorkforceCSV(
                            tempFile,
                            file.getOriginalFilename()
                    );


            System.out.println(
                    "DATABASE CSV IMPORT COMPLETED"
            );

            System.out.println(
                    importResult
            );


            // ========================================================
            // 5. SUCCESS
            // ========================================================

            System.out.println(
                    "=========================================="
            );

            System.out.println(
                    "PREDICTION COMPLETED SUCCESSFULLY"
            );

            System.out.println(
                    "=========================================="
            );


            return json;


        } catch (Exception e) {

            System.err.println(
                    "=========================================="
            );

            System.err.println(
                    "PREDICTION FAILED"
            );

            System.err.println(
                    "Error: " + e.getMessage()
            );

            e.printStackTrace();

            System.err.println(
                    "=========================================="
            );


            throw e;


        } finally {

            // ========================================================
            // 6. DELETE TEMP FILE
            // ========================================================

            /*
             * This executes ONLY after:
             *
             * Python prediction
             * AND
             * Database CSV import
             *
             * have finished.
             */

            if (tempFile != null) {

                try {

                    if (tempFile.exists()) {

                        boolean deleted =
                                tempFile.delete();

                        System.out.println(
                                "Temporary file deleted: "
                                        + deleted
                        );

                        if (!deleted) {

                            System.err.println(
                                    "WARNING: Could not delete temporary file: "
                                            + tempFile.getAbsolutePath()
                            );
                        }

                    } else {

                        System.out.println(
                                "Temporary file already removed."
                        );
                    }

                } catch (Exception cleanupException) {

                    System.err.println(
                            "Could not delete temporary file: "
                                    + cleanupException.getMessage()
                    );
                }
            }
        }
    }


    // ============================================================
    // SAVE PREDICTION
    // ============================================================

    private void savePrediction(
            String json,
            String uploadedFile
    ) {

        try {

            System.out.println(
                    "========== SAVE PREDICTION STARTED =========="
            );


            // ========================================================
            // VALIDATE RESPONSE
            // ========================================================

            if (json == null || json.isBlank()) {

                throw new RuntimeException(
                        "Prediction service returned empty response"
                );
            }


            // ========================================================
            // CONVERT JSON
            // ========================================================

            PredictionResponse prediction =
                    objectMapper.readValue(
                            json,
                            PredictionResponse.class
                    );


            System.out.println(
                    "Model: "
                            + prediction.getModel()
            );


            System.out.println(
                    "Total records: "
                            + prediction.getTotal_records()
            );


            // ========================================================
            // CREATE PREDICTION RUN
            // ========================================================

            PredictionRun run =
                    PredictionRun.builder()

                            .modelName(
                                    prediction.getModel()
                            )

                            .uploadedFile(
                                    uploadedFile
                            )

                            .totalRecords(
                                    prediction.getTotal_records()
                            )

                            .averagePrediction(
                                    prediction.getAverage_prediction()
                            )

                            .maximumPrediction(
                                    prediction.getMaximum_prediction()
                            )

                            .minimumPrediction(
                                    prediction.getMinimum_prediction()
                            )

                            .createdAt(
                                    LocalDateTime.now()
                            )

                            .build();


            run =
                    predictionRunRepository.save(
                            run
                    );


            predictionRunRepository.flush();


            System.out.println(
                    "Prediction Run ID: "
                            + run.getId()
            );


            // ========================================================
            // CREATE PREDICTION RESULTS
            // ========================================================

            List<PredictionResult> results =
                    new ArrayList<>();


            if (prediction.getResults() != null) {

                for (
                        PredictionResultDTO dto
                        : prediction.getResults()
                ) {

                    if (dto == null) {
                        continue;
                    }


                    LocalDate predictionDate =
                            parsePredictionDate(
                                    dto.getAttendanceDate()
                            );


                    PredictionResult entity =
                            PredictionResult.builder()

                                    .attendanceDate(
                                            predictionDate
                                    )

                                    .department(
                                            dto.getDepartment()
                                    )

                                    .actualDemand(
                                            dto.getActualDemand()
                                    )

                                    .predictedDemand(
                                            dto.getPredictedDemand()
                                    )

                                    .predictionRun(
                                            run
                                    )

                                    .build();


                    results.add(
                            entity
                    );
                }
            }


            // ========================================================
            // SAVE RESULTS IN BATCH
            // ========================================================

            if (!results.isEmpty()) {
                long startTime = System.currentTimeMillis();
                System.out.println("Starting batch save of " + results.size() + " prediction results...");
                
                predictionResultRepository.saveAll(results);
                
                long saveTime = System.currentTimeMillis() - startTime;
                System.out.println("Batch save completed in " + saveTime + "ms");
                
                // Remove flush() to let JPA optimize batch operations
                // Only flush if you need immediate database consistency
                // predictionResultRepository.flush();
            }


            System.out.println(
                    "Saved prediction results: "
                            + results.size()
            );


            System.out.println(
                    "========== SAVE PREDICTION COMPLETED =========="
            );


        } catch (Exception e) {

            System.err.println(
                    "========== SAVE PREDICTION FAILED =========="
            );

            e.printStackTrace();

            throw new RuntimeException(
                    "Failed to save prediction results: "
                            + e.getMessage(),
                    e
            );
        }
    }


    // ============================================================
    // DATE PARSER
    // ============================================================

    private LocalDate parsePredictionDate(
            String value
    ) {

        if (
                value == null
                        || value.isBlank()
        ) {

            return null;
        }


        try {

            /*
             * Python may return:
             *
             * 2026-08-09
             *
             * OR:
             *
             * 2026-08-09T00:00:00
             */

            if (value.length() == 10) {

                return LocalDate.parse(
                        value
                );
            }


            return LocalDateTime
                    .parse(value)
                    .toLocalDate();


        } catch (Exception e) {

            System.err.println(
                    "Unable to parse prediction date: "
                            + value
            );

            return null;
        }
    }


    // ============================================================
    // FETCH DATA FROM PYTHON
    // ============================================================

    public String fetchData() {

        ResponseEntity<String> response =
                restTemplate.getForEntity(
                        "http://localhost:8000/predict/fetch-data",
                        String.class
                );


        return response.getBody();
    }
}