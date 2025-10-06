package com.fiap.techchallenge.scheduling.repository;

import com.fiap.techchallenge.scheduling.domain.entity.Appointment;
import com.fiap.techchallenge.scheduling.domain.enums.AppointmentStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientIdOrderByScheduledAtDesc(String patientId);

    List<Appointment> findByPatientIdAndScheduledAtAfterOrderByScheduledAtAsc(String patientId, LocalDateTime after);

    List<Appointment> findByDoctorIdOrderByScheduledAtDesc(String doctorId);

    @Query("select a from Appointment a where (:patientId is null or a.patientId = :patientId) " +
           "and (:doctorId is null or a.doctorId = :doctorId) " +
           "and (:status is null or a.status = :status) " +
           "and (:fromDate is null or a.scheduledAt >= :fromDate) " +
           "and (:toDate is null or a.scheduledAt <= :toDate) " +
           "order by a.scheduledAt desc")
    List<Appointment> search(@Param("patientId") String patientId,
                             @Param("doctorId") String doctorId,
                             @Param("status") AppointmentStatus status,
                             @Param("fromDate") LocalDateTime fromDate,
                             @Param("toDate") LocalDateTime toDate);

    Optional<Appointment> findFirstByIdAndPatientId(Long id, String patientId);
}
