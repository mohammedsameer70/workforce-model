package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.dto.entity.ShiftStaffing;
import com.boostphysioclinic.workforceapplication.dto.entity.AIRecommendation;
import com.boostphysioclinic.workforceapplication.dto.entity.ShiftCoverageMatrix;
import com.boostphysioclinic.workforceapplication.dto.ShiftMetricDTO;
import com.boostphysioclinic.workforceapplication.dto.ShiftRecommendationDTO;
import com.boostphysioclinic.workforceapplication.dto.ShiftCoverageDTO;
import com.boostphysioclinic.workforceapplication.dto.StaffingBarPointDTO;
import com.boostphysioclinic.workforceapplication.service.ShiftOptimizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shift-optimization")
@RequiredArgsConstructor
public class ShiftOptimizationController {

    private final ShiftOptimizationService shiftOptimizationService;

    // New DTO endpoints for frontend
    @GetMapping("/metrics")
    public List<ShiftMetricDTO> getMetrics() {
        return shiftOptimizationService.getShiftMetricsDTO();
    }

    @GetMapping("/staffing-by-department")
    public List<StaffingBarPointDTO> getStaffingByDepartment() {
        return shiftOptimizationService.getStaffingByDepartmentDTO();
    }

    @GetMapping("/recommendations")
    public List<ShiftRecommendationDTO> getRecommendations() {
        return shiftOptimizationService.getShiftRecommendationsDTO();
    }

    @GetMapping("/coverage")
    public List<ShiftCoverageDTO> getCoverage() {
        return shiftOptimizationService.getShiftCoverageDTO();
    }

    // Original entity endpoints
    @GetMapping("/shift-staffing")
    public List<ShiftStaffing> getAllShiftStaffing() {
        return shiftOptimizationService.getAllShiftStaffing();
    }

    @GetMapping("/shift-staffing/department/{department}")
    public List<ShiftStaffing> getShiftStaffingByDepartment(@PathVariable String department) {
        return shiftOptimizationService.getShiftStaffingByDepartment(department);
    }

    @GetMapping("/shift-staffing/shift/{shift}")
    public List<ShiftStaffing> getShiftStaffingByShift(@PathVariable String shift) {
        return shiftOptimizationService.getShiftStaffingByShift(shift);
    }

    @PostMapping("/shift-staffing")
    public ShiftStaffing createShiftStaffing(@RequestBody ShiftStaffing staffing) {
        return shiftOptimizationService.createShiftStaffing(staffing);
    }

    @GetMapping("/ai-recommendations")
    public List<AIRecommendation> getAllAIRecommendations() {
        return shiftOptimizationService.getAllAIRecommendations();
    }

    @GetMapping("/ai-recommendations/status/{status}")
    public List<AIRecommendation> getAIRecommendationsByStatus(@PathVariable String status) {
        return shiftOptimizationService.getAIRecommendationsByStatus(status);
    }

    @GetMapping("/ai-recommendations/department/{department}")
    public List<AIRecommendation> getAIRecommendationsByDepartment(@PathVariable String department) {
        return shiftOptimizationService.getAIRecommendationsByDepartment(department);
    }

    @GetMapping("/ai-recommendations/priority/{priority}")
    public List<AIRecommendation> getAIRecommendationsByPriority(@PathVariable String priority) {
        return shiftOptimizationService.getAIRecommendationsByPriority(priority);
    }

    @PostMapping("/ai-recommendations")
    public AIRecommendation createAIRecommendation(@RequestBody AIRecommendation recommendation) {
        return shiftOptimizationService.createAIRecommendation(recommendation);
    }

    @PatchMapping("/ai-recommendations/{id}/apply")
    public ResponseEntity<Void> applyRecommendation(@PathVariable Long id) {
        shiftOptimizationService.applyRecommendation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/shift-coverage")
    public List<ShiftCoverageMatrix> getAllShiftCoverageMatrices() {
        return shiftOptimizationService.getAllShiftCoverageMatrices();
    }

    @GetMapping("/shift-coverage/department/{department}")
    public List<ShiftCoverageMatrix> getShiftCoverageMatricesByDepartment(@PathVariable String department) {
        return shiftOptimizationService.getShiftCoverageMatricesByDepartment(department);
    }

    @PostMapping("/shift-coverage")
    public ShiftCoverageMatrix createShiftCoverageMatrix(@RequestBody ShiftCoverageMatrix matrix) {
        return shiftOptimizationService.createShiftCoverageMatrix(matrix);
    }
}
