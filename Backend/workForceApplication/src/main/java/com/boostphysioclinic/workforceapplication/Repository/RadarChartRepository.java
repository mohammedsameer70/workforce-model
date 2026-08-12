package com.boostphysioclinic.workforceapplication.Repository;

import com.boostphysioclinic.workforceapplication.dto.entity.RadarChart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RadarChartRepository extends JpaRepository<RadarChart, Long> {
    List<RadarChart> findByDepartment(String department);
}
