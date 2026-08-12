package com.boostphysioclinic.workforceapplication.service;

import com.boostphysioclinic.workforceapplication.Repository.PredictionResultRepository;
import com.boostphysioclinic.workforceapplication.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OptimizationService {

    private final PredictionResultRepository predictionResultRepository;

    // Mock current staff data per department (in real app, this would come from HR system)
    private static final Map<String, Integer> CURRENT_STAFF = new HashMap<>();
    
    static {
        CURRENT_STAFF.put("HR", 30);
        CURRENT_STAFF.put("Packing", 90);
        CURRENT_STAFF.put("Quality Control", 120);
        CURRENT_STAFF.put("Inbound", 115);
        CURRENT_STAFF.put("Operations", 120);
        CURRENT_STAFF.put("Sales", 115);
        CURRENT_STAFF.put("IT", 120);
        CURRENT_STAFF.put("Outbound", 115);
        CURRENT_STAFF.put("Sortation", 115);
        CURRENT_STAFF.put("Customer Support", 115);
        CURRENT_STAFF.put("Finance", 115);
        CURRENT_STAFF.put("Warehouse", 120);
    }

    // Mock shift distribution percentages
    private static final double MORNING_SHIFT_PERCENT = 0.45;
    private static final double AFTERNOON_SHIFT_PERCENT = 0.35;
    private static final double NIGHT_SHIFT_PERCENT = 0.20;

    // Mock hourly wage per employee
    private static final double HOURLY_WAGE = 25.0;
    private static final double HOURS_PER_SHIFT = 8.0;

    public OptimizationResultDTO runOptimization() {
        // Get predicted demand by department
        List<Object[]> deptPredictions = predictionResultRepository.findAveragePredictionByDepartment();
        
        // Calculate optimization metrics
        List<StaffAllocationDTO> allocations = calculateStaffAllocations(deptPredictions);
        List<ShiftAssignmentDTO> shiftAssignments = calculateShiftAssignments(allocations);
        List<EmployeeReassignmentDTO> reassignments = calculateReassignments(allocations);
        List<OptimizationWarningDTO> warnings = generateWarnings(allocations);
        List<OptimizationRecommendationDTO> recommendations = generateRecommendations(allocations, reassignments);

        // Calculate overall metrics
        int totalCurrentStaff = allocations.stream().mapToInt(StaffAllocationDTO::getCurrentStaff).sum();
        int totalRecommendedStaff = allocations.stream().mapToInt(StaffAllocationDTO::getRecommendedStaff).sum();
        int employeesReassigned = reassignments.stream().mapToInt(EmployeeReassignmentDTO::getEmployeesToMove).sum();
        int departmentsOptimized = (int) allocations.stream().filter(a -> a.getSurplus() > 0 || a.getShortage() > 0).count();

        double currentUtilization = calculateCurrentUtilization(allocations);
        double optimizedUtilization = calculateOptimizedUtilization(allocations);
        
        double dailyCostBefore = totalCurrentStaff * HOURLY_WAGE * HOURS_PER_SHIFT;
        double dailyCostAfter = totalRecommendedStaff * HOURLY_WAGE * HOURS_PER_SHIFT;
        double dailySavings = dailyCostBefore - dailyCostAfter;
        double savingsPercentage = (dailySavings / dailyCostBefore) * 100;

        // Calculate optimization score (0-100)
        int optimizationScore = calculateOptimizationScore(allocations, shiftAssignments);

        return OptimizationResultDTO.builder()
                .optimizationScore(optimizationScore)
                .employeesReassigned(employeesReassigned)
                .departmentsOptimized(departmentsOptimized)
                .estimatedSavingsPercentage(Math.round(savingsPercentage * 100.0) / 100.0)
                .currentUtilization(Math.round(currentUtilization * 100.0) / 100.0)
                .optimizedUtilization(Math.round(optimizedUtilization * 100.0) / 100.0)
                .dailyCostBefore(Math.round(dailyCostBefore * 100.0) / 100.0)
                .dailyCostAfter(Math.round(dailyCostAfter * 100.0) / 100.0)
                .dailySavings(Math.round(dailySavings * 100.0) / 100.0)
                .build();
    }

    public List<StaffAllocationDTO> getStaffAllocations() {
        List<Object[]> deptPredictions = predictionResultRepository.findAveragePredictionByDepartment();
        return calculateStaffAllocations(deptPredictions);
    }

    public List<ShiftAssignmentDTO> getShiftAssignments() {
        List<Object[]> deptPredictions = predictionResultRepository.findAveragePredictionByDepartment();
        List<StaffAllocationDTO> allocations = calculateStaffAllocations(deptPredictions);
        return calculateShiftAssignments(allocations);
    }

    public List<EmployeeReassignmentDTO> getReassignments() {
        List<Object[]> deptPredictions = predictionResultRepository.findAveragePredictionByDepartment();
        List<StaffAllocationDTO> allocations = calculateStaffAllocations(deptPredictions);
        return calculateReassignments(allocations);
    }

    public List<OptimizationWarningDTO> getWarnings() {
        List<Object[]> deptPredictions = predictionResultRepository.findAveragePredictionByDepartment();
        List<StaffAllocationDTO> allocations = calculateStaffAllocations(deptPredictions);
        return generateWarnings(allocations);
    }

    public List<OptimizationRecommendationDTO> getRecommendations() {
        List<Object[]> deptPredictions = predictionResultRepository.findAveragePredictionByDepartment();
        List<StaffAllocationDTO> allocations = calculateStaffAllocations(deptPredictions);
        List<EmployeeReassignmentDTO> reassignments = calculateReassignments(allocations);
        return generateRecommendations(allocations, reassignments);
    }

    private List<StaffAllocationDTO> calculateStaffAllocations(List<Object[]> deptPredictions) {
        List<StaffAllocationDTO> allocations = new ArrayList<>();
        
        for (Object[] result : deptPredictions) {
            if (result == null || result.length < 2) continue;
            String department = result[0] != null ? result[0].toString() : "Unknown";
            Double predictedDemand = result[1] != null ? ((Number) result[1]).doubleValue() : 0.0;
            
            Integer currentStaff = CURRENT_STAFF.getOrDefault(department, 100);
            Integer recommendedStaff = (int) Math.round(predictedDemand);
            Integer surplus = Math.max(0, currentStaff - recommendedStaff);
            Integer shortage = Math.max(0, recommendedStaff - currentStaff);
            
            allocations.add(StaffAllocationDTO.builder()
                    .department(department)
                    .predictedDemand(predictedDemand)
                    .currentStaff(currentStaff)
                    .recommendedStaff(recommendedStaff)
                    .surplus(surplus)
                    .shortage(shortage)
                    .build());
        }
        
        return allocations;
    }

    private List<ShiftAssignmentDTO> calculateShiftAssignments(List<StaffAllocationDTO> allocations) {
        List<ShiftAssignmentDTO> shiftAssignments = new ArrayList<>();
        
        int totalRequired = allocations.stream().mapToInt(StaffAllocationDTO::getRecommendedStaff).sum();
        int morningRequired = (int) Math.round(totalRequired * MORNING_SHIFT_PERCENT);
        int afternoonRequired = (int) Math.round(totalRequired * AFTERNOON_SHIFT_PERCENT);
        int nightRequired = (int) Math.round(totalRequired * NIGHT_SHIFT_PERCENT);
        
        // Mock current assignments
        int morningAssigned = (int) Math.round(totalRequired * 0.40);
        int afternoonAssigned = (int) Math.round(totalRequired * 0.40);
        int nightAssigned = (int) Math.round(totalRequired * 0.20);
        
        shiftAssignments.add(ShiftAssignmentDTO.builder()
                .shift("Morning (06:00-14:00)")
                .required(morningRequired)
                .assigned(morningAssigned)
                .gap(morningRequired - morningAssigned)
                .build());
        
        shiftAssignments.add(ShiftAssignmentDTO.builder()
                .shift("Afternoon (14:00-22:00)")
                .required(afternoonRequired)
                .assigned(afternoonAssigned)
                .gap(afternoonRequired - afternoonAssigned)
                .build());
        
        shiftAssignments.add(ShiftAssignmentDTO.builder()
                .shift("Night (22:00-06:00)")
                .required(nightRequired)
                .assigned(nightAssigned)
                .gap(nightRequired - nightAssigned)
                .build());
        
        return shiftAssignments;
    }

    private List<EmployeeReassignmentDTO> calculateReassignments(List<StaffAllocationDTO> allocations) {
        List<EmployeeReassignmentDTO> reassignments = new ArrayList<>();
        
        // Find overstaffed departments (surplus)
        List<StaffAllocationDTO> overstaffed = allocations.stream()
                .filter(a -> a.getSurplus() > 0)
                .sorted(Comparator.comparingInt(StaffAllocationDTO::getSurplus).reversed())
                .collect(Collectors.toList());
        
        // Find understaffed departments (shortage)
        List<StaffAllocationDTO> understaffed = allocations.stream()
                .filter(a -> a.getShortage() > 0)
                .sorted(Comparator.comparingInt(StaffAllocationDTO::getShortage).reversed())
                .collect(Collectors.toList());
        
        // Create reassignment suggestions
        int maxReassignments = Math.min(5, Math.min(overstaffed.size(), understaffed.size()));
        for (int i = 0; i < maxReassignments; i++) {
            StaffAllocationDTO from = overstaffed.get(i);
            StaffAllocationDTO to = understaffed.get(i);
            int employeesToMove = Math.min(from.getSurplus(), to.getShortage());
            
            if (employeesToMove > 0) {
                reassignments.add(EmployeeReassignmentDTO.builder()
                        .employeesToMove(employeesToMove)
                        .fromDepartment(from.getDepartment())
                        .toDepartment(to.getDepartment())
                        .reason(String.format("Move %d employees from overstaffed %s to understaffed %s", 
                                employeesToMove, from.getDepartment(), to.getDepartment()))
                        .build());
            }
        }
        
        return reassignments;
    }

    private List<OptimizationWarningDTO> generateWarnings(List<StaffAllocationDTO> allocations) {
        List<OptimizationWarningDTO> warnings = new ArrayList<>();
        
        for (StaffAllocationDTO allocation : allocations) {
            // Understaffed warnings
            if (allocation.getShortage() > 10) {
                warnings.add(OptimizationWarningDTO.builder()
                        .type("UNDERSTAFFED")
                        .message(String.format("%s is significantly understaffed by %d employees", 
                                allocation.getDepartment(), allocation.getShortage()))
                        .department(allocation.getDepartment())
                        .severity("high")
                        .build());
            } else if (allocation.getShortage() > 0) {
                warnings.add(OptimizationWarningDTO.builder()
                        .type("UNDERSTAFFED")
                        .message(String.format("%s needs %d more employees", 
                                allocation.getDepartment(), allocation.getShortage()))
                        .department(allocation.getDepartment())
                        .severity("medium")
                        .build());
            }
            
            // Overstaffed warnings
            if (allocation.getSurplus() > 10) {
                warnings.add(OptimizationWarningDTO.builder()
                        .type("OVERSTAFFED")
                        .message(String.format("%s has %d excess employees", 
                                allocation.getDepartment(), allocation.getSurplus()))
                        .department(allocation.getDepartment())
                        .severity("medium")
                        .build());
            }
        }
        
        return warnings;
    }

    private List<OptimizationRecommendationDTO> generateRecommendations(List<StaffAllocationDTO> allocations, 
                                                                          List<EmployeeReassignmentDTO> reassignments) {
        List<OptimizationRecommendationDTO> recommendations = new ArrayList<>();
        
        // Add recommendations based on allocations
        for (StaffAllocationDTO allocation : allocations) {
            if (allocation.getShortage() > 0) {
                recommendations.add(OptimizationRecommendationDTO.builder()
                        .action("INCREASE_STAFF")
                        .details(String.format("Increase %s staff by %d", allocation.getDepartment(), allocation.getShortage()))
                        .priority(allocation.getShortage() > 10 ? "high" : "medium")
                        .build());
            } else if (allocation.getSurplus() > 0) {
                recommendations.add(OptimizationRecommendationDTO.builder()
                        .action("REDUCE_STAFF")
                        .details(String.format("Reduce %s staff by %d or reassign", allocation.getDepartment(), allocation.getSurplus()))
                        .priority(allocation.getSurplus() > 10 ? "high" : "low")
                        .build());
            }
        }
        
        // Add reassignment recommendations
        for (EmployeeReassignmentDTO reassignment : reassignments) {
            recommendations.add(OptimizationRecommendationDTO.builder()
                    .action("REASSIGN_STAFF")
                    .details(reassignment.getReason())
                    .priority("medium")
                    .build());
        }
        
        // Add shift-specific recommendations
        recommendations.add(OptimizationRecommendationDTO.builder()
                .action("OPTIMIZE_SHIFTS")
                .details("Increase Night Shift coverage to meet demand")
                .priority("medium")
                .build());
        
        return recommendations.stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    private double calculateCurrentUtilization(List<StaffAllocationDTO> allocations) {
        double totalDemand = allocations.stream().mapToDouble(StaffAllocationDTO::getPredictedDemand).sum();
        double totalCurrent = allocations.stream().mapToInt(StaffAllocationDTO::getCurrentStaff).sum();
        return totalDemand > 0 ? (totalDemand / totalCurrent) * 100 : 0;
    }

    private double calculateOptimizedUtilization(List<StaffAllocationDTO> allocations) {
        double totalDemand = allocations.stream().mapToDouble(StaffAllocationDTO::getPredictedDemand).sum();
        double totalRecommended = allocations.stream().mapToInt(StaffAllocationDTO::getRecommendedStaff).sum();
        return totalRecommended > 0 ? (totalDemand / totalRecommended) * 100 : 0;
    }

    private int calculateOptimizationScore(List<StaffAllocationDTO> allocations, List<ShiftAssignmentDTO> shiftAssignments) {
        // Base score starts at 100
        int score = 100;
        
        // Deduct points for shortages
        int totalShortage = allocations.stream().mapToInt(StaffAllocationDTO::getShortage).sum();
        score -= Math.min(30, totalShortage / 2);
        
        // Deduct points for surpluses
        int totalSurplus = allocations.stream().mapToInt(StaffAllocationDTO::getSurplus).sum();
        score -= Math.min(20, totalSurplus / 3);
        
        // Deduct points for shift gaps
        int totalShiftGap = shiftAssignments.stream().mapToInt(s -> Math.abs(s.getGap())).sum();
        score -= Math.min(20, totalShiftGap / 5);
        
        // Ensure score is between 0 and 100
        return Math.max(0, Math.min(100, score));
    }

    public void applyRecommendation(OptimizationRecommendationDTO recommendation) {
        // In a real application, this would:
        // 1. Update the current staff data in the HR system
        // 2. Log the recommendation as applied
        // 3. Trigger notifications to relevant managers
        // 4. Update the optimization state
        
        // For this demo, we'll just log the application
        System.out.println("Applied recommendation: " + recommendation.getAction() + " - " + recommendation.getDetails());
        
        // If it's a reassignment, update the mock current staff
        if (recommendation.getAction().equals("REASSIGN_STAFF")) {
            // Parse the reason to extract from/to departments and count
            String details = recommendation.getDetails();
            if (details.contains("Move") && details.contains("from") && details.contains("to")) {
                // Extract department names and count (simplified parsing)
                // In real app, this would be more robust
            }
        }
    }

    public StaffAllocationDTO createAllocation(StaffAllocationDTO allocation) {
        // In a real application, this would:
        // 1. Validate the allocation data
        // 2. Save to the shift_staffing table
        // 3. Update the current staff counts
        // 4. Return the created allocation
        
        // For this demo, we'll just return the allocation as-is
        System.out.println("Created allocation: " + allocation.getDepartment() + " - " + allocation.getCurrentStaff());
        
        return allocation;
    }
}
