package com.boostphysioclinic.workforceapplication.Repository;

import com.boostphysioclinic.workforceapplication.dto.entity.ExperimentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperimentLogRepository extends JpaRepository<ExperimentLog, Long> {
    List<ExperimentLog> findByStatus(String status);
}
