package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.dto.PredictionRecord;
import com.boostphysioclinic.workforceapplication.service.CLPredictionService;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

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
}