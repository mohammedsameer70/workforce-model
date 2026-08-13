package com.boostphysioclinic.workforceapplication.Repository;

import com.boostphysioclinic.workforceapplication.dto.entity.VersionPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VersionPerformanceRepository extends JpaRepository<VersionPerformance, Long> {
    List<VersionPerformance> findByServiceName(String serviceName);
    List<VersionPerformance> findByVersion(String version);
}
