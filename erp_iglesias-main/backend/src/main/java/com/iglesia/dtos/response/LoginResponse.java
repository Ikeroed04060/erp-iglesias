package com.iglesia.dtos.response;

public record LoginResponse(
        String token,
        String email,
        String role
) {}
