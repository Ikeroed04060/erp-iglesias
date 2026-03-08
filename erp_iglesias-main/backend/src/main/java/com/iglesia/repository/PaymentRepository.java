package com.iglesia.repository;

import com.iglesia.model.Payment;
import com.iglesia.model.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByStatus(PaymentStatus status);
    long countByStatus(PaymentStatus status);
}
