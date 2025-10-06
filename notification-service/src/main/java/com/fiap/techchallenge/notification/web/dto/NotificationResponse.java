package com.fiap.techchallenge.notification.web.dto;

import com.fiap.techchallenge.notification.domain.entity.Notification;
import java.time.LocalDateTime;

public class NotificationResponse {

    private Long id;
    private Long appointmentId;
    private String patientId;
    private String patientName;
    private String patientEmail;
    private String patientPhone;
    private String doctorName;
    private String eventType;
    private String notes;
    private LocalDateTime scheduledAt;
    private String status;
    private String channel;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private String errorMessage;

    public static NotificationResponse fromEntity(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.id = notification.getId();
        response.appointmentId = notification.getAppointmentId();
        response.patientId = notification.getPatientId();
        response.patientName = notification.getPatientName();
        response.patientEmail = notification.getPatientEmail();
        response.patientPhone = notification.getPatientPhone();
        response.doctorName = notification.getDoctorName();
        response.eventType = notification.getEventType();
        response.notes = notification.getNotes();
        response.scheduledAt = notification.getScheduledAt();
        response.status = notification.getStatus().name();
        response.channel = notification.getChannel();
        response.createdAt = notification.getCreatedAt();
        response.sentAt = notification.getSentAt();
        response.errorMessage = notification.getErrorMessage();
        return response;
    }

    public Long getId() {
        return id;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getEventType() {
        return eventType;
    }

    public String getNotes() {
        return notes;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public String getStatus() {
        return status;
    }

    public String getChannel() {
        return channel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
