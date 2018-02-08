package com.nokia.ices.app.dhss.domain.equipment;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity
public class EquipmentTypeRelUnitWeb implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 
     */
    private static final long serialVersionUID = -5005249575576755407L;
    private String webType;
    private String unitType;

	public String getWebType() {
		return webType;
	}

	public void setWebType(String webType) {
		this.webType = webType;
	}

	public String getUnitType() {
		return unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}

    


}
