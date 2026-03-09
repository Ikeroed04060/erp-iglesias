package com.iglesia.service;

import com.iglesia.dtos.response.DashboardResponse;
import com.iglesia.model.Church;
import com.iglesia.model.enums.PaymentStatus;
import com.iglesia.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.YearMonth;

@Service
public class DashboardService {

    private final ChurchRepository churchRepository;
    private final PersonRepository personRepository;
    private final CourseRepository courseRepository;
    private final OfferingRepository offeringRepository;
    private final PaymentRepository paymentRepository;

    public DashboardService(ChurchRepository churchRepository,
                            PersonRepository personRepository,
                            CourseRepository courseRepository,
                            OfferingRepository offeringRepository,
                            PaymentRepository paymentRepository) {
        this.churchRepository = churchRepository;
        this.personRepository = personRepository;
        this.courseRepository = courseRepository;
        this.offeringRepository = offeringRepository;
        this.paymentRepository = paymentRepository;
    }
    
    public DashboardResponse getDashboardData() {
        Church church = churchRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe registrar una iglesia primero"));

        long totalPeople = personRepository.countByChurchId(church.getId());
        long activeCourses = courseRepository.countByChurchIdAndActiveTrue(church.getId());

        YearMonth month = YearMonth.now();
        LocalDateTime start = month.atDay(1).atStartOfDay();
        LocalDateTime end = month.plusMonths(1).atDay(1).atStartOfDay();
        long offeringsMonth = offeringRepository.countByCreatedAtBetween(start, end);

        long pendingPayments = paymentRepository.countByStatus(PaymentStatus.INICIADO);

        return new DashboardResponse(totalPeople, activeCourses, offeringsMonth, pendingPayments);
    }
}