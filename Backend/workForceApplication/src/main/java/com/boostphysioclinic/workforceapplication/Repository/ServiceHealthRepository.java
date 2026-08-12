package com.boostphysioclinic.workforceapplication.Repository;

import com.boostphysioclinic.workforceapplication.dto.entity.ServiceHealth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceHealthRepository extends JpaRepository<ServiceHealth, Long> {
    List<ServiceHealth> findAll();
    Optional<ServiceHealth> findByServiceName(String serviceName);
}
