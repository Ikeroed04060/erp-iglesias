package com.iglesia.service;

import com.iglesia.dtos.request.CourseRequest;
import com.iglesia.dtos.response.CourseResponse;
import com.iglesia.model.Church;
import com.iglesia.model.Course;
import com.iglesia.repository.ChurchRepository;
import com.iglesia.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final ChurchRepository churchRepository;

    public CourseService(CourseRepository courseRepository, ChurchRepository churchRepository) {
        this.courseRepository = courseRepository;
        this.churchRepository = churchRepository;
    }

    // Crear un curso
    public CourseResponse createCourse(CourseRequest request) {
        Church church = requireChurch();
        Course course = new Course();
        course.setName(request.name());
        course.setDescription(request.description());
        course.setPrice(request.price());
        course.setChurch(church);
        courseRepository.save(course);
        return CourseResponse.from(course);
    }

    // Listar los cursos
    public List<CourseResponse> listCourses() {
        Church church = requireChurch();
        return courseRepository.findAllByChurchId(church.getId())
                .stream()
                .map(CourseResponse::from)
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
