package com.fiap.techchallenge.scheduling.web.dto;

import com.fiap.techchallenge.scheduling.domain.entity.Appointment;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class AppointmentResponse {

    private Long id;
    private String patientId;
    private String patientName;
    private String patientEmail;
    private String patientPhone;
    private String doctorId;
    private String doctorName;
    private OffsetDateTime scheduledAt;
    private String status;
    private String notes;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static AppointmentResponse fromEntity(Appointment appointment) {
        AppointmentResponse response = new AppointmentResponse();
        response.id = appointment.getId();
        response.patientId = appointment.getPatientId();
        response.patientName = appointment.getPatientName();
        response.patientEmail = appointment.getPatientEmail();
        response.patientPhone = appointment.getPatientPhone();
        response.doctorId = appointment.getDoctorId();
        response.doctorName = appointment.getDoctorName();
        response.scheduledAt = toOffsetDateTime(appointment.getScheduledAt());
        response.status = appointment.getStatus().name();
        response.notes = appointment.getNotes();
        response.createdAt = toOffsetDateTime(appointment.getCreatedAt());
        response.updatedAt = toOffsetDateTime(appointment.getUpdatedAt());
        return response;
    }

    private static OffsetDateTime toOffsetDateTime(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        return value.atOffset(ZoneOffset.UTC);
    }

    public Long getId() {
        return id;
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

    public String getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public OffsetDateTime getScheduledAt() {
        return scheduledAt;
    }

    public String getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
