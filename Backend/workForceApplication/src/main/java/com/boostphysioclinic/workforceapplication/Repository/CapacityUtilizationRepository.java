package com.boostphysioclinic.workforceapplication.Repository;

import com.boostphysioclinic.workforceapplication.dto.entity.CapacityUtilization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CapacityUtilizationRepository extends JpaRepository<CapacityUtilization, Long> {
    List<CapacityUtilization> findByDepartment(String department);
}
