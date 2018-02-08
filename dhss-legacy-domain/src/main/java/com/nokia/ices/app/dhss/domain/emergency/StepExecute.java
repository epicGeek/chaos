package com.nokia.ices.app.dhss.domain.emergency;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


//@Entity
public class StepExecute {
	
	@Id
	@GeneratedValue
	private Long id;
	
	private Integer executeState;
	
	
	@ManyToOne
	private EmergencySecurityState emergencySecurityState; 
	
	
	@ManyToOne
	private StepConfTable stepConfTable;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getExecuteState() {
		return executeState;
	}

	public void setExecuteState(Integer executeState) {
		this.executeState = executeState;
	}

	public EmergencySecurityState getEmergencySecurityState() {
		return emergencySecurityState;
	}

	public void setEmergencySecurityState(EmergencySecurityState emergencySecurityState) {
		this.emergencySecurityState = emergencySecurityState;
	}

	public StepConfTable getStepConfTable() {
		return stepConfTable;
	}

	public void setStepConfTable(StepConfTable stepConfTable) {
		this.stepConfTable = stepConfTable;
	}  

}
