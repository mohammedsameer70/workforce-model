package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.dto.entity.MicroserviceHealth;
import com.boostphysioclinic.workforceapplication.service.MicroserviceHealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/microservice-health")
@RequiredArgsConstructor
public class MicroserviceHealthController {

    private final MicroserviceHealthService microserviceHealthService;

    @GetMapping
    public List<MicroserviceHealth> getAllMicroserviceHealth() {
        return microserviceHealthService.getAllMicroserviceHealth();
    }

    @GetMapping("/service/{serviceName}")
    public ResponseEntity<MicroserviceHealth> getByServiceName(@PathVariable String serviceName) {
        return microserviceHealthService.getByServiceName(serviceName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MicroserviceHealth> getById(@PathVariable Long id) {
        return microserviceHealthService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public MicroserviceHealth createMicroserviceHealth(@RequestBody MicroserviceHealth health) {
        return microserviceHealthService.createMicroserviceHealth(health);
    }

    @PutMapping("/{id}")
    public MicroserviceHealth updateMicroserviceHealth(@PathVariable Long id, @RequestBody MicroserviceHealth health) {
        return microserviceHealthService.updateMicroserviceHealth(id, health);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMicroserviceHealth(@PathVariable Long id) {
        microserviceHealthService.deleteMicroserviceHealth(id);
        return ResponseEntity.noContent().build();
    }
}
