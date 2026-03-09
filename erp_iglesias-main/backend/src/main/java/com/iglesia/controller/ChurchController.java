package com.iglesia.controller;

import com.iglesia.dtos.request.ChurchRequest;
import com.iglesia.dtos.response.ChurchResponse;
import com.iglesia.service.ChurchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/church")
public class ChurchController {

    private final ChurchService churchService;

    @Autowired
    public ChurchController(ChurchService churchService) {
        this.churchService = churchService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ChurchResponse create(@RequestBody ChurchRequest request) {
        return churchService.createChurch(request);
    }

    @GetMapping
    public ChurchResponse get() {
        return churchService.getChurch();
    }
}