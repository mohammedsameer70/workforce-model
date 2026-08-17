package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.config.TestSecurityConfig;
import com.boostphysioclinic.workforceapplication.dto.entity.Notification;
import com.boostphysioclinic.workforceapplication.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
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
@Import(TestSecurityConfig.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificationService notificationService;

    private Notification testNotification;

    @BeforeEach
    void setUp() {
        testNotification = createMockNotification();
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetAllNotifications() throws Exception {
        List<Notification> notifications = List.of(testNotification);
        when(notificationService.getAllNotifications()).thenReturn(notifications);

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetUnreadNotifications() throws Exception {
        List<Notification> notifications = List.of(testNotification);
        when(notificationService.getUnreadNotifications()).thenReturn(notifications);

        mockMvc.perform(get("/api/notifications/unread"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetNotificationsByType() throws Exception {
        List<Notification> notifications = List.of(testNotification);
        when(notificationService.getNotificationsByType("Info")).thenReturn(notifications);

        mockMvc.perform(get("/api/notifications/type/Info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetNotificationsByPriority() throws Exception {
        List<Notification> notifications = List.of(testNotification);
        when(notificationService.getNotificationsByPriority("High")).thenReturn(notifications);

        mockMvc.perform(get("/api/notifications/priority/High"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetNotificationByIdFound() throws Exception {
        when(notificationService.getNotificationById(1L)).thenReturn(Optional.of(testNotification));

        mockMvc.perform(get("/api/notifications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetNotificationByIdNotFound() throws Exception {
        when(notificationService.getNotificationById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/notifications/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testCreateNotification() throws Exception {
        when(notificationService.createNotification(any(Notification.class))).thenReturn(testNotification);

        mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testNotification)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Test notification"));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testUpdateNotification() throws Exception {
        when(notificationService.updateNotification(anyLong(), any(Notification.class))).thenReturn(testNotification);

        mockMvc.perform(put("/api/notifications/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testNotification)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testMarkAsRead() throws Exception {
        mockMvc.perform(patch("/api/notifications/1/mark-read"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testDeleteNotification() throws Exception {
        mockMvc.perform(delete("/api/notifications/1"))
                .andExpect(status().isNoContent());
    }

    private Notification createMockNotification() {
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setTitle("Test Notification");
        notification.setMessage("Test notification");
        notification.setType("Info");
        notification.setPriority("High");
        notification.setIsRead(false);
        notification.setCreatedAt(java.time.LocalDateTime.now());
        return notification;
    }
}
