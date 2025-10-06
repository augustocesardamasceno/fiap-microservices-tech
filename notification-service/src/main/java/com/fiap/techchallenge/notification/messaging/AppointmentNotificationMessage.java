package com.fiap.techchallenge.notification.messaging;

import java.time.LocalDateTime;

public record AppointmentNotificationMessage(
        Long appointmentId,
        String patientId,
        String patientName,
        String patientEmail,
        String patientPhone,
        LocalDateTime scheduledAt,
        String doctorName,
        String eventType,
        String notes
) {
}
