package com.boostphysioclinic.workforceapplication.Repository;

import com.boostphysioclinic.workforceapplication.dto.entity.MonitoringMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonitoringMetricRepository extends JpaRepository<MonitoringMetric, Long> {
    List<MonitoringMetric> findByServiceName(String serviceName);
    List<MonitoringMetric> findByCategory(String category);
}
