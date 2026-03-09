package com.iglesia.service;

import com.iglesia.dtos.request.PersonRequest;
import com.iglesia.dtos.response.PersonResponse;
import com.iglesia.model.Church;
import com.iglesia.model.Person;
import com.iglesia.repository.ChurchRepository;
import com.iglesia.repository.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final ChurchRepository churchRepository;

    public PersonService(PersonRepository personRepository, ChurchRepository churchRepository) {
        this.personRepository = personRepository;
        this.churchRepository = churchRepository;
    }

    // Crear una persona
    public PersonResponse createPerson(PersonRequest request) {
        Church church = requireChurch();
        Person person = new Person();
        person.setFirstName(request.firstName());
        person.setLastName(request.lastName());
        person.setDocument(request.document());
        person.setPhone(request.phone());
        person.setEmail(request.email());
        person.setChurch(church);
        personRepository.save(person);
        return PersonResponse.from(person);
    }

    // Listar personas por iglesia
    public List<PersonResponse> listPeople() {
        Church church = requireChurch();
        return personRepository.findAllByChurchId(church.getId())
                .stream()
                .map(PersonResponse::from)
                .toList();
    }

    // Verificar si la iglesia está registrada
    private Church requireChurch() {
        return churchRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe registrar una iglesia primero"));
    }
}
