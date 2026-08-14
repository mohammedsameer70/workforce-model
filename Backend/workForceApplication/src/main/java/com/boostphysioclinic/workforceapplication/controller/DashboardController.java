package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.dto.PredictionRecord;
import com.boostphysioclinic.workforceapplication.service.CLPredictionService;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private CLPredictionService predictionCsvService;

    @GetMapping("/predictions")
    public List<PredictionRecord> getPredictions()
            throws IOException, CsvValidationException {

        return predictionCsvService.getPredictions();
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> metrics = new HashMap<>();

        metrics.put("Best Model", "Awaiting training");
        metrics.put("R² Score", "Pending");
        metrics.put("RMSE", "Pending");
        metrics.put("Status", "Ready to train");

        response.put("metrics", metrics);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverview() {
        return getDashboard();
    }
}