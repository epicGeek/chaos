package com.nokia.ices.app.dhss.domain.equipment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class EquipmentUnit  {
	
	@Id
	@GeneratedValue
	private Long id;
	/**
     * 
     */
    @Column(unique = true, nullable = false)
    private String unitName;

    private String unitType;

	private String serverIp;
	
    @Column(length=2048)
    private String webInterfaceURL;

    private int serverPort;
    
    private String serverProtocol;

    private Long neId;
    
    private String neName;

    private String neType;
    

    private String dhssName;
    
    private String neSite;

    
	public String getDhssName() {
		return dhssName;
	}

	public void setDhssName(String dhssName) {
		this.dhssName = dhssName;
	}

	public String getNeSite() {
		return neSite;
	}

	public void setNeSite(String neSite) {
		this.neSite = neSite;
	}
	@JsonIgnore
    private String loginName;

    @JsonIgnore
    private String loginPassword;

    @JsonIgnore
    private String rootPassword;
    
    private String uuId;

    /**
     * 是否禁用（0：启用，1：禁用）
     */
    private Boolean isForbidden;
    
    private String hostname;


	@Column(length=100)
    //private String neCode; neCode->coGid
	private String coGid;
    @Column(length=100)
   // private String cnum;  cnum->coDn
    private String coDn;
    

	private String unitIdsVersion;
    
    private String unitSwVersion;
    
    @Column(length=100)
    private String unitRemark;
    
    private String physicalLocation;
    
    private boolean isDirect;
    
    private String jumperIp;
    
    private String jumperPort;
    
    private String jumperUserName;
    
    private String jumperPassword;
    
    private String jumpProtocol;
    
   

	public String getPhysicalLocation() {
		return physicalLocation;
	}

	public void setPhysicalLocation(String physicalLocation) {
		this.physicalLocation = physicalLocation;
	}

	public String getUnitRemark() {
		return unitRemark;
	}

	public void setUnitRemark(String unitRemark) {
		this.unitRemark = unitRemark;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public String getWebInterfaceURL() {
		return webInterfaceURL;
	}

	public void setWebInterfaceURL(String webInterfaceURL) {
		this.webInterfaceURL = webInterfaceURL;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

	public String getRootPassword() {
		return rootPassword;
	}

	public void setRootPassword(String rootPassword) {
		this.rootPassword = rootPassword;
	}

	public String getUuId() {
		return uuId;
	}

	public void setUuId(String uuId) {
		this.uuId = uuId;
	}

	public Boolean getIsForbidden() {
		return isForbidden;
	}

	public void setIsForbidden(Boolean isForbidden) {
		this.isForbidden = isForbidden;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
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

	public String getUnitIdsVersion() {
		return unitIdsVersion;
	}
    
	public void setUnitIdsVersion(String unitIdsVersion) {
		this.unitIdsVersion = unitIdsVersion;
	}

	public String getUnitSwVersion() {
		return unitSwVersion;
	}

	public void setUnitSwVersion(String unitSwVersion) {
		this.unitSwVersion = unitSwVersion;
	}

    public String getUnitType() {
		return unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}
	public String getNeName(){
		return neName;
	}
    public String getServerProtocol() {
		return serverProtocol;
	}

	public void setServerProtocol(String serverProtocol) {
		this.serverProtocol = serverProtocol;
	}

    public String getNeType() {
		return neType;
	}

	public void setNeType(String neType) {
		this.neType = neType;
	}
	
	public Long getNeId() {
		return neId;
	}

	public void setNeName(String neName) {
		this.neName = neName;
	}

	public Long getNeID(){
		return neId;
	}
	public void setNeId(Long neId) {
		this.neId = neId;
	}

	public boolean isDirect() {
		return isDirect;
	}

	public void setDirect(boolean isDirect) {
		this.isDirect = isDirect;
	}

	public String getJumperIp() {
		return jumperIp;
	}

	public void setJumperIp(String jumperIp) {
		this.jumperIp = jumperIp;
	}

	public String getJumperPort() {
		return jumperPort;
	}

	public void setJumperPort(String jumperPort) {
		this.jumperPort = jumperPort;
	}

	public String getJumperUserName() {
		return jumperUserName;
	}

	public void setJumperUserName(String jumperUserName) {
		this.jumperUserName = jumperUserName;
	}

	public String getJumperPassword() {
		return jumperPassword;
	}

	public void setJumperPassword(String jumperPassword) {
		this.jumperPassword = jumperPassword;
	}

	public String getJumpProtocol() {
		return jumpProtocol;
	}

	public void setJumpProtocol(String jumpProtocol) {
		this.jumpProtocol = jumpProtocol;
	}
	
}
