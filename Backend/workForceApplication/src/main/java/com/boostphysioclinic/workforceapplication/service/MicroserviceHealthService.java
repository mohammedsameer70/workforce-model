package com.boostphysioclinic.workforceapplication.service;

import com.boostphysioclinic.workforceapplication.Repository.MicroserviceHealthRepository;
import com.boostphysioclinic.workforceapplication.dto.entity.MicroserviceHealth;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MicroserviceHealthService {

    private final MicroserviceHealthRepository microserviceHealthRepository;
    public Optional<MicroserviceHealth> getByServiceName(String serviceName) {
        return microserviceHealthRepository.findByServiceName(serviceName);
    }

    public List<MicroserviceHealth> getAllMicroserviceHealth() {
        return microserviceHealthRepository.findAll();
    }

    public Optional<MicroserviceHealth> getServiceHealth(String serviceName) {
        return microserviceHealthRepository.findByServiceName(serviceName);
    }

    public Optional<MicroserviceHealth> getById(Long id) {
        return microserviceHealthRepository.findById(id);
    }

    public MicroserviceHealth createMicroserviceHealth(MicroserviceHealth health) {
        return microserviceHealthRepository.save(health);
    }

    public MicroserviceHealth updateMicroserviceHealth(Long id, MicroserviceHealth health) {
        health.setId(id);
        return microserviceHealthRepository.save(health);
    }

    public void deleteMicroserviceHealth(Long id) {
        microserviceHealthRepository.deleteById(id);
    }


}
