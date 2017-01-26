package com.hibernate.domain.ex.inheritance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Sunil
 */
//@DiscriminatorValue("TwoWheeler")
@Entity
@Table(name = "two_wheeler")
public class TwoWheeler extends Vehicle {

   
    @Column(name = "handle_type")
    private String handleType;

    public String getHandleType() {
        return handleType;
    }

    public void setHandleType(String handleType) {
        this.handleType = handleType;
    }
}
