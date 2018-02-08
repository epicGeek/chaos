package com.nokia.ices.app.dhss.domain.equipment;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity
public class EquipmentTypeRelNeUnit implements Serializable{

    public EquipmentTypeRelNeUnit(String neType) {
		super();
		this.neType = neType;
	}
    public EquipmentTypeRelNeUnit() {
		super();
	}

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

    private String neType;
    
    private String unitType;
	public String getNeType() {
		return neType;
	}

	public void setNeType(String neType) {
		this.neType = neType;
	}

	public String getUnitType() {
		return unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}
    
    

    
}
