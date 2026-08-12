package com.boostphysioclinic.workforceapplication.Repository;

import com.boostphysioclinic.workforceapplication.dto.entity.MicroserviceHealth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MicroserviceHealthRepository extends JpaRepository<MicroserviceHealth, Long> {
    List<MicroserviceHealth> findAll();
    Optional<MicroserviceHealth> findByServiceName(String serviceName);
}
