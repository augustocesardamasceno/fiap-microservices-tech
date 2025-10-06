package com.fiap.techchallenge.scheduling.messaging;

import java.time.LocalDateTime;

public record AppointmentNotificationPayload(
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
