package de.dataport.vaadin.data.entity;

import javax.persistence.Entity;

@Entity
public class LocationEntity extends AbstractEntity {

    private String locationId;
    private String street;
    private String city;

    public String getLocationId() {
        return locationId;
    }
    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }
    public String getStreet() {
        return street;
    }
    public void setStreet(String street) {
        this.street = street;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

}
