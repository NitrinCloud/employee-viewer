package de.dataport.vaadin.data.service;

import de.dataport.vaadin.data.entity.EmployeeEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class EmployeeEntityService {

    private final EmployeeEntityRepository repository;

    public EmployeeEntityService(EmployeeEntityRepository repository) {
        this.repository = repository;
    }

    public Optional<EmployeeEntity> get(Long id) {
        return repository.findById(id);
    }

    public EmployeeEntity update(EmployeeEntity entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<EmployeeEntity> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<EmployeeEntity> list(Pageable pageable, Specification<EmployeeEntity> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
