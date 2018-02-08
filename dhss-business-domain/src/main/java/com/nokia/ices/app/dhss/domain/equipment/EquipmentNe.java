package com.nokia.ices.app.dhss.domain.equipment;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class EquipmentNe implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6513097003072642416L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(unique = true, nullable = false)
	private String neName;

	private String dhssName;

	private String physicalLocation;

	private String businessLocation;

    
	private String neType;

	@Column(length = 100)
	//private String neCode; neCode->coGid
	private String coGid;
	@Column(length = 100)
	// private String cnum;  cnum->coDn
    private String coDn;

	private String remarks;

	private String neIdsVersion;

	private String neSwVersion;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNeName() {
		return neName;
	}

	public void setNeName(String neName) {
		this.neName = neName;
	}

	public String getDhssName() {
		return dhssName;
	}

	public void setDhssName(String dhssName) {
		this.dhssName = dhssName;
	}

	public String getPhysicalLocation() {
		return physicalLocation;
	}

	public void setPhysicalLocation(String physicalLocation) {
		this.physicalLocation = physicalLocation;
	}

	public String getBusinessLocation() {
		return businessLocation;
	}

	public void setBusinessLocation(String businessLocation) {
		this.businessLocation = businessLocation;
	}

	public String getNeType() {
		return neType;
	}

	public void setNeType(String neType) {
		this.neType = neType;
	}

	public String getCoGid() {
		return coGid;
	}

	public void setCoGid(String coGid) {
		this.coGid = coGid;
	}

	public String getCoDn() {
		return coDn;
	}

	public void setCoDn(String coDn) {
		this.coDn = coDn;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getNeIdsVersion() {
		return neIdsVersion;
	}

	public void setNeIdsVersion(String neIdsVersion) {
		this.neIdsVersion = neIdsVersion;
	}

	public String getNeSwVersion() {
		return neSwVersion;
	}

	public void setNeSwVersion(String neSwVersion) {
		this.neSwVersion = neSwVersion;
	}

	public Long getEquipmentNeID(){
		return getId();
	}
}
