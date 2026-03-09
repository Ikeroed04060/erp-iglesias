package com.iglesia.dtos.response;

public record DashboardResponse(
        long totalPeople,
        long activeCourses,
        long offeringsMonth,
        long pendingPayments
) {}