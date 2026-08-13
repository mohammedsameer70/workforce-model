package com.boostphysioclinic.workforceapplication.Repository;

import com.boostphysioclinic.workforceapplication.dto.entity.EmployeeAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeAttendanceRepository extends JpaRepository<EmployeeAttendance, Long> {
    List<EmployeeAttendance> findByEmployeeId(Long employeeId);
    List<EmployeeAttendance> findByStatus(String status);
}
