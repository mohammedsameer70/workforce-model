package com.boostphysioclinic.workforceapplication.Repository;

import com.boostphysioclinic.workforceapplication.dto.entity.PredictionRun;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PredictionRunRepository extends JpaRepository<PredictionRun, Long> {

    PredictionRun findFirstByOrderByCreatedAtDesc();
}