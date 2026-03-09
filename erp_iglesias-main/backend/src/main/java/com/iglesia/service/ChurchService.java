package com.iglesia.service;

import com.iglesia.dtos.request.ChurchRequest;
import com.iglesia.dtos.response.ChurchResponse;
import com.iglesia.model.Church;
import com.iglesia.repository.ChurchRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class ChurchService {

    private final ChurchRepository churchRepository;

    public ChurchService(ChurchRepository churchRepository) {
        this.churchRepository = churchRepository;
    }

    public ChurchResponse createChurch(ChurchRequest request) {
        if (churchRepository.count() > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe una iglesia registrada");
        }

        Church church = new Church();
        church.setName(request.name());
        church.setAddress(request.address());
        churchRepository.save(church);

        return ChurchResponse.from(church); // Retorna el DTO con los datos de la iglesia creada
    }

    public ChurchResponse getChurch() {
        return churchRepository.findAll()
                .stream()
                .findFirst()
                .map(ChurchResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay iglesia registrada"));
    }
}