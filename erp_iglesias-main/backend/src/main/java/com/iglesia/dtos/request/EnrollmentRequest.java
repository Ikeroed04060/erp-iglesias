package com.iglesia.dtos.request;

import jakarta.validation.constraints.NotNull;

public record EnrollmentRequest(
        @NotNull Long personId,
        @NotNull Long courseId
) {}
