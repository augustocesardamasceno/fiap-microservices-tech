package com.fiap.techchallenge.notification.web.controller;

import com.fiap.techchallenge.notification.domain.entity.Notification;
import com.fiap.techchallenge.notification.domain.enums.NotificationStatus;
import com.fiap.techchallenge.notification.service.NotificationService;
import com.fiap.techchallenge.notification.web.dto.NotificationResponse;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR','NURSE')")
    public List<NotificationResponse> list(@RequestParam(required = false) String patientId,
                                           @RequestParam(required = false) NotificationStatus status,
                                           @RequestParam(required = false)
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                           LocalDateTime from,
                                           @RequestParam(required = false)
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                           LocalDateTime to) {
        return notificationService.listNotifications(patientId, status, from, to)
                .stream()
                .map(NotificationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('PATIENT')")
    @ResponseStatus(HttpStatus.OK)
    public List<NotificationResponse> myNotifications(Principal principal) {
        return notificationService.listPatientNotifications(principal.getName())
                .stream()
                .map(NotificationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public NotificationResponse findById(@PathVariable Long id, Authentication authentication) {
        Notification notification = notificationService.findById(id);
        if (hasRole(authentication, "ROLE_DOCTOR") || hasRole(authentication, "ROLE_NURSE")) {
            return NotificationResponse.fromEntity(notification);
        }
        if (hasRole(authentication, "ROLE_PATIENT")
                && notification.getPatientId().equals(authentication.getName())) {
            return NotificationResponse.fromEntity(notification);
        }
        throw new AccessDeniedException("Acesso negado");
    }

    private boolean hasRole(Authentication authentication, String role) {
        if (authentication == null) {
            return false;
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (role.equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
