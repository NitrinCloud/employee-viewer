package de.dataport.vaadin.data.service;

import de.dataport.vaadin.data.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EmployeeEntityRepository
        extends
            JpaRepository<EmployeeEntity, Long>,
            JpaSpecificationExecutor<EmployeeEntity> {

}
