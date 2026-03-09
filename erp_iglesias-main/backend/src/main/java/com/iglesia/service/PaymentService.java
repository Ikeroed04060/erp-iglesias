package com.iglesia.service;


import com.iglesia.dtos.response.PaymentResponse;
import com.iglesia.model.Enrollment;
import com.iglesia.model.Offering;
import com.iglesia.model.Payment;
import com.iglesia.model.enums.EnrollmentStatus;
import com.iglesia.model.enums.OfferingStatus;
import com.iglesia.model.enums.PaymentStatus;
import com.iglesia.model.enums.PaymentType;
import com.iglesia.repository.EnrollmentRepository;
import com.iglesia.repository.OfferingRepository;
import com.iglesia.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final OfferingRepository offeringRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          EnrollmentRepository enrollmentRepository,
                          OfferingRepository offeringRepository) {
        this.paymentRepository = paymentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.offeringRepository = offeringRepository;
    }

    // Listar pagos
    public List<PaymentResponse> listPayments(PaymentStatus status) {
        List<Payment> payments = status == null ? paymentRepository.findAll() : paymentRepository.findAllByStatus(status);
        return payments.stream().map(PaymentResponse::from).toList();
    }

    // Confirmar pago
    public PaymentResponse confirmPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pago no encontrado"));

        payment.setStatus(PaymentStatus.CONFIRMADO);
        paymentRepository.save(payment);

        if (payment.getType() == PaymentType.INSCRIPCION_CURSO) {
            Enrollment enrollment = enrollmentRepository.findById(payment.getReferenceId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inscripción no encontrada"));
            enrollment.setStatus(EnrollmentStatus.PAGADA);
            enrollmentRepository.save(enrollment);
        } else if (payment.getType() == PaymentType.OFRENDA) {
            Offering offering = offeringRepository.findById(payment.getReferenceId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ofrenda no encontrada"));
            offering.setStatus(OfferingStatus.REGISTRADA);
            offeringRepository.save(offering);
        }

        return PaymentResponse.from(payment);
    }

    // Marcar pago como fallido
    public PaymentResponse failPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pago no encontrado"));

        if (payment.getStatus() == PaymentStatus.CONFIRMADO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El pago ya fue confirmado");
        }

        payment.setAttempts(payment.getAttempts() + 1);
        payment.setStatus(PaymentStatus.FALLIDO);
        paymentRepository.save(payment);

        return PaymentResponse.from(payment);
    }

    // Reintentar pago fallido
    public PaymentResponse retryPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pago no encontrado"));

        if (payment.getStatus() != PaymentStatus.FALLIDO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se reintenta un pago fallido");
        }

        if (payment.getAttempts() >= 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se superó el máximo de reintentos");
        }

        payment.setStatus(PaymentStatus.INICIADO);
        paymentRepository.save(payment);
        return PaymentResponse.from(payment);
    }
}
