package de.dataport.vaadin.data.service;

import de.dataport.vaadin.data.entity.EmployeeEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.dataport.vaadin.data.entity.LocationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class EmployeeEntityService {

    private static final String URL = "http://localhost:8080/employee";

    public Optional<EmployeeEntity> get(long id) {
        WebClient.RequestHeadersSpec<?> spec = WebClient.create()
                .get().uri(URL + "/find/" + id);
        ResponseEntity<EmployeeEntity> responseEntity = spec.retrieve().toEntity(EmployeeEntity.class).block();
        if (responseEntity == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(responseEntity.getBody());
    }

    public EmployeeEntity update(EmployeeEntity entity) {
        WebClient.RequestHeadersSpec<?> spec = WebClient.create()
                .post().uri(URL + "/add").header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).body(Mono.just(entity), EmployeeEntity.class);
        ResponseEntity<EmployeeEntity> responseEntity = spec.retrieve().toEntity(EmployeeEntity.class).block();
        if (responseEntity == null) {
            return entity;
        }
        return responseEntity.getBody();
    }

    public void delete(long id) {
        WebClient.RequestHeadersSpec<?> spec = WebClient.create()
                .get().uri(URL + "/delete/" + id);
        spec.retrieve().toBodilessEntity().block();
    }

    public Page<EmployeeEntity> list(Pageable pageable) {
        WebClient.RequestHeadersSpec<?> spec = WebClient.create()
                .get().uri(URL + "/all");
        ResponseEntity<List<EmployeeEntity>> responseEntity = spec.retrieve().toEntityList(EmployeeEntity.class).block();
        if (responseEntity == null) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
        List<EmployeeEntity> employees = responseEntity.getBody();
        if (employees == null) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
        int currentIndex = pageable.getPageSize() * pageable.getPageNumber();
        if (employees.size() > currentIndex + pageable.getPageSize()) {
            employees = employees.subList(currentIndex, currentIndex + pageable.getPageSize());
        }
        return new PageImpl<>(employees, pageable, employees.size());
    }

}
