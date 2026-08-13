package com.boostphysioclinic.workforceapplication.Repository;

import com.boostphysioclinic.workforceapplication.dto.entity.LatencyMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LatencyMetricRepository extends JpaRepository<LatencyMetric, Long> {
    List<LatencyMetric> findByEndpoint(String endpoint);
    List<LatencyMetric> findByLoadLevel(String loadLevel);
}
