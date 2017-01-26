package com.hibernate.domain.ex.component;

import javax.persistence.Embeddable;

/**
 *
 * @author Sunil
 */
@Embeddable
public class Address {

    private String street;
    private String city;
    private String country;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    

}
