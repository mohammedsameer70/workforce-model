package com.boostphysioclinic.workforceapplication.service;

import com.boostphysioclinic.workforceapplication.Repository.PredictionResultRepository;
import com.boostphysioclinic.workforceapplication.Repository.PredictionRunRepository;
import com.boostphysioclinic.workforceapplication.dto.DashboardDataDTO;
import com.boostphysioclinic.workforceapplication.dto.entity.PredictionResult;
import com.boostphysioclinic.workforceapplication.dto.entity.PredictionRun;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

        private final PredictionResultRepository predictionResultRepository;
        private final PredictionRunRepository predictionRunRepository;

        // ============================================================
        // ALL PREDICTIONS
        // ============================================================

        public List<PredictionResult> getPredictions() {

                return predictionResultRepository.findAll();
        }

        // ============================================================
        // COMPLETE DASHBOARD DATA
        // ============================================================

        public DashboardDataDTO getDashboardData() {

                // --------------------------------------------------------
                // Latest prediction run
                // --------------------------------------------------------

                PredictionRun latestRun = predictionRunRepository
                                .findFirstByOrderByCreatedAtDesc();

                // --------------------------------------------------------
                // Summary
                // --------------------------------------------------------

                DashboardDataDTO.SummaryData summary = DashboardDataDTO.SummaryData.builder()

                                .model(
                                                latestRun != null
                                                                ? latestRun.getModelName()
                                                                : "No predictions yet")

                                .totalRecords(
                                                latestRun != null
                                                                ? latestRun.getTotalRecords().longValue()
                                                                : 0L)

                                .averagePrediction(
                                                latestRun != null
                                                                ? latestRun.getAveragePrediction()
                                                                : 0.0)

                                .maximumPrediction(
                                                latestRun != null
                                                                ? latestRun.getMaximumPrediction()
                                                                : 0.0)

                                .minimumPrediction(
                                                latestRun != null
                                                                ? latestRun.getMinimumPrediction()
                                                                : 0.0)

                                .build();

                // --------------------------------------------------------
                // Department performance
                // --------------------------------------------------------

                List<Object[]> deptResults = latestRun != null
                                ? predictionResultRepository.findAveragePredictionByDepartmentForRun(latestRun.getId())
                                : new ArrayList<>();

                List<DashboardDataDTO.DepartmentData> departments = new ArrayList<>();

                for (Object[] result : deptResults) {

                        if (result == null || result.length < 2) {
                                continue;
                        }

                        String department = result[0] != null
                                        ? result[0].toString()
                                        : "Unknown";

                        Double averagePrediction = result[1] != null
                                        ? ((Number) result[1]).doubleValue()
                                        : 0.0;

                        departments.add(
                                        DashboardDataDTO.DepartmentData.builder()

                                                        .department(department)

                                                        .averagePrediction(
                                                                        averagePrediction)

                                                        .build());
                }

                // --------------------------------------------------------
                // Prediction trend
                // --------------------------------------------------------

                List<Object[]> trendResults = latestRun != null
                                ? predictionResultRepository.findPredictionTrendByDateForRun(latestRun.getId())
                                : new ArrayList<>();

                List<DashboardDataDTO.TrendData> trend = new ArrayList<>();

                for (Object[] result : trendResults) {

                        if (result == null || result.length < 2) {
                                continue;
                        }

                        String date = null;

                        if (result[0] instanceof LocalDate) {

                                date = result[0].toString();

                        } else if (result[0] != null) {

                                date = result[0].toString();
                        }

                        Double predictedDemand = result[1] != null
                                        ? ((Number) result[1]).doubleValue()
                                        : 0.0;

                        if (date != null) {

                                trend.add(
                                                DashboardDataDTO.TrendData.builder()

                                                                .date(date)

                                                                .predictedDemand(
                                                                                predictedDemand)

                                                                .build());
                        }
                }

                // --------------------------------------------------------
                // Recent predictions
                // --------------------------------------------------------

                List<PredictionResult> recentResults = latestRun != null
                                ? predictionResultRepository.findTop50ByPredictionRunIdOrderByAttendanceDateDescIdDesc(
                                                latestRun.getId())
                                : new ArrayList<>();

                List<DashboardDataDTO.RecentPredictionData> recentPredictions = new ArrayList<>();

                for (PredictionResult result : recentResults) {

                        if (result == null) {
                                continue;
                        }

                        String date = result.getAttendanceDate() != null
                                        ? result.getAttendanceDate().toString()
                                        : null;

                        recentPredictions.add(

                                        DashboardDataDTO.RecentPredictionData.builder()

                                                        .id(result.getId())

                                                        .date(date)

                                                        .department(
                                                                        result.getDepartment())

                                                        .actualDemand(
                                                                        result.getActualDemand())

                                                        .predictedDemand(
                                                                        result.getPredictedDemand())

                                                        .build());
                }

                // --------------------------------------------------------
                // Return complete dashboard
                // --------------------------------------------------------

                return DashboardDataDTO.builder()

                                .summary(summary)

                                .departments(departments)

                                .trend(trend)

                                .recentPredictions(
                                                recentPredictions)

                                .build();
        }
}