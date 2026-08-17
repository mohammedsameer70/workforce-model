package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.dto.ForecastTrendDTO;
import com.boostphysioclinic.workforceapplication.dto.ForecastMetricDTO;
import com.boostphysioclinic.workforceapplication.dto.StaffingHeatmapDTO;
import com.boostphysioclinic.workforceapplication.dto.WeeklyForecastDTO;
import com.boostphysioclinic.workforceapplication.dto.entity.HourlyForecast;
import com.boostphysioclinic.workforceapplication.dto.entity.RadarChart;
import com.boostphysioclinic.workforceapplication.dto.entity.StaffingHeatmap;
import com.boostphysioclinic.workforceapplication.dto.entity.WeeklyForecast;
import com.boostphysioclinic.workforceapplication.service.ForecastingService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ForecastingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ForecastingService forecastingService;

    private HourlyForecast testHourlyForecast;
    private WeeklyForecast testWeeklyForecast;
    private RadarChart testRadarChart;
    private StaffingHeatmap testStaffingHeatmap;

    @BeforeEach
    void setUp() {
        testHourlyForecast = createMockHourlyForecast();
        testWeeklyForecast = createMockWeeklyForecast();
        testRadarChart = createMockRadarChart();
        testStaffingHeatmap = createMockStaffingHeatmap();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetAllHourlyForecasts() throws Exception {
        List<ForecastTrendDTO> forecasts = List.of(new ForecastTrendDTO());
        when(forecastingService.getHourlyForecastDTO()).thenReturn(forecasts);

        mockMvc.perform(get("/api/forecasting/hourly"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetHourlyForecastsByDepartment() throws Exception {
        List<HourlyForecast> forecasts = List.of(testHourlyForecast);
        when(forecastingService.getHourlyForecastsByDepartment("IT")).thenReturn(forecasts);

        mockMvc.perform(get("/api/forecasting/hourly/department/IT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testCreateHourlyForecast() throws Exception {
        when(forecastingService.createHourlyForecast(any(HourlyForecast.class))).thenReturn(testHourlyForecast);

        mockMvc.perform(post("/api/forecasting/hourly")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testHourlyForecast)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetAllWeeklyForecasts() throws Exception {
        List<WeeklyForecastDTO> forecasts = List.of(new WeeklyForecastDTO());
        when(forecastingService.getWeeklyForecastDTO()).thenReturn(forecasts);

        mockMvc.perform(get("/api/forecasting/weekly"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetWeeklyForecastsByDepartment() throws Exception {
        List<WeeklyForecast> forecasts = List.of(testWeeklyForecast);
        when(forecastingService.getWeeklyForecastsByDepartment("IT")).thenReturn(forecasts);

        mockMvc.perform(get("/api/forecasting/weekly/department/IT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testCreateWeeklyForecast() throws Exception {
        when(forecastingService.createWeeklyForecast(any(WeeklyForecast.class))).thenReturn(testWeeklyForecast);

        mockMvc.perform(post("/api/forecasting/weekly")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testWeeklyForecast)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetAllRadarCharts() throws Exception {
        List<ForecastMetricDTO> charts = List.of(new ForecastMetricDTO());
        when(forecastingService.getForecastMetricsDTO()).thenReturn(charts);

        mockMvc.perform(get("/api/forecasting/radar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetRadarChartsByDepartment() throws Exception {
        List<RadarChart> charts = List.of(testRadarChart);
        when(forecastingService.getRadarChartsByDepartment("IT")).thenReturn(charts);

        mockMvc.perform(get("/api/forecasting/radar/department/IT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testCreateRadarChart() throws Exception {
        when(forecastingService.createRadarChart(any(RadarChart.class))).thenReturn(testRadarChart);

        mockMvc.perform(post("/api/forecasting/radar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRadarChart)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetAllStaffingHeatmaps() throws Exception {
        List<StaffingHeatmapDTO> heatmaps = List.of(new StaffingHeatmapDTO());
        when(forecastingService.getStaffingHeatmapData()).thenReturn(heatmaps);

        mockMvc.perform(get("/api/forecasting/staffing-heatmap"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetStaffingHeatmapsByDepartment() throws Exception {
        List<StaffingHeatmap> heatmaps = List.of(testStaffingHeatmap);
        when(forecastingService.getStaffingHeatmapsByDepartment("IT")).thenReturn(heatmaps);

        mockMvc.perform(get("/api/forecasting/staffing-heatmap/department/IT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testCreateStaffingHeatmap() throws Exception {
        when(forecastingService.createStaffingHeatmap(any(StaffingHeatmap.class))).thenReturn(testStaffingHeatmap);

        mockMvc.perform(post("/api/forecasting/staffing-heatmap")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testStaffingHeatmap)))
                .andExpect(status().isOk());
    }

    private HourlyForecast createMockHourlyForecast() {
        HourlyForecast forecast = new HourlyForecast();
        forecast.setId(1L);
        forecast.setDepartment("IT");
        forecast.setHour("09:00");
        forecast.setPredictedDemand(10.0);
        forecast.setConfidenceInterval(0.9);
        forecast.setForecastDate(java.time.LocalDateTime.now());
        return forecast;
    }

    private WeeklyForecast createMockWeeklyForecast() {
        WeeklyForecast forecast = new WeeklyForecast();
        forecast.setId(1L);
        forecast.setDepartment("IT");
        forecast.setDayOfWeek("Monday");
        forecast.setPredictedDemand(50.0);
        forecast.setActualDemand(48.0);
        forecast.setVariance(2.0);
        forecast.setWeekStartDate(java.time.LocalDateTime.now());
        return forecast;
    }

    private RadarChart createMockRadarChart() {
        RadarChart chart = new RadarChart();
        chart.setId(1L);
        chart.setDepartment("IT");
        chart.setMetric("Performance");
        chart.setValue(85.0);
        chart.setCategory("Efficiency");
        chart.setCreatedAt(java.time.LocalDateTime.now());
        return chart;
    }

    private StaffingHeatmap createMockStaffingHeatmap() {
        StaffingHeatmap heatmap = new StaffingHeatmap();
        heatmap.setId(1L);
        heatmap.setDepartment("IT");
        heatmap.setDayOfWeek("Monday");
        heatmap.setTimeSlot("09:00");
        heatmap.setStaffingLevel(10.0);
        heatmap.setDemandLevel(12.0);
        heatmap.setStatus("Understaffed");
        return heatmap;
    }
}
