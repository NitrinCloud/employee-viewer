package de.dataport.vaadin.data.entity;

import javax.persistence.*;

@Entity
public class EmployeeEntity {

    @Id
    private Long id;

    private String surname;
    private String name;
    private String locationId;

    public EmployeeEntity(Long id, String surname, String name, String locationId) {
        this.id = id;
        this.surname = surname;
        this.name = name;
        this.locationId = locationId;
    }

    public EmployeeEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getLocationId() {
        return locationId;
    }
    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }
}
