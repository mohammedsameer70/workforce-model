package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.service.PredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/predict")
@RequiredArgsConstructor
public class PredictionController {

    private final PredictionService predictionService;

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
}
