package com.boostphysioclinic.workforceapplication.Repository;

import com.boostphysioclinic.workforceapplication.dto.entity.StaffingHeatmap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffingHeatmapRepository extends JpaRepository<StaffingHeatmap, Long> {
    List<StaffingHeatmap> findByDepartment(String department);
}
