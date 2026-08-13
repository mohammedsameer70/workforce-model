package com.boostphysioclinic.workforceapplication.Repository;

import com.boostphysioclinic.workforceapplication.dto.entity.DataComparison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataComparisonRepository extends JpaRepository<DataComparison, Long> {
    List<DataComparison> findByDataSource(String dataSource);
}
