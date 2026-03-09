package com.iglesia.controller;

import com.iglesia.dtos.request.OfferingRequest;
import com.iglesia.dtos.response.OfferingResponse;
import com.iglesia.service.OfferingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offerings")
public class OfferingController {

    private final OfferingService offeringService;

    @Autowired
    public OfferingController(OfferingService offeringService) {
        this.offeringService = offeringService;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @PostMapping
    public OfferingResponse create(@RequestBody OfferingRequest request) {
        return offeringService.createOffering(request);  // Delegamos la lógica al servicio
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @GetMapping
    public List<OfferingResponse> list() {
        return offeringService.listOfferings();  // Delegamos la lógica al servicio
    }
}