package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.Repository.AlertRepository;
import com.boostphysioclinic.workforceapplication.dto.entity.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AlertController {

    @Autowired
    private AlertRepository alertRepository;

    @GetMapping("/alerts")
    public List<Alert> getAlerts(@RequestParam(defaultValue = "20") int limit) {
        List<Alert> alerts = alertRepository.findAll();
        return alerts.stream()
                .limit(limit)
                .toList();
    }
}
