package com.hibernate.domain.ex.inheritance;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Sunil
 */
@Entity
@DiscriminatorValue(value = "FourWheeler")
@Table(name = "four_wheeler")
public class FourWheeler extends Vehicle {

    
    @Column(name = "wheels")
    private int wheels;
    @Column(name = "power_window")
    private boolean powerWindow;

    public int getWheels() {
        return wheels;
    }

    public void setWheels(int wheels) {
        this.wheels = wheels;
    }

    public boolean isPowerWindow() {
        return powerWindow;
    }

    public void setPowerWindow(boolean powerWindow) {
        this.powerWindow = powerWindow;
    }
}
