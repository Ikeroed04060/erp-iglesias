package com.iglesia.service;

import com.iglesia.dtos.request.EnrollmentRequest;
import com.iglesia.dtos.response.EnrollmentResponse;
import com.iglesia.model.*;
import com.iglesia.model.enums.EnrollmentStatus;
import com.iglesia.model.enums.PaymentType;
import com.iglesia.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final PersonRepository personRepository;
    private final CourseRepository courseRepository;
    private final PaymentRepository paymentRepository;
    private final ChurchRepository churchRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             PersonRepository personRepository,
                             CourseRepository courseRepository,
                             PaymentRepository paymentRepository,
                             ChurchRepository churchRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.personRepository = personRepository;
        this.courseRepository = courseRepository;
        this.paymentRepository = paymentRepository;
        this.churchRepository = churchRepository;
    }

    public EnrollmentResponse createEnrollment(EnrollmentRequest request) {
        Church church = requireChurch();
        Person person = personRepository.findById(request.personId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Persona no encontrada"));
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado"));

        if (!person.getChurch().getId().equals(church.getId()) || !course.getChurch().getId().equals(church.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos no pertenecen a la iglesia");
        }

        // Crear la inscripción
        Enrollment enrollment = new Enrollment();
        enrollment.setPerson(person);
        enrollment.setCourse(course);
        enrollment.setStatus(EnrollmentStatus.PENDIENTE);
        enrollmentRepository.save(enrollment);

        // Crear el pago
        Payment payment = new Payment();
        payment.setType(PaymentType.INSCRIPCION_CURSO);
        payment.setAmount(course.getPrice());
        payment.setReferenceId(enrollment.getId());
        paymentRepository.save(payment);

        // Asociar el pago con la inscripción
        enrollment.setPaymentId(payment.getId());
        enrollmentRepository.save(enrollment);

        return EnrollmentResponse.from(enrollment, payment);  // Retorna los datos de la inscripción y el pago
    }

    // Listar inscripciones de la iglesia
    public List<EnrollmentResponse> listEnrollments() {
        Church church = requireChurch();
        return enrollmentRepository.findAllByPersonChurchId(church.getId())
                .stream()
                .map(enrollment -> {
                    Payment payment = null;
                    if (enrollment.getPaymentId() != null) {
                        payment = paymentRepository.findById(enrollment.getPaymentId()).orElse(null);
                    }
                    return EnrollmentResponse.from(enrollment, payment);
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
