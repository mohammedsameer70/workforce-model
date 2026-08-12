package com.boostphysioclinic.workforceapplication.service;

import com.boostphysioclinic.workforceapplication.Repository.ServiceHealthRepository;
import com.boostphysioclinic.workforceapplication.Repository.MonitoringMetricRepository;
import com.boostphysioclinic.workforceapplication.dto.entity.ServiceHealth;
import com.boostphysioclinic.workforceapplication.dto.entity.MonitoringMetric;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MonitorService {

    private final ServiceHealthRepository serviceHealthRepository;
    private final MonitoringMetricRepository monitoringMetricRepository;

    public List<ServiceHealth> getAllServiceHealth() {
        return serviceHealthRepository.findAll();
    }

    public Optional<ServiceHealth> getServiceHealthByServiceName(String serviceName) {
        return serviceHealthRepository.findByServiceName(serviceName);
    }

    public ServiceHealth createServiceHealth(ServiceHealth health) {
        return serviceHealthRepository.save(health);
    }

    public ServiceHealth updateServiceHealth(Long id, ServiceHealth health) {
        health.setId(id);
        return serviceHealthRepository.save(health);
    }

    public List<MonitoringMetric> getAllMonitoringMetrics() {
        return monitoringMetricRepository.findAll();
    }

    public List<MonitoringMetric> getMonitoringMetricsByServiceName(String serviceName) {
        return monitoringMetricRepository.findByServiceName(serviceName);
    }

    public List<MonitoringMetric> getMonitoringMetricsByCategory(String category) {
        return monitoringMetricRepository.findByCategory(category);
    }

    public MonitoringMetric createMonitoringMetric(MonitoringMetric metric) {
        return monitoringMetricRepository.save(metric);
    }
}
