package com.fiap.techchallenge.scheduling.service;

import com.fiap.techchallenge.scheduling.domain.entity.Appointment;
import com.fiap.techchallenge.scheduling.domain.enums.AppointmentStatus;
import com.fiap.techchallenge.scheduling.domain.exception.NotFoundException;
import com.fiap.techchallenge.scheduling.messaging.AppointmentNotificationPayload;
import com.fiap.techchallenge.scheduling.repository.AppointmentRepository;
import com.fiap.techchallenge.scheduling.web.dto.CreateAppointmentRequest;
import com.fiap.techchallenge.scheduling.web.dto.UpdateAppointmentRequest;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppointmentService {

    private final AppointmentRepository repository;
    private final NotificationPublisher notificationPublisher;

    public AppointmentService(AppointmentRepository repository, NotificationPublisher notificationPublisher) {
        this.repository = repository;
        this.notificationPublisher = notificationPublisher;
    }

    @Transactional
    public Appointment createAppointment(CreateAppointmentRequest request) {
        if (request.getScheduledAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("A data da consulta deve estar no futuro");
        }
        Appointment appointment = new Appointment();
        appointment.setPatientId(request.getPatientId());
        appointment.setPatientName(request.getPatientName());
        appointment.setPatientEmail(request.getPatientEmail());
        appointment.setPatientPhone(request.getPatientPhone());
        appointment.setDoctorId(request.getDoctorId());
        appointment.setDoctorName(request.getDoctorName());
        appointment.setScheduledAt(request.getScheduledAt());
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setNotes(request.getNotes());
        Appointment saved = repository.save(appointment);
        publishEvent(saved, "APPOINTMENT_CREATED");
        return saved;
    }

    @Transactional
    public Appointment updateAppointment(Long id, UpdateAppointmentRequest request) {
        Appointment appointment = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Consulta não encontrada"));

        if (request.getScheduledAt() != null) {
            if (request.getScheduledAt().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("A nova data da consulta deve estar no futuro");
            }
            appointment.setScheduledAt(request.getScheduledAt());
        }
        if (request.getStatus() != null) {
            appointment.setStatus(request.getStatus());
        }
        if (request.getNotes() != null) {
            appointment.setNotes(request.getNotes());
        }
        if (request.getPatientEmail() != null) {
            appointment.setPatientEmail(request.getPatientEmail());
        }
        if (request.getPatientPhone() != null) {
            appointment.setPatientPhone(request.getPatientPhone());
        }

        Appointment saved = repository.save(appointment);
        publishEvent(saved, "APPOINTMENT_UPDATED");
        return saved;
    }

    @Transactional(readOnly = true)
    public Appointment findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Consulta não encontrada"));
    }

    @Transactional(readOnly = true)
    public Appointment findByIdForPatient(Long id, String patientId) {
        return repository.findFirstByIdAndPatientId(id, patientId)
                .orElseThrow(() -> new NotFoundException("Consulta não encontrada para o paciente"));
    }

    @Transactional(readOnly = true)
    public List<Appointment> listPatientAppointments(String patientId, boolean includePast) {
        if (includePast) {
            return repository.findByPatientIdOrderByScheduledAtDesc(patientId);
        }
        return repository.findByPatientIdAndScheduledAtAfterOrderByScheduledAtAsc(patientId, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<Appointment> listDoctorAppointments(String doctorId) {
        return repository.findByDoctorIdOrderByScheduledAtDesc(doctorId);
    }

    @Transactional(readOnly = true)
    public List<Appointment> search(String patientId, String doctorId, AppointmentStatus status,
                                    LocalDateTime fromDate, LocalDateTime toDate) {
        return repository.search(patientId, doctorId, status, fromDate, toDate);
    }

    private void publishEvent(Appointment appointment, String eventType) {
        AppointmentNotificationPayload payload = new AppointmentNotificationPayload(
                appointment.getId(),
                appointment.getPatientId(),
                appointment.getPatientName(),
                appointment.getPatientEmail(),
                appointment.getPatientPhone(),
                appointment.getScheduledAt(),
                appointment.getDoctorName(),
                eventType,
                appointment.getNotes()
        );
        notificationPublisher.publish(payload);
    }
}
