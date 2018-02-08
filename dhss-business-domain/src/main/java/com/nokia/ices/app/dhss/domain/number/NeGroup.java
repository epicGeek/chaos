package com.nokia.ices.app.dhss.domain.number;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class NeGroup {
	
	@Id
	@GeneratedValue
	private Long id;
	
	private Long neId;
	
	private Long numberId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getNeId() {
		return neId;
	}

	public void setNeId(Long neId) {
		this.neId = neId;
	}

	public Long getNumberId() {
		return numberId;
	}

	public void setNumberId(Long numberId) {
		this.numberId = numberId;
	}

}
