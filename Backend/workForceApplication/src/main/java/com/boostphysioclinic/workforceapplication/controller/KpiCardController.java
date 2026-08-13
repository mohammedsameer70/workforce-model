package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.dto.entity.KpiCard;
import com.boostphysioclinic.workforceapplication.service.KpiCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kpi-cards")
@RequiredArgsConstructor
public class KpiCardController {

    private final KpiCardService kpiCardService;

    @GetMapping
    public List<KpiCard> getAllKpiCards() {
        return kpiCardService.getAllKpiCards();
    }

    @GetMapping("/category/{category}")
    public List<KpiCard> getKpiCardsByCategory(@PathVariable String category) {
        return kpiCardService.getKpiCardsByCategory(category);
    }

    @GetMapping("/{id}")
    public ResponseEntity<KpiCard> getKpiCardById(@PathVariable Long id) {
        return kpiCardService.getKpiCardById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public KpiCard createKpiCard(@RequestBody KpiCard kpiCard) {
        return kpiCardService.createKpiCard(kpiCard);
    }

    @PutMapping("/{id}")
    public KpiCard updateKpiCard(@PathVariable Long id, @RequestBody KpiCard kpiCard) {
        return kpiCardService.updateKpiCard(id, kpiCard);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKpiCard(@PathVariable Long id) {
        kpiCardService.deleteKpiCard(id);
        return ResponseEntity.noContent().build();
    }
}
