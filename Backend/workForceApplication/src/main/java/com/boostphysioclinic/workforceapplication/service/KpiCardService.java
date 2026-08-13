package com.boostphysioclinic.workforceapplication.service;

import com.boostphysioclinic.workforceapplication.Repository.KpiCardRepository;
import com.boostphysioclinic.workforceapplication.dto.entity.KpiCard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KpiCardService {

    private final KpiCardRepository kpiCardRepository;

    public List<KpiCard> getAllKpiCards() {
        return kpiCardRepository.findAll();
    }

    public List<KpiCard> getKpiCardsByCategory(String category) {
        return kpiCardRepository.findByCategory(category);
    }

    public java.util.Optional<KpiCard> getKpiCardById(Long id) {
        return kpiCardRepository.findById(id);
    }

    public KpiCard createKpiCard(KpiCard kpiCard) {
        return kpiCardRepository.save(kpiCard);
    }

    public KpiCard updateKpiCard(Long id, KpiCard kpiCard) {
        kpiCard.setId(id);
        return kpiCardRepository.save(kpiCard);
    }

    public void deleteKpiCard(Long id) {
        kpiCardRepository.deleteById(id);
    }
}
