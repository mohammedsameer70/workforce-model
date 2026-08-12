package com.boostphysioclinic.workforceapplication.Repository;

import com.boostphysioclinic.workforceapplication.dto.entity.PredictionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PredictionResultRepository
        extends JpaRepository<PredictionResult, Long> {

    // ============================================================
    // DEPARTMENT PERFORMANCE
    // ============================================================

    @Query("""
            SELECT
                pr.department,
                AVG(pr.predictedDemand)
            FROM PredictionResult pr
            WHERE pr.department IS NOT NULL
              AND pr.predictionRun.id = :predictionRunId
            GROUP BY pr.department
            ORDER BY AVG(pr.predictedDemand) DESC
            """)
    List<Object[]> findAveragePredictionByDepartmentForRun(Long predictionRunId);

    @Query("""
            SELECT
                pr.department,
                AVG(pr.predictedDemand)
            FROM PredictionResult pr
            WHERE pr.department IS NOT NULL
            GROUP BY pr.department
            ORDER BY AVG(pr.predictedDemand) DESC
            """)
    List<Object[]> findAveragePredictionByDepartment();

    @Query("""
            SELECT
                pr.attendanceDate,
                AVG(pr.predictedDemand)
            FROM PredictionResult pr
            WHERE pr.attendanceDate IS NOT NULL
              AND pr.predictionRun.id = :predictionRunId
            GROUP BY pr.attendanceDate
            ORDER BY pr.attendanceDate ASC
            """)
    List<Object[]> findPredictionTrendByDateForRun(Long predictionRunId);

    // ============================================================
    // PREDICTION TREND
    // ============================================================

    @Query("""
            SELECT
                pr.attendanceDate,
                AVG(pr.predictedDemand)
            FROM PredictionResult pr
            WHERE pr.attendanceDate IS NOT NULL
            GROUP BY pr.attendanceDate
            ORDER BY pr.attendanceDate ASC
            """)
    List<Object[]> findPredictionTrendByDate();

    // ============================================================
    // RECENT PREDICTIONS
    // ============================================================

    @Query("""
            SELECT pr
            FROM PredictionResult pr
            WHERE pr.predictionRun.id = (
                SELECT MAX(run.id)
                FROM PredictionRun run
            )
            ORDER BY pr.attendanceDate DESC, pr.id DESC
            """)
    List<PredictionResult> findRecentPredictions();

    List<PredictionResult> findTop50ByPredictionRunIdOrderByAttendanceDateDescIdDesc(Long predictionRunId);
}