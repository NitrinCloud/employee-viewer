package de.dataport.vaadin.data.service;

import de.dataport.vaadin.data.entity.LocationEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class LocationEntityService {

    private final LocationEntityRepository repository;

    public LocationEntityService(LocationEntityRepository repository) {
        this.repository = repository;
    }

    public Optional<LocationEntity> get(Long id) {
        return repository.findById(id);
    }

    public LocationEntity update(LocationEntity entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<LocationEntity> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<LocationEntity> list(Pageable pageable, Specification<LocationEntity> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
