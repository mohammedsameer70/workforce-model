package com.boostphysioclinic.workforceapplication.controller;

import com.workforce.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/train")
@CrossOrigin(origins = "*")
public class TrainingController {

    @Autowired
    private TrainingService trainingService;

    @PostMapping
    public ResponseEntity<String> trainModel(
            @RequestParam("file") MultipartFile file,
            @RequestParam("algorithms") String algorithms
    ) throws Exception {

        return ResponseEntity.ok(
                trainingService.train(file, algorithms)
        );
    }

}