package com.nokia.ices.app.dhss.domain.maintain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.nokia.ices.app.dhss.domain.equipment.EquipmentUnit;

@Entity
public class MaintainResult implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue
    private Long id;
	
	private Long neId;

	private String neName;
	private String neType;

    

	private String reportPath;
    
    private String errorLog;
    
    
    private String unitName;
    
    private String itemName;
    
    private boolean isSuccess;
    
    
    public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	private String uuId;

//    @ManyToOne
//    private EquipmentUnit unit;
//    
//    @ManyToOne
//    private CommandCheckItem commandCheckItem;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestTime;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date responseTime;

    @ManyToOne
    private MaintainOperation operation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    

    public String getErrorLog() {
		return errorLog;
	}

	public void setErrorLog(String errorLog) {
		this.errorLog = errorLog;
	}

    public String getReportPath() {
        return reportPath;
    }

    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }

//    public EquipmentUnit getUnit() {
//        return unit;
//    }
//
//    public void setUnit(EquipmentUnit unit) {
//        this.unit = unit;
//    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public Date getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Date responseTime) {
        this.responseTime = responseTime;
    }

    public MaintainOperation getOperation() {
        return operation;
    }

    public void setOperation(MaintainOperation operation) {
        this.operation = operation;
    }

//	public CommandCheckItem getCommandCheckItem() {
//		return commandCheckItem;
//	}
//
//	public void setCommandCheckItem(CommandCheckItem commandCheckItem) {
//		this.commandCheckItem = commandCheckItem;
//	}

	public String getUuId() {
		return uuId;
	}

	public void setUuId(String uuId) {
		this.uuId = uuId;
	}
	public Long getNeId() {
		return neId;
	}

	public void setNeId(Long neId) {
		this.neId = neId;
	}

	public String getNeName() {
		return neName;
	}

	public void setNeName(String neName) {
		this.neName = neName;
	}

	public String getNeType() {
		return neType;
	}

	public void setNeType(String neType) {
		this.neType = neType;
	}

	public void setNEInfo(EquipmentUnit equipmentUnit) {
		this.neId = equipmentUnit.getNeId();
		this.neName = equipmentUnit.getNeName();
		this.neType = equipmentUnit.getNeType();
		
	}
    
}
