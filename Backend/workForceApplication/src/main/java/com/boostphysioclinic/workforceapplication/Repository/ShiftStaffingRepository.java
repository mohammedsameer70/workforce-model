package com.boostphysioclinic.workforceapplication.Repository;

import com.boostphysioclinic.workforceapplication.dto.entity.ShiftStaffing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftStaffingRepository extends JpaRepository<ShiftStaffing, Long> {
    List<ShiftStaffing> findByDepartment(String department);
    List<ShiftStaffing> findByShift(String shift);
}
