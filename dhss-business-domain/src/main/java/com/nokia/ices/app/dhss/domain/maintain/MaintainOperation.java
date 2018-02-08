package com.nokia.ices.app.dhss.domain.maintain;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class MaintainOperation implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue
    private Long id;


    private String createBy;

    private String checkName;

    private Integer unitCount;

    private Integer itemCount;



    public Integer getUnitCount() {
		return unitCount;
	}

	public void setUnitCount(Integer unitCount) {
		this.unitCount = unitCount;
	}

	public Integer getItemCount() {
		return itemCount;
	}

	public void setItemCount(Integer itemCount) {
		this.itemCount = itemCount;
	}

	@CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestTime;


    private Boolean isDone;


    public Date getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
	}

	public String getCheckName() {
		return checkName;
	}

	public void setCheckName(String checkName) {
		this.checkName = checkName;
	}

	public Boolean getIsDone() {
        return isDone;
    }

    public void setIsDone(Boolean isDone) {
        this.isDone = isDone;
    }

	private String commandCategory;



    @OneToMany(mappedBy="operation")
    @JsonIgnore
    private Set<MaintainResult> result;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}



	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}



	public String getCommandCategory() {
		return commandCategory;
	}

	public void setCommandCategory(String commandCategory) {
		this.commandCategory = commandCategory;
	}

	public Set<MaintainResult> getResult() {
		return result;
	}

	public void setResult(Set<MaintainResult> result) {
		this.result = result;
	}

	public Long getOperationId() {
		 return getId();
	}
}
