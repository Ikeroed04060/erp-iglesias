package com.iglesia.dtos.response;

import com.iglesia.model.Church;

public record ChurchResponse(
        Long id,
        String name,
        String address
) {
    public static ChurchResponse from(Church church) {
        return new ChurchResponse(church.getId(), church.getName(), church.getAddress());
    }
}