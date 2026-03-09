package com.iglesia.controller;

import com.iglesia.dtos.request.EnrollmentRequest;
import com.iglesia.dtos.response.EnrollmentResponse;
import com.iglesia.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Autowired
    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @PostMapping
    public EnrollmentResponse create(@Valid @RequestBody EnrollmentRequest request) {
        return enrollmentService.createEnrollment(request);  // Delegamos la lógica al servicio
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @GetMapping
    public List<EnrollmentResponse> list() {
        return enrollmentService.listEnrollments();  // Delegamos la lógica al servicio
    }
}
