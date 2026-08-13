package com.boostphysioclinic.workforceapplication.Repository;

import com.boostphysioclinic.workforceapplication.dto.entity.WeeklyForecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeeklyForecastRepository extends JpaRepository<WeeklyForecast, Long> {
    List<WeeklyForecast> findByDepartment(String department);
}
