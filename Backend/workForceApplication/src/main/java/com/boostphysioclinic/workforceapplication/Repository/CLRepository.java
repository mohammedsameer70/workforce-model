package com.boostphysioclinic.workforceapplication.Repository;

import com.boostphysioclinic.workforceapplication.model.EmployeeDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CLRepository extends JpaRepository<EmployeeDTO,Long> {

}
