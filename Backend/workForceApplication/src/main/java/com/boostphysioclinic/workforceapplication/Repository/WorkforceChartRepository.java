


package com.boostphysioclinic.workforceapplication.Repository;

import com.boostphysioclinic.workforceapplication.dto.entity.WorkforceChart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkforceChartRepository extends JpaRepository<WorkforceChart, Long> {
    List<WorkforceChart> findByDepartment(String department);
}
