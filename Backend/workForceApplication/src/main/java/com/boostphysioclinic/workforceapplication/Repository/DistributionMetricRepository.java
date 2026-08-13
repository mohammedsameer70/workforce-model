package com.boostphysioclinic.workforceapplication.Repository;

import com.boostphysioclinic.workforceapplication.dto.entity.DistributionMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistributionMetricRepository extends JpaRepository<DistributionMetric, Long> {
    List<DistributionMetric> findByMetricType(String metricType);
    List<DistributionMetric> findByDepartment(String department);
}
