package de.dataport.vaadin.data.entity;

import javax.persistence.Entity;

@Entity
public class EmployeeEntity extends AbstractEntity {

    private String surname;
    private String name;
    private String locationId;

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
