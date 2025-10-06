package com.fiap.techchallenge.scheduling.graphql;

import com.fiap.techchallenge.scheduling.service.AppointmentService;
import com.fiap.techchallenge.scheduling.web.dto.AppointmentResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
public class AppointmentGraphQLController {

    private final AppointmentService appointmentService;

    public AppointmentGraphQLController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('DOCTOR','NURSE')")
    public List<AppointmentResponse> patientAppointments(@Argument String patientId,
                                                          @Argument boolean includePast) {
        return appointmentService.listPatientAppointments(patientId, includePast)
                .stream()
                .map(AppointmentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @QueryMapping
    @PreAuthorize("hasRole('PATIENT')")
    public List<AppointmentResponse> myUpcomingAppointments() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String patientId = authentication.getName();
        return appointmentService.listPatientAppointments(patientId, false)
                .stream()
                .map(AppointmentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('DOCTOR','NURSE')")
    public List<AppointmentResponse> doctorAppointments(@Argument String doctorId) {
        return appointmentService.listDoctorAppointments(doctorId)
                .stream()
                .map(AppointmentResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
