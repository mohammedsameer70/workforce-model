package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.Repository.AIModelRepository;
import com.boostphysioclinic.workforceapplication.Repository.ModelComparisonRepository;
import com.boostphysioclinic.workforceapplication.TrainingService;
import com.boostphysioclinic.workforceapplication.dto.entity.AIModel;
import com.boostphysioclinic.workforceapplication.dto.entity.ModelComparison;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/train")
public class TrainingController {
    @Autowired
    private TrainingService trainingService;
    @Autowired
    private AIModelRepository aiModelRepository;
    @Autowired
    private ModelComparisonRepository modelComparisonRepository;
    @PostMapping
    public ResponseEntity<String> trainModel( @RequestParam("file") MultipartFile file, @RequestParam("algorithms") List<String> algorithms) throws Exception {
        return ResponseEntity.ok(trainingService.train(file, algorithms));
    }
    @GetMapping("/cleaned-dataset")
    public ResponseEntity<byte[]> downloadCleanedDataset() {
        byte[] csv = trainingService.downloadCleanedDataset();
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=cleaned_dataset.csv")
                .contentType(MediaType.parseMediaType("text/csv")).body(csv);
    }
    @GetMapping("/latest-model")
    public ResponseEntity<AIModel> getLatestModel() {
        Optional<AIModel> latestModel = aiModelRepository.findFirstByOrderByLastTrainedDesc();
        return latestModel.map(ResponseEntity::ok).orElse(ResponseEntity.noContent().build());
    }
    @GetMapping("/model-comparisons")
    public ResponseEntity<List<ModelComparison>> getModelComparisons() {
        List<ModelComparison> comparisons = modelComparisonRepository.findAll();
        return ResponseEntity.ok(comparisons);
    }

}