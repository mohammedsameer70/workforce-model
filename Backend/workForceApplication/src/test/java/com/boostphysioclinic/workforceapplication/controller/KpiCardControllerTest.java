package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.dto.entity.KpiCard;
import com.boostphysioclinic.workforceapplication.service.KpiCardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class KpiCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private KpiCardService kpiCardService;

    private KpiCard testKpiCard;

    @BeforeEach
    void setUp() {
        testKpiCard = createMockKpiCard();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetAllKpiCards() throws Exception {
        List<KpiCard> cards = List.of(testKpiCard);
        when(kpiCardService.getAllKpiCards()).thenReturn(cards);

        mockMvc.perform(get("/api/kpi-cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetKpiCardsByCategory() throws Exception {
        List<KpiCard> cards = List.of(testKpiCard);
        when(kpiCardService.getKpiCardsByCategory("Performance")).thenReturn(cards);

        mockMvc.perform(get("/api/kpi-cards/category/Performance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetKpiCardByIdFound() throws Exception {
        when(kpiCardService.getKpiCardById(1L)).thenReturn(Optional.of(testKpiCard));

        mockMvc.perform(get("/api/kpi-cards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetKpiCardByIdNotFound() throws Exception {
        when(kpiCardService.getKpiCardById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/kpi-cards/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testCreateKpiCard() throws Exception {
        when(kpiCardService.createKpiCard(any(KpiCard.class))).thenReturn(testKpiCard);

        mockMvc.perform(post("/api/kpi-cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testKpiCard)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Total Employees"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testUpdateKpiCard() throws Exception {
        when(kpiCardService.updateKpiCard(anyLong(), any(KpiCard.class))).thenReturn(testKpiCard);

        mockMvc.perform(put("/api/kpi-cards/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testKpiCard)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testDeleteKpiCard() throws Exception {
        mockMvc.perform(delete("/api/kpi-cards/1"))
                .andExpect(status().isNoContent());
    }

    private KpiCard createMockKpiCard() {
        KpiCard card = new KpiCard();
        card.setId(1L);
        card.setTitle("Total Employees");
        card.setValue("150");
        card.setCategory("Performance");
        card.setTrend("+5%");
        return card;
    }
}
