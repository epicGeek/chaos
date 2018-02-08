package com.nokia.ices.app.auth.domain;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;


@Entity
public class SystemRole implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5424462946567316455L;

	@Id
	@GeneratedValue
	private Long id;
	
	@Column(length=16)
	private String roleName;
	
	@Column(length=64)
	private String roleDisplayName;
	
	private String roleDesc;

	@ManyToMany
	private Set<SystemUser> systemUser;
	
	@ManyToMany
	private Set<SystemResource> systemResource;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleDisplayName() {
		return roleDisplayName;
	}

	public void setRoleDisplayName(String roleDisplayName) {
		this.roleDisplayName = roleDisplayName;
	}

	public String getRoleDesc() {
		return roleDesc;
	}

	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}

	public Set<SystemUser> getSystemUser() {
		return systemUser;
	}

	public void setSystemUser(Set<SystemUser> systemUser) {
		this.systemUser = systemUser;
	}

	public Set<SystemResource> getSystemResource() {
		return systemResource;
	}

	public void setSystemResource(Set<SystemResource> systemResource) {
		this.systemResource = systemResource;
	}

}
