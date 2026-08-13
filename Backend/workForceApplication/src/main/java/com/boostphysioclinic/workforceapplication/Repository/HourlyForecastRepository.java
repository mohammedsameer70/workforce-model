package com.boostphysioclinic.workforceapplication.Repository;

import com.boostphysioclinic.workforceapplication.dto.entity.HourlyForecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HourlyForecastRepository extends JpaRepository<HourlyForecast, Long> {
    List<HourlyForecast> findByDepartment(String department);
}
