package com.nokia.ices.app.auth.domain;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;


@Entity
public class SystemResource implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3727959246032661203L;
	public static final String SYSTEM_RESOURCE_TYPE_MENU="MENU";
	public static final String SYSTEM_RESOURCE_TYPE_OPERATE="OPERATE";
	public static final String SYSTEM_RESOURCE_TYPE_NE="NE";
	public static final String SYSTEM_RESOURCE_TYPE_AREA="AREA";
	public static final String SYSTEM_RESOURCE_TYPE_NETYPE="NETYPE";
	public static final String SYSTEM_RESOURCE_TYPE_USERDATA_NUMBER_SECTION_GROUP="USERDATA_NUMBER_SECTION_GROUP";

	
	@Id
	@GeneratedValue
	private Long id;
	@Column(length=16)
	private String resourceName;
	
	@Column(length=64)
	private String resourceDisplayName;
	
	private String resourceDesc;
	
	@Column(length=1024)
	private String resourceContent;

	private String resourceType;
	

	@ManyToMany(mappedBy="systemResource")
	private Set<SystemRole> systemRole;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getResourceDisplayName() {
		return resourceDisplayName;
	}

	public void setResourceDisplayName(String resourceDisplayName) {
		this.resourceDisplayName = resourceDisplayName;
	}

	public String getResourceDesc() {
		return resourceDesc;
	}

	public void setResourceDesc(String resourceDesc) {
		this.resourceDesc = resourceDesc;
	}
	public String getResourceContent() {
		return resourceContent;
	}
	public void setResourceContent(String resourceContent) {
		this.resourceContent = resourceContent;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public Set<SystemRole> getSystemRole() {
		return systemRole;
	}

	public void setSystemRole(Set<SystemRole> systemRole) {
		this.systemRole = systemRole;
	}
	
}
