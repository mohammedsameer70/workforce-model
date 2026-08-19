package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.Repository.AIModelRepository;
import com.boostphysioclinic.workforceapplication.dto.PredictionRecord;
import com.boostphysioclinic.workforceapplication.dto.entity.AIModel;
import com.boostphysioclinic.workforceapplication.service.CLPredictionService;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private CLPredictionService predictionCsvService;

    @Autowired
    private AIModelRepository aiModelRepository;

    @GetMapping("/predictions")
    public List<PredictionRecord> getPredictions()
            throws IOException, CsvValidationException {

        return predictionCsvService.getPredictions();
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> metrics = new HashMap<>();
        Map<String, Object> charts = new HashMap<>();

        try {
            // Fetch latest trained model from database
            Optional<AIModel> latestModel = aiModelRepository.findFirstByOrderByLastTrainedDesc();
            
            List<PredictionRecord> predictions = predictionCsvService.getPredictions();
            
            // Calculate real metrics from predictions
            if (!predictions.isEmpty()) {
                double totalPredicted = predictions.stream()
                    .mapToDouble(PredictionRecord::getPredictedDemand)
                    .sum();
                
                double avgPredicted = totalPredicted / predictions.size();
                
                double totalHistorical = predictions.stream()
                    .mapToDouble(PredictionRecord::getHistoricalDemand)
                    .sum();
                
                double avgHistorical = totalHistorical / predictions.size();
                
                // Calculate accuracy (simplified)
                double accuracy = 100 - (Math.abs(avgPredicted - avgHistorical) / avgHistorical * 100);
                
                // Use actual model data if available, otherwise use calculated values
                if (latestModel.isPresent()) {
                    AIModel model = latestModel.get();
                    metrics.put("Model Name", model.getName() != null ? model.getName() : model.getAlgorithm());
                    metrics.put("R² Score", model.getRSquared() != null ? String.format("%.4f", model.getRSquared()) : "N/A");
                    metrics.put("RMSE", model.getRmse() != null ? String.format("%.4f", model.getRmse()) : "N/A");
                    metrics.put("MAE", model.getMae() != null ? String.format("%.4f", model.getMae()) : "N/A");
                    metrics.put("MAPE", model.getMape() != null ? String.format("%.4f", model.getMape()) : "N/A");
                    metrics.put("Status", model.getStatus() != null ? model.getStatus() : "Unknown");
                    metrics.put("Algorithm", model.getAlgorithm() != null ? model.getAlgorithm() : "Unknown");
                    metrics.put("Version", model.getVersion() != null ? model.getVersion() : "N/A");
                } else {
                    metrics.put("Model Name", "No Model Trained");
                    metrics.put("R² Score", "N/A");
                    metrics.put("RMSE", "N/A");
                    metrics.put("MAE", "N/A");
                    metrics.put("MAPE", "N/A");
                    metrics.put("Status", "Not Trained");
                    metrics.put("Algorithm", "N/A");
                    metrics.put("Version", "N/A");
                }
                
                metrics.put("Total Predictions", predictions.size());
                metrics.put("Average Demand", String.format("%.2f", avgPredicted));
                metrics.put("Accuracy", String.format("%.1f%%", accuracy));

                // Prepare chart data from predictions
                // Line chart: Historical vs Predicted demand over time
                List<String> labels = predictions.stream()
                    .limit(7)
                    .map(PredictionRecord::getAttendanceDate)
                    .toList();
                
                List<Double> historicalData = predictions.stream()
                    .limit(7)
                    .map(PredictionRecord::getHistoricalDemand)
                    .toList();
                
                List<Double> predictedData = predictions.stream()
                    .limit(7)
                    .map(PredictionRecord::getPredictedDemand)
                    .toList();

                Map<String, Object> lineChartData = new HashMap<>();
                lineChartData.put("labels", labels);
                lineChartData.put("historical", historicalData);
                lineChartData.put("predicted", predictedData);
                charts.put("lineChart", lineChartData);

                // Bar chart: Department performance (group by department)
                Map<String, List<Double>> departmentData = new HashMap<>();
                for (PredictionRecord pred : predictions) {
                    String dept = pred.getDepartment();
                    if (dept != null && !dept.isEmpty()) {
                        departmentData.computeIfAbsent(dept, k -> new ArrayList<>()).add(pred.getPredictedDemand());
                    }
                }

                List<String> deptLabels = new ArrayList<>(departmentData.keySet());
                List<Double> deptPerformance = deptLabels.stream()
                    .map(dept -> departmentData.get(dept).stream().mapToDouble(Double::doubleValue).average().orElse(0))
                    .toList();
                List<Double> deptTarget = deptLabels.stream()
                    .map(dept -> departmentData.get(dept).stream().mapToDouble(Double::doubleValue).average().orElse(0) * 0.8)
                    .toList();

                Map<String, Object> barChartData = new HashMap<>();
                barChartData.put("labels", deptLabels);
                barChartData.put("performance", deptPerformance);
                barChartData.put("target", deptTarget);
                charts.put("barChart", barChartData);

            } else {
                // No predictions, but check if model exists
                if (latestModel.isPresent()) {
                    AIModel model = latestModel.get();
                    metrics.put("Model Name", model.getName() != null ? model.getName() : model.getAlgorithm());
                    metrics.put("R² Score", model.getRSquared() != null ? String.format("%.4f", model.getRSquared()) : "N/A");
                    metrics.put("RMSE", model.getRmse() != null ? String.format("%.4f", model.getRmse()) : "N/A");
                    metrics.put("MAE", model.getMae() != null ? String.format("%.4f", model.getMae()) : "N/A");
                    metrics.put("MAPE", model.getMape() != null ? String.format("%.4f", model.getMape()) : "N/A");
                    metrics.put("Status", model.getStatus() != null ? model.getStatus() : "Unknown");
                    metrics.put("Algorithm", model.getAlgorithm() != null ? model.getAlgorithm() : "Unknown");
                    metrics.put("Version", model.getVersion() != null ? model.getVersion() : "N/A");
                } else {
                    metrics.put("Model Name", "No Model Trained");
                    metrics.put("R² Score", "N/A");
                    metrics.put("RMSE", "N/A");
                    metrics.put("MAE", "N/A");
                    metrics.put("MAPE", "N/A");
                    metrics.put("Status", "Not Trained");
                    metrics.put("Algorithm", "N/A");
                    metrics.put("Version", "N/A");
                }
                metrics.put("Total Predictions", 0);
                metrics.put("Average Demand", "N/A");
                metrics.put("Accuracy", "N/A");
                
                charts.put("lineChart", new HashMap<>());
                charts.put("barChart", new HashMap<>());
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Try to at least get model data on error
            Optional<AIModel> latestModel = aiModelRepository.findFirstByOrderByLastTrainedDesc();
            if (latestModel.isPresent()) {
                AIModel model = latestModel.get();
                metrics.put("Model Name", model.getName() != null ? model.getName() : model.getAlgorithm());
                metrics.put("R² Score", model.getRSquared() != null ? String.format("%.4f", model.getRSquared()) : "Error");
                metrics.put("RMSE", model.getRmse() != null ? String.format("%.4f", model.getRmse()) : "Error");
                metrics.put("MAE", model.getMae() != null ? String.format("%.4f", model.getMae()) : "Error");
                metrics.put("MAPE", model.getMape() != null ? String.format("%.4f", model.getMape()) : "Error");
                metrics.put("Status", "Error loading predictions");
                metrics.put("Algorithm", model.getAlgorithm() != null ? model.getAlgorithm() : "Unknown");
                metrics.put("Version", model.getVersion() != null ? model.getVersion() : "N/A");
            } else {
                metrics.put("Model Name", "Error loading data");
                metrics.put("R² Score", "Error");
                metrics.put("RMSE", "Error");
                metrics.put("MAE", "Error");
                metrics.put("MAPE", "Error");
                metrics.put("Status", "Error");
                metrics.put("Algorithm", "Error");
                metrics.put("Version", "Error");
            }
            metrics.put("Total Predictions", "Error");
            metrics.put("Average Demand", "Error");
            metrics.put("Accuracy", "Error");
            
            charts.put("lineChart", new HashMap<>());
            charts.put("barChart", new HashMap<>());
        }

        response.put("metrics", metrics);
        response.put("charts", charts);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverview() {
        return getDashboard();
    }
}