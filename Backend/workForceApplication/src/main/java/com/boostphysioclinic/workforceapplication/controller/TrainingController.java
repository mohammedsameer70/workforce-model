package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

@RestController
@RequestMapping("/api/train")
public class TrainingController {

    @Autowired
    private TrainingService trainingService;

    @PostMapping
    public ResponseEntity<String> trainModel(
            @RequestParam("file") MultipartFile file,
            @RequestParam("algorithms") List<String> algorithms
    ) throws Exception {
        System.out.println("=== Training Controller ===");
        System.out.println("File: " + file.getOriginalFilename());
        System.out.println("Algorithms: " + algorithms);

        return ResponseEntity.ok(
                trainingService.train(file, algorithms)
        );
    }
    @GetMapping("/cleaned-dataset")
    public ResponseEntity<byte[]> downloadCleanedDataset() {

        byte[] csv = trainingService.downloadCleanedDataset();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=cleaned_dataset.csv"
                )
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

}