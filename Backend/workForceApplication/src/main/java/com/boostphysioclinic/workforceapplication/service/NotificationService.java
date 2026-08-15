package com.boostphysioclinic.workforceapplication.service;

import com.boostphysioclinic.workforceapplication.Repository.NotificationRepository;
import com.boostphysioclinic.workforceapplication.dto.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @PostConstruct
    public void initializeSampleNotifications() {
        if (notificationRepository.count() == 0) {
            notificationRepository.save(Notification.builder()
                    .title("System Update Completed")
                    .message("The workforce forecasting model has been successfully updated to version 2.1")
                    .type("info")
                    .priority("low")
                    .icon("pi pi-info-circle")
                    .createdAt(LocalDateTime.now().minusMinutes(5))
                    .isRead(false)
                    .build());

            notificationRepository.save(Notification.builder()
                    .title("High Demand Alert")
                    .message("Unexpected spike in demand detected for the Engineering department. Consider additional staffing.")
                    .type("critical")
                    .priority("high")
                    .icon("pi pi-exclamation-triangle")
                    .createdAt(LocalDateTime.now().minusHours(1))
                    .isRead(false)
                    .build());

            notificationRepository.save(Notification.builder()
                    .title("Shift Optimization Available")
                    .message("New shift allocation recommendations are ready for review based on latest predictions.")
                    .type("warning")
                    .priority("medium")
                    .icon("pi pi-clock")
                    .createdAt(LocalDateTime.now().minusHours(3))
                    .isRead(false)
                    .build());

            notificationRepository.save(Notification.builder()
                    .title("Report Generated Successfully")
                    .message("Monthly workforce analysis report has been generated and is ready for download.")
                    .type("success")
                    .priority("low")
                    .icon("pi pi-check-circle")
                    .createdAt(LocalDateTime.now().minusHours(5))
                    .isRead(true)
                    .build());

            notificationRepository.save(Notification.builder()
                    .title("CSV Import Completed")
                    .message("Employee data import from CSV has been processed successfully. 150 records updated.")
                    .type("info")
                    .priority("low")
                    .icon("pi pi-upload")
                    .createdAt(LocalDateTime.now().minusDays(1))
                    .isRead(true)
                    .build());
        }
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public List<Notification> getUnreadNotifications() {
        return notificationRepository.findByIsReadFalse();
    }

    public List<Notification> getNotificationsByType(String type) {
        return notificationRepository.findByType(type);
    }

    public List<Notification> getNotificationsByPriority(String priority) {
        return notificationRepository.findByPriority(priority);
    }

    public java.util.Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }

    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public Notification updateNotification(Long id, Notification notification) {
        notification.setId(id);
        return notificationRepository.save(notification);
    }

    public Notification markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id).orElseThrow();
        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
}
