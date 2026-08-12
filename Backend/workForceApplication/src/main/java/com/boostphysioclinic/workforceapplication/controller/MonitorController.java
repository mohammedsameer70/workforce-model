package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.dto.entity.ServiceHealth;
import com.boostphysioclinic.workforceapplication.dto.entity.MonitoringMetric;
import com.boostphysioclinic.workforceapplication.service.MonitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monitor")
@RequiredArgsConstructor
public class MonitorController {

    private final MonitorService monitorService;

    @GetMapping("/service-health")
    public List<ServiceHealth> getAllServiceHealth() {
        return monitorService.getAllServiceHealth();
    }

    @GetMapping("/service-health/service/{serviceName}")
    public ResponseEntity<ServiceHealth> getServiceHealthByServiceName(@PathVariable String serviceName) {
        return monitorService.getServiceHealthByServiceName(serviceName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/service-health")
    public ServiceHealth createServiceHealth(@RequestBody ServiceHealth health) {
        return monitorService.createServiceHealth(health);
    }

    @PutMapping("/service-health/{id}")
    public ServiceHealth updateServiceHealth(@PathVariable Long id, @RequestBody ServiceHealth health) {
        return monitorService.updateServiceHealth(id, health);
    }

    @GetMapping("/monitoring-metrics")
    public List<MonitoringMetric> getAllMonitoringMetrics() {
        return monitorService.getAllMonitoringMetrics();
    }

    @GetMapping("/monitoring-metrics/service/{serviceName}")
    public List<MonitoringMetric> getMonitoringMetricsByServiceName(@PathVariable String serviceName) {
        return monitorService.getMonitoringMetricsByServiceName(serviceName);
    }

    @GetMapping("/monitoring-metrics/category/{category}")
    public List<MonitoringMetric> getMonitoringMetricsByCategory(@PathVariable String category) {
        return monitorService.getMonitoringMetricsByCategory(category);
    }

    @PostMapping("/monitoring-metrics")
    public MonitoringMetric createMonitoringMetric(@RequestBody MonitoringMetric metric) {
        return monitorService.createMonitoringMetric(metric);
    }
}
