package com.boostphysioclinic.workforceapplication.Repository;

import com.boostphysioclinic.workforceapplication.dto.entity.ThroughputMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThroughputMetricRepository extends JpaRepository<ThroughputMetric, Long> {
    List<ThroughputMetric> findByDepartment(String department);
}
