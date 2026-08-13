package com.boostphysioclinic.workforceapplication.Repository;

import com.boostphysioclinic.workforceapplication.dto.entity.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DatasetRepository extends JpaRepository<Dataset, Long> {
    List<Dataset> findByStatus(String status);
    Optional<Dataset> findByName(String name);
}
