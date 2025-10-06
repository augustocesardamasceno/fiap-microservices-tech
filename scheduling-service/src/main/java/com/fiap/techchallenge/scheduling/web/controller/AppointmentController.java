package com.fiap.techchallenge.scheduling.web.controller;

import com.fiap.techchallenge.scheduling.domain.entity.Appointment;
import com.fiap.techchallenge.scheduling.domain.enums.AppointmentStatus;
import com.fiap.techchallenge.scheduling.service.AppointmentService;
import com.fiap.techchallenge.scheduling.web.dto.AppointmentResponse;
import com.fiap.techchallenge.scheduling.web.dto.CreateAppointmentRequest;
import com.fiap.techchallenge.scheduling.web.dto.UpdateAppointmentRequest;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('DOCTOR','NURSE')")
    public AppointmentResponse create(@Valid @RequestBody CreateAppointmentRequest request) {
        Appointment appointment = appointmentService.createAppointment(request);
        return AppointmentResponse.fromEntity(appointment);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR','NURSE')")
    public AppointmentResponse update(@PathVariable Long id,
                                       @Valid @RequestBody UpdateAppointmentRequest request) {
        Appointment appointment = appointmentService.updateAppointment(id, request);
        return AppointmentResponse.fromEntity(appointment);
    }

    @GetMapping("/{id}")
    public AppointmentResponse findById(@PathVariable Long id, Authentication authentication) {
        if (hasRole(authentication, "ROLE_DOCTOR") || hasRole(authentication, "ROLE_NURSE")) {
            return AppointmentResponse.fromEntity(appointmentService.findById(id));
        }
        if (hasRole(authentication, "ROLE_PATIENT")) {
            Appointment appointment = appointmentService.findByIdForPatient(id, authentication.getName());
            return AppointmentResponse.fromEntity(appointment);
        }
        throw new AccessDeniedException("Acesso negado");
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR','NURSE')")
    public List<AppointmentResponse> search(@RequestParam(required = false) String patientId,
                                            @RequestParam(required = false) String doctorId,
                                            @RequestParam(required = false) AppointmentStatus status,
                                            @RequestParam(required = false)
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                            LocalDateTime from,
                                            @RequestParam(required = false)
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                            LocalDateTime to) {
        return appointmentService.search(patientId, doctorId, status, from, to)
                .stream()
                .map(AppointmentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('PATIENT')")
    public List<AppointmentResponse> myAppointments(Principal principal,
                                                    @RequestParam(defaultValue = "false") boolean includePast) {
        return appointmentService.listPatientAppointments(principal.getName(), includePast)
                .stream()
                .map(AppointmentResponse::fromEntity)
                .collect(Collectors.toList());
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
