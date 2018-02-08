//package com.nokia.ices.app.dhss.domain.userdata;
//
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.Id;
//import javax.persistence.Transient;
//
//
//@Entity
//public class UserDataNumberSection {
//	@Id
//	@GeneratedValue
//	private Long id;
//	
//	private String number;
//	
//	private String imsi;
//	
//	@Transient
//	private boolean inUse;
//	
//	private String neName;
//	
//	private String numberSectionGroupName;//1.控制权限 2.快速路由到PGW 福州 厦门
//	
//	private String pgwList;//PGW列表[{"PGW_NAME":"PGW01","PGW_IP":"192.168.1.1"},{"PGW_NAME":"PGW01","PGW_IP":"192.168.1.2"}]
//
//	public Long getId() {
//		return id;
//	}
//
//	public void setId(Long id) {
//		this.id = id;
//	}
//
//	public String getNumber() {
//		return number;
//	}
//
//	public void setNumber(String number) {
//		this.number = number;
//	}
//
//	public String getImsi() {
//		return imsi;
//	}
//
//	public void setImsi(String imsi) {
//		this.imsi = imsi;
//	}
//
//	public boolean isInUse() {
//		return inUse;
//	}
//
//	public void setInUse(boolean inUse) {
//		this.inUse = inUse;
//	}
//	
//	public Long getNumberId(){
//		return getId();
//	}
//	
//	public String getNeName() {
//		return neName;
//	}
//
//	public void setNeName(String neName) {
//		this.neName = neName;
//	}
//
//	public String getNumberSectionGroupName() {
//		return numberSectionGroupName;
//	}
//
//	public void setNumberSectionGroupName(String numberSectionGroupName) {
//		this.numberSectionGroupName = numberSectionGroupName;
//	}
//
//	public String getPgwList() {
//		return pgwList;
//	}
//
//	public void setPgwList(String pgwList) {
//		this.pgwList = pgwList;
//	}
//
//	public List<Map<String,String>> getUnitList(){
//		List<Map<String,String>> result = new ArrayList<Map<String,String>>();
//		// TODO pgwList to Object
//		return result;
//	}
//	
//	public String getNumberAndImsi(){
//		return getNumber() + " * " + getImsi();
//	}
//}
