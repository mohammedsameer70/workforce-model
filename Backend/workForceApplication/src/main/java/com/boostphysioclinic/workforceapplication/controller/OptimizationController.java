package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.dto.*;
import com.boostphysioclinic.workforceapplication.service.OptimizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/optimization")
@RequiredArgsConstructor
public class OptimizationController {

    private final OptimizationService optimizationService;

    @PostMapping("/run")
    public ResponseEntity<OptimizationResultDTO> runOptimization() {
        OptimizationResultDTO result = optimizationService.runOptimization();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/result")
    public ResponseEntity<OptimizationResultDTO> getOptimizationResult() {
        OptimizationResultDTO result = optimizationService.runOptimization();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/staff-allocations")
    public ResponseEntity<List<StaffAllocationDTO>> getStaffAllocations() {
        List<StaffAllocationDTO> allocations = optimizationService.getStaffAllocations();
        return ResponseEntity.ok(allocations);
    }

    @GetMapping("/shift-assignments")
    public ResponseEntity<List<ShiftAssignmentDTO>> getShiftAssignments() {
        List<ShiftAssignmentDTO> assignments = optimizationService.getShiftAssignments();
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/reassignments")
    public ResponseEntity<List<EmployeeReassignmentDTO>> getReassignments() {
        List<EmployeeReassignmentDTO> reassignments = optimizationService.getReassignments();
        return ResponseEntity.ok(reassignments);
    }

    @GetMapping("/warnings")
    public ResponseEntity<List<OptimizationWarningDTO>> getWarnings() {
        List<OptimizationWarningDTO> warnings = optimizationService.getWarnings();
        return ResponseEntity.ok(warnings);
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<OptimizationRecommendationDTO>> getRecommendations() {
        List<OptimizationRecommendationDTO> recommendations = optimizationService.getRecommendations();
        return ResponseEntity.ok(recommendations);
    }

    @PostMapping("/apply-recommendation")
    public ResponseEntity<Void> applyRecommendation(@RequestBody OptimizationRecommendationDTO recommendation) {
        optimizationService.applyRecommendation(recommendation);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create-allocation")
    public ResponseEntity<StaffAllocationDTO> createAllocation(@RequestBody StaffAllocationDTO allocation) {
        StaffAllocationDTO created = optimizationService.createAllocation(allocation);
        return ResponseEntity.ok(created);
    }
}
