package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.Repository.PredictionResultRepository;
import com.boostphysioclinic.workforceapplication.Repository.PredictionRunRepository;
import com.boostphysioclinic.workforceapplication.dto.PredictionResponse;
import com.boostphysioclinic.workforceapplication.dto.PredictionResultDTO;
import com.boostphysioclinic.workforceapplication.dto.entity.PredictionResult;
import com.boostphysioclinic.workforceapplication.dto.entity.PredictionRun;
import com.boostphysioclinic.workforceapplication.service.PredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/predict")
@RequiredArgsConstructor
public class PredictionController {

    private final PredictionService predictionService;
    private final PredictionRunRepository predictionRunRepository;
    private final PredictionResultRepository predictionResultRepository;

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> predict(
            @RequestParam("file") MultipartFile file
    ) throws Exception {

        String response = predictionService.predict(file);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/fetch-data")
    public ResponseEntity<String> fetchData() {

        String response = predictionService.fetchData();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/latest")
    public ResponseEntity<PredictionResponse> getLatestPrediction() {

        PredictionRun latestRun = predictionRunRepository.findFirstByOrderByCreatedAtDesc();

        if (latestRun == null) {
            return ResponseEntity.noContent().build();
        }

        List<PredictionResult> results = predictionResultRepository
                .findTop50ByPredictionRunIdOrderByAttendanceDateDescIdDesc(latestRun.getId());

        List<PredictionResultDTO> resultDTOs = results.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        PredictionResponse response = PredictionResponse.builder()
                .model(latestRun.getModelName())
                .total_records(latestRun.getTotalRecords())
                .average_prediction(latestRun.getAveragePrediction())
                .maximum_prediction(latestRun.getMaximumPrediction())
                .minimum_prediction(latestRun.getMinimumPrediction())
                .results(resultDTOs)
                .build();

        return ResponseEntity.ok(response);
    }

    private PredictionResultDTO mapToDTO(PredictionResult entity) {
        return PredictionResultDTO.builder()
                .attendanceDate(entity.getAttendanceDate() != null ? entity.getAttendanceDate().toString() : null)
                .department(entity.getDepartment())
                .actualDemand(entity.getActualDemand())
                .predictedDemand(entity.getPredictedDemand())
                .build();
    }
}
