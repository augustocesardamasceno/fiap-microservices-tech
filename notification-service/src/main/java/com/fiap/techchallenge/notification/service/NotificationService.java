package com.fiap.techchallenge.notification.service;

import com.fiap.techchallenge.notification.domain.entity.Notification;
import com.fiap.techchallenge.notification.domain.enums.NotificationStatus;
import com.fiap.techchallenge.notification.domain.exception.NotFoundException;
import com.fiap.techchallenge.notification.messaging.AppointmentNotificationMessage;
import com.fiap.techchallenge.notification.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void processAppointmentEvent(AppointmentNotificationMessage message) {
        Notification notification = new Notification();
        notification.setAppointmentId(message.appointmentId());
        notification.setPatientId(message.patientId());
        notification.setPatientName(message.patientName());
        notification.setPatientEmail(message.patientEmail());
        notification.setPatientPhone(message.patientPhone());
        notification.setDoctorName(message.doctorName());
        notification.setEventType(message.eventType());
        notification.setNotes(message.notes());
        notification.setScheduledAt(message.scheduledAt());
        notification.setStatus(NotificationStatus.PENDING);

        Notification saved = repository.save(notification);

        try {
            sendReminder(saved);
            saved.setStatus(NotificationStatus.SENT);
            saved.setSentAt(LocalDateTime.now());
            repository.save(saved);
            LOGGER.info("Reminder sent for appointment {}", saved.getAppointmentId());
        } catch (Exception ex) {
            saved.setStatus(NotificationStatus.FAILED);
            saved.setErrorMessage(ex.getMessage());
            saved.setSentAt(null);
            repository.save(saved);
            LOGGER.error("Failed to send reminder for appointment {}", saved.getAppointmentId(), ex);
        }
    }

    @Transactional(readOnly = true)
    public List<Notification> listNotifications(String patientId, NotificationStatus status,
                                                LocalDateTime fromDate, LocalDateTime toDate) {
        return repository.search(patientId, status, fromDate, toDate);
    }

    @Transactional(readOnly = true)
    public List<Notification> listPatientNotifications(String patientId) {
        return repository.findByPatientIdOrderByCreatedAtDesc(patientId);
    }

    @Transactional(readOnly = true)
    public Notification findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notificação não encontrada"));
    }

    private void sendReminder(Notification notification) {
        // Em uma aplicação real, este método chamaria um serviço de email/SMS externo.
        LOGGER.info("Sending {} reminder to {} (appointment {})", notification.getChannel(),
                notification.getPatientEmail(), notification.getAppointmentId());
    }
}
