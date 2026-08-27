package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.Repository.SettingsRepository;
import com.boostphysioclinic.workforceapplication.dto.entity.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    @Autowired
    private SettingsRepository settingsRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getSettings() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Settings> settingsOpt = settingsRepository.findFirstByOrderByIdDesc();
            
            if (settingsOpt.isPresent()) {
                Settings settings = settingsOpt.get();
                response.put("profile", Map.of(
                    "fullName", settings.getFullName() != null ? settings.getFullName() : "",
                    "email", settings.getEmail() != null ? settings.getEmail() : "",
                    "role", settings.getRole() != null ? settings.getRole() : "",
                    "department", settings.getDepartment() != null ? settings.getDepartment() : ""
                ));
                response.put("appearance", Map.of(
                    "darkMode", settings.getDarkMode() != null ? settings.getDarkMode() : false,
                    "compactView", settings.getCompactView() != null ? settings.getCompactView() : false,
                    "animations", settings.getAnimations() != null ? settings.getAnimations() : false
                ));
                response.put("notifications", Map.of(
                    "criticalAlerts", settings.getCriticalAlerts() != null ? settings.getCriticalAlerts() : false,
                    "shiftRecommendations", settings.getShiftRecommendations() != null ? settings.getShiftRecommendations() : false,
                    "systemMonitoring", settings.getSystemMonitoring() != null ? settings.getSystemMonitoring() : false,
                    "emailDigest", settings.getEmailDigest() != null ? settings.getEmailDigest() : false
                ));
                response.put("config", Map.of(
                    "model", settings.getModel() != null ? settings.getModel() : "",
                    "refresh", settings.getRefresh() != null ? settings.getRefresh() : "",
                    "apiUrl", settings.getApiUrl() != null ? settings.getApiUrl() : "",
                    "mlUrl", settings.getMlUrl() != null ? settings.getMlUrl() : "",
                    "dataRetention", settings.getDataRetention() != null ? settings.getDataRetention() : 90
                ));
                response.put("aiModel", Map.of(
                    "activeModel", settings.getActiveModel() != null ? settings.getActiveModel() : "",
                    "version", settings.getModelVersion() != null ? settings.getModelVersion() : "",
                    "trainingFrequency", settings.getTrainingFrequency() != null ? settings.getTrainingFrequency() : "",
                    "confidenceThreshold", settings.getConfidenceThreshold() != null ? settings.getConfidenceThreshold() : 75,
                    "autoRetrain", settings.getAutoRetrain() != null ? settings.getAutoRetrain() : false,
                    "monitoring", settings.getMonitoring() != null ? settings.getMonitoring() : true,
                    "featureImportance", settings.getFeatureImportance() != null ? settings.getFeatureImportance() : true
                ));
                return ResponseEntity.ok(response);
            } else {
                // Return default settings if none exist
                response.put("profile", Map.of(
                    "fullName", "",
                    "email", "",
                    "role", "",
                    "department", ""
                ));
                response.put("appearance", Map.of(
                    "darkMode", false,
                    "compactView", false,
                    "animations", false
                ));
                response.put("notifications", Map.of(
                    "criticalAlerts", false,
                    "shiftRecommendations", false,
                    "systemMonitoring", false,
                    "emailDigest", false
                ));
                response.put("config", Map.of(
                    "model", "",
                    "refresh", "",
                    "apiUrl", "",
                    "mlUrl", "",
                    "dataRetention", 90
                ));
                response.put("aiModel", Map.of(
                    "activeModel", "",
                    "version", "",
                    "trainingFrequency", "",
                    "confidenceThreshold", 75,
                    "autoRetrain", false,
                    "monitoring", true,
                    "featureImportance", true
                ));
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("error", "Failed to load settings: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> saveSettings(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Settings> settingsOpt = settingsRepository.findFirstByOrderByIdDesc();
            Settings settings;
            
            if (settingsOpt.isPresent()) {
                settings = settingsOpt.get();
            } else {
                settings = new Settings();
            }
            
            // Extract profile settings
            Map<String, Object> profile = (Map<String, Object>) payload.get("profile");
            if (profile != null) {
                settings.setFullName((String) profile.get("fullName"));
                settings.setEmail((String) profile.get("email"));
                settings.setRole((String) profile.get("role"));
                settings.setDepartment((String) profile.get("department"));
            }
            
            // Extract appearance settings
            Map<String, Object> appearance = (Map<String, Object>) payload.get("appearance");
            if (appearance != null) {
                settings.setDarkMode((Boolean) appearance.get("darkMode"));
                settings.setCompactView((Boolean) appearance.get("compactView"));
                settings.setAnimations((Boolean) appearance.get("animations"));
            }
            
            // Extract notification settings
            Map<String, Object> notifications = (Map<String, Object>) payload.get("notifications");
            if (notifications != null) {
                settings.setCriticalAlerts((Boolean) notifications.get("criticalAlerts"));
                settings.setShiftRecommendations((Boolean) notifications.get("shiftRecommendations"));
                settings.setSystemMonitoring((Boolean) notifications.get("systemMonitoring"));
                settings.setEmailDigest((Boolean) notifications.get("emailDigest"));
            }
            
            // Extract config settings
            Map<String, Object> config = (Map<String, Object>) payload.get("config");
            if (config != null) {
                settings.setModel((String) config.get("model"));
                settings.setRefresh((String) config.get("refresh"));
                settings.setApiUrl((String) config.get("apiUrl"));
                settings.setMlUrl((String) config.get("mlUrl"));
                settings.setDataRetention(config.get("dataRetention") != null ? ((Number) config.get("dataRetention")).intValue() : 90);
            }

            // Extract AI Model settings
            Map<String, Object> aiModel = (Map<String, Object>) payload.get("aiModel");
            if (aiModel != null) {
                settings.setActiveModel((String) aiModel.get("activeModel"));
                settings.setModelVersion((String) aiModel.get("version"));
                settings.setTrainingFrequency((String) aiModel.get("trainingFrequency"));
                settings.setConfidenceThreshold(aiModel.get("confidenceThreshold") != null ? ((Number) aiModel.get("confidenceThreshold")).intValue() : 75);
                settings.setAutoRetrain((Boolean) aiModel.get("autoRetrain"));
                settings.setMonitoring((Boolean) aiModel.get("monitoring"));
                settings.setFeatureImportance((Boolean) aiModel.get("featureImportance"));
            }

            settingsRepository.save(settings);
            
            response.put("success", true);
            response.put("message", "Settings saved successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to save settings: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
