package com.nokia.ices.app.dhss.domain.smart;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nokia.ices.app.dhss.domain.command.CommandCheckItem;
import com.nokia.ices.app.dhss.domain.equipment.EquipmentUnit;

@Entity
public class SmartCheckJob {

	@Id
	@GeneratedValue
	private Long id;

	// 方案名称
	private String jobName;

	// 方案描述
	@Column(length = 1024)
	private String jobDesc;

	// 创建时间
	private Date createTime;

	// 创建人ID
	private Long userId;

	// 开始时间(每月、每周几号)
	private Date execDay;

	// 开始时间(小时:分钟)
	private String execTime;

	// 下次运行时间(每月、每周几号几点几分)
	private Date nextDay;

	// 执行粒度 1:每天执行 2:每周执行 3:每月执行
	private int jobType;

	// 执行状态 0：未启用 1:已启用 2:已停止
	private int execFlag;

	private String loopHour;

	@ManyToMany
	@JsonIgnore
	private Set<CommandCheckItem> checkItem = new HashSet<>();
	

	@ManyToMany
	@JsonIgnore
    private Set<EquipmentUnit> unit = new HashSet<>();

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobDesc() {
		return jobDesc;
	}

	public void setJobDesc(String jobDesc) {
		this.jobDesc = jobDesc;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getExecDay() {
		return execDay;
	}

	public void setExecDay(Date execDay) {
		this.execDay = execDay;
	}

	public String getExecTime() {
		return execTime;
	}

	public void setExecTime(String execTime) {
		this.execTime = execTime;
	}

	public Date getNextDay() {
		return nextDay;
	}

	public void setNextDay(Date nextDay) {
		this.nextDay = nextDay;
	}

	public int getJobType() {
		return jobType;
	}

	public void setJobType(int jobType) {
		this.jobType = jobType;
	}

	public int getExecFlag() {
		return execFlag;
	}

	public void setExecFlag(int execFlag) {
		this.execFlag = execFlag;
	}

	public String getLoopHour() {
		return loopHour;
	}

	public void setLoopHour(String loopHour) {
		this.loopHour = loopHour;
	}

    public Set<CommandCheckItem> getCheckItem() {
        return checkItem;
    }

    public void setCheckItem(Set<CommandCheckItem> checkItem) {
        this.checkItem = checkItem;
    }

    public Set<EquipmentUnit> getUnit() {
        return unit;
    }

    public void setUnit(Set<EquipmentUnit> unit) {
        this.unit = unit;
    }
    
    public String getUnitNames(){
    	String temp = "";
    	for (EquipmentUnit equipmentUnit : this.getUnit()) {
    		temp += "[" + equipmentUnit.getUnitName() + "]";
		}
    	return temp;
    }
    
    public String getItemIds(){
    	String temp = "";
    	for (CommandCheckItem commandCheckItem : this.getCheckItem()) {
    		temp += "[" + commandCheckItem.getId() + "]";
		}
    	return temp;
    }
}
