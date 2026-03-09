package com.iglesia.service;

import com.iglesia.dtos.request.OfferingRequest;
import com.iglesia.dtos.response.OfferingResponse;
import com.iglesia.model.Church;
import com.iglesia.model.Offering;
import com.iglesia.model.Payment;
import com.iglesia.model.Person;
import com.iglesia.model.enums.OfferingStatus;
import com.iglesia.model.enums.PaymentType;
import com.iglesia.repository.ChurchRepository;
import com.iglesia.repository.OfferingRepository;
import com.iglesia.repository.PaymentRepository;
import com.iglesia.repository.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OfferingService {

    private final OfferingRepository offeringRepository;
    private final PersonRepository personRepository;
    private final PaymentRepository paymentRepository;
    private final ChurchRepository churchRepository;

    public OfferingService(OfferingRepository offeringRepository,
                           PersonRepository personRepository,
                           PaymentRepository paymentRepository,
                           ChurchRepository churchRepository) {
        this.offeringRepository = offeringRepository;
        this.personRepository = personRepository;
        this.paymentRepository = paymentRepository;
        this.churchRepository = churchRepository;
    }

    @Transactional
    public OfferingResponse createOffering(OfferingRequest request) {
        Church church = requireChurch();
        Person person = personRepository.findById(request.personId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Persona no encontrada"));

        if (!person.getChurch().getId().equals(church.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Persona no pertenece a la iglesia");
        }

        // Crear la ofrenda
        Offering offering = new Offering();
        offering.setPerson(person);
        offering.setAmount(request.amount());
        offering.setConcept(request.concept());
        offering.setStatus(OfferingStatus.PENDIENTE);
        offeringRepository.save(offering);

        // Crear el pago asociado
        Payment payment = new Payment();
        payment.setType(PaymentType.OFRENDA);
        payment.setAmount(request.amount());
        payment.setReferenceId(offering.getId());
        paymentRepository.save(payment);

        // Asociar el pago con la ofrenda
        offering.setPaymentId(payment.getId());
        offeringRepository.save(offering);

        return OfferingResponse.from(offering, payment);
    }

    // Obtener todas las ofrendas de la iglesia
    public List<OfferingResponse> listOfferings() {
        Church church = requireChurch();
        return offeringRepository.findAllByPersonChurchId(church.getId())
                .stream()
                .map(offering -> {
                    Payment payment = null;
                    if (offering.getPaymentId() != null) {
                        payment = paymentRepository.findById(offering.getPaymentId()).orElse(null);
                    }
                    return OfferingResponse.from(offering, payment);
                })
                .toList();
    }

    // Validar si la iglesia está registrada
    private Church requireChurch() {
        return churchRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe registrar una iglesia primero"));
    }
}
