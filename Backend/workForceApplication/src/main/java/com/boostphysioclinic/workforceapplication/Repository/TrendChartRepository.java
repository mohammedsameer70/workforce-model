package com.boostphysioclinic.workforceapplication.Repository;

import com.boostphysioclinic.workforceapplication.dto.entity.TrendChart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrendChartRepository extends JpaRepository<TrendChart, Long> {
    List<TrendChart> findByTrendType(String trendType);
}
