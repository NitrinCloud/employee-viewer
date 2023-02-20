package de.dataport.vaadin.data.service;

import de.dataport.vaadin.data.entity.LocationEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class LocationEntityService {

    private static final String URL = "http://localhost:8080/location";

    public Optional<LocationEntity> get(String id) {
        WebClient.RequestHeadersSpec<?> spec = WebClient.create()
                .get().uri(URL + "/find/" + id);
        ResponseEntity<LocationEntity> responseEntity = spec.retrieve().toEntity(LocationEntity.class).block();
        if (responseEntity == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(responseEntity.getBody());
    }

    public LocationEntity update(LocationEntity entity) {
        WebClient.RequestHeadersSpec<?> spec = WebClient.create()
                .post().uri(URL + "/add").header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).body(Mono.just(entity), LocationEntity.class);
        ResponseEntity<LocationEntity> responseEntity = spec.retrieve().toEntity(LocationEntity.class).block();
        if (responseEntity == null) {
            return entity;
        }
        return responseEntity.getBody();
    }

    public void delete(String id) {
        WebClient.RequestHeadersSpec<?> spec = WebClient.create()
                .get().uri(URL + "/delete/" + id);
        spec.retrieve().toBodilessEntity().block();
    }

    public Page<LocationEntity> list(Pageable pageable) {
        WebClient.RequestHeadersSpec<?> spec = WebClient.create()
                .get().uri(URL + "/all");
        ResponseEntity<List<LocationEntity>> responseEntity = spec.retrieve().toEntityList(LocationEntity.class).block();
        if (responseEntity == null) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
        List<LocationEntity> locations = responseEntity.getBody();
        if (locations == null) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
        int currentIndex = pageable.getPageSize() * pageable.getPageNumber();
        if (locations.size() > currentIndex + pageable.getPageSize()) {
            locations = locations.subList(currentIndex, currentIndex + pageable.getPageSize());
        }
        return new PageImpl<>(locations, pageable, locations.size());
    }
}
