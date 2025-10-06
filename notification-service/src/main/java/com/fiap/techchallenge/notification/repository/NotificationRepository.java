package com.fiap.techchallenge.notification.repository;

import com.fiap.techchallenge.notification.domain.entity.Notification;
import com.fiap.techchallenge.notification.domain.enums.NotificationStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByPatientIdOrderByCreatedAtDesc(String patientId);

    @Query("select n from Notification n where (:patientId is null or n.patientId = :patientId) " +
           "and (:status is null or n.status = :status) " +
           "and (:fromDate is null or n.createdAt >= :fromDate) " +
           "and (:toDate is null or n.createdAt <= :toDate) " +
           "order by n.createdAt desc")
    List<Notification> search(@Param("patientId") String patientId,
                              @Param("status") NotificationStatus status,
                              @Param("fromDate") LocalDateTime fromDate,
                              @Param("toDate") LocalDateTime toDate);
}
