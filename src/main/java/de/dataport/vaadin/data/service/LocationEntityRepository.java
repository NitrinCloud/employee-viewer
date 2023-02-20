package de.dataport.vaadin.data.service;

import de.dataport.vaadin.data.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LocationEntityRepository
        extends
            JpaRepository<LocationEntity, Long>,
            JpaSpecificationExecutor<LocationEntity> {

}
