package com.nokia.ices.app.dhss.domain.alarm;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class NotImportantAlarm {
	@Id
	@GeneratedValue
	private Long id;
	
	private String alarmNum;
	
	private Date createDate;
	
	private String createUser;
	
	private String alarmDesc;
	
	public String getAlarmNum() {
		return alarmNum;
	}

	public void setAlarmNum(String alarmNum) {
		this.alarmNum = alarmNum;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getAlarmDesc() {
		return alarmDesc;
	}

	public void setAlarmDesc(String alarmDesc) {
		this.alarmDesc = alarmDesc;
	}

	/*@Column(length = 16777215)
	private String alarmNoArray;*/

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

//	public List<String> getAlarmNoList() {
//		List<String> list = new ArrayList<String>();
//		if(StringUtils.isNotBlank(alarmNoArray)){
//			return list;
//		}
//		String [] array = alarmNoArray.split("_");
//		
//		for (String string : array) {
//			if(!StringUtils.isNotEmpty(string)){
//				continue;
//			}
//			list.add(string);
//		}
//		return list;
//	}

	/*public String getAlarmNoArray() {
		return alarmNoArray;
	}

	public void setAlarmNoArray(String alarmNoArray) {
		this.alarmNoArray = alarmNoArray;
	}*/

}
