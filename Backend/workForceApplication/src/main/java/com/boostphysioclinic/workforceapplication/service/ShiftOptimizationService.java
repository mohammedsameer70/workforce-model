package com.boostphysioclinic.workforceapplication.service;

import com.boostphysioclinic.workforceapplication.Repository.ShiftStaffingRepository;
import com.boostphysioclinic.workforceapplication.Repository.AIRecommendationRepository;
import com.boostphysioclinic.workforceapplication.Repository.ShiftCoverageMatrixRepository;
import com.boostphysioclinic.workforceapplication.Repository.PredictionResultRepository;
import com.boostphysioclinic.workforceapplication.dto.entity.ShiftStaffing;
import com.boostphysioclinic.workforceapplication.dto.entity.AIRecommendation;
import com.boostphysioclinic.workforceapplication.dto.entity.ShiftCoverageMatrix;
import com.boostphysioclinic.workforceapplication.dto.ShiftMetricDTO;
import com.boostphysioclinic.workforceapplication.dto.ShiftRecommendationDTO;
import com.boostphysioclinic.workforceapplication.dto.ShiftCoverageDTO;
import com.boostphysioclinic.workforceapplication.dto.StaffingBarPointDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShiftOptimizationService {

    private final ShiftStaffingRepository shiftStaffingRepository;
    private final AIRecommendationRepository aiRecommendationRepository;
    private final ShiftCoverageMatrixRepository shiftCoverageMatrixRepository;
    private final PredictionResultRepository predictionResultRepository;

    public List<ShiftStaffing> getAllShiftStaffing() {
        return shiftStaffingRepository.findAll();
    }

    public List<ShiftStaffing> getShiftStaffingByDepartment(String department) {
        return shiftStaffingRepository.findByDepartment(department);
    }

    public List<ShiftStaffing> getShiftStaffingByShift(String shift) {
        return shiftStaffingRepository.findByShift(shift);
    }

    public ShiftStaffing createShiftStaffing(ShiftStaffing staffing) {
        return shiftStaffingRepository.save(staffing);
    }

    public List<AIRecommendation> getAllAIRecommendations() {
        return aiRecommendationRepository.findAll();
    }

    public List<AIRecommendation> getAIRecommendationsByStatus(String status) {
        return aiRecommendationRepository.findByStatus(status);
    }

    public List<AIRecommendation> getAIRecommendationsByDepartment(String department) {
        return aiRecommendationRepository.findByDepartment(department);
    }

    public List<AIRecommendation> getAIRecommendationsByPriority(String priority) {
        return aiRecommendationRepository.findByPriority(priority);
    }

    public AIRecommendation createAIRecommendation(AIRecommendation recommendation) {
        return aiRecommendationRepository.save(recommendation);
    }

    public void applyRecommendation(Long id) {
        AIRecommendation recommendation = aiRecommendationRepository.findById(id).orElseThrow();
        recommendation.setStatus("APPLIED");
        recommendation.setAppliedAt(LocalDateTime.now());
        aiRecommendationRepository.save(recommendation);
    }

    public List<ShiftCoverageMatrix> getAllShiftCoverageMatrices() {
        return shiftCoverageMatrixRepository.findAll();
    }

    public List<ShiftCoverageMatrix> getShiftCoverageMatricesByDepartment(String department) {
        return shiftCoverageMatrixRepository.findByDepartment(department);
    }

    public ShiftCoverageMatrix createShiftCoverageMatrix(ShiftCoverageMatrix matrix) {
        return shiftCoverageMatrixRepository.save(matrix);
    }

    // New methods to return DTOs matching frontend expectations
    public List<ShiftMetricDTO> getShiftMetricsDTO() {
        List<Object[]> deptResults = predictionResultRepository.findAveragePredictionByDepartment();
        List<ShiftMetricDTO> metrics = new ArrayList<>();
        
        // Calculate mock metrics from prediction data
        double totalDemand = deptResults.stream()
                .mapToDouble(r -> r[1] != null ? ((Number) r[1]).doubleValue() : 0.0)
                .sum();
        
        metrics.add(ShiftMetricDTO.builder()
                .title("Total Shift Gaps")
                .value("15")
                .icon("pi pi-exclamation-circle")
                .build());
        
        metrics.add(ShiftMetricDTO.builder()
                .title("Required Staff")
                .value(String.valueOf((int) totalDemand))
                .icon("pi pi-users")
                .build());
        
        metrics.add(ShiftMetricDTO.builder()
                .title("Current Staff")
                .value(String.valueOf((int) (totalDemand * 0.85)))
                .icon("pi pi-user-check")
                .build());
        
        metrics.add(ShiftMetricDTO.builder()
                .title("Departments Covered")
                .value(String.valueOf(deptResults.size()))
                .icon("pi pi-sitemap")
                .build());
        
        return metrics;
    }

    public List<StaffingBarPointDTO> getStaffingByDepartmentDTO() {
        List<Object[]> deptResults = predictionResultRepository.findAveragePredictionByDepartment();
        List<StaffingBarPointDTO> staffing = new ArrayList<>();
        
        for (Object[] result : deptResults) {
            if (result == null || result.length < 2) continue;
            String department = result[0] != null ? result[0].toString() : "Unknown";
            Double avgPrediction = result[1] != null ? ((Number) result[1]).doubleValue() : 0.0;
            
            staffing.add(StaffingBarPointDTO.builder()
                    .department(department)
                    .current(avgPrediction * 0.85) // Mock current staff
                    .optimal(avgPrediction) // Use prediction as optimal
                    .build());
        }
        return staffing;
    }

    public List<ShiftRecommendationDTO> getShiftRecommendationsDTO() {
        List<ShiftRecommendationDTO> recommendations = new ArrayList<>();
        
        // Generate mock recommendations based on departments
        recommendations.add(ShiftRecommendationDTO.builder()
                .id(1L)
                .title("Increase staffing for peak demand")
                .priority("high")
                .workers("5 -> 8")
                .from("Customer Support")
                .to("Morning Shift")
                .build());
        
        recommendations.add(ShiftRecommendationDTO.builder()
                .id(2L)
                .title("Optimize weekend coverage")
                .priority("medium")
                .workers("3 -> 5")
                .from("Operations")
                .to("Afternoon Shift")
                .build());
        
        recommendations.add(ShiftRecommendationDTO.builder()
                .id(3L)
                .title("Reduce overstaffing in low-demand period")
                .priority("low")
                .workers("8 -> 6")
                .from("IT")
                .to("Night Shift")
                .build());
        
        return recommendations;
    }

    public List<ShiftCoverageDTO> getShiftCoverageDTO() {
        List<Object[]> deptResults = predictionResultRepository.findAveragePredictionByDepartment();
        List<ShiftCoverageDTO> coverage = new ArrayList<>();
        
        for (Object[] result : deptResults) {
            if (result == null || result.length < 2) continue;
            String department = result[0] != null ? result[0].toString() : "Unknown";
            Double avgPrediction = result[1] != null ? ((Number) result[1]).doubleValue() : 0.0;
            
            // Generate mock coverage percentages
            double morningCoverage = 85 + (Math.random() * 10);
            double afternoonCoverage = 90 + (Math.random() * 8);
            double nightCoverage = 70 + (Math.random() * 15);
            
            String status = (morningCoverage < 80 || afternoonCoverage < 80 || nightCoverage < 70) 
                    ? "Understaffed" : "Optimal";
            
            coverage.add(ShiftCoverageDTO.builder()
                    .department(department)
                    .morning(String.format("%.0f%%", morningCoverage))
                    .afternoon(String.format("%.0f%%", afternoonCoverage))
                    .night(String.format("%.0f%%", nightCoverage))
                    .status(status)
                    .build());
        }
        return coverage;
    }
}
