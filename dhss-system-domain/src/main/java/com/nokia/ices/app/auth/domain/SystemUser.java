package com.nokia.ices.app.auth.domain;

import java.io.Serializable;
import java.sql.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class SystemUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1873031114342322204L;
	
	@Id
	@GeneratedValue
	private Long id;
	@Column(length=16)
	private String userName;
	
	@Column(length=64)
	private String userDisplayName;
	
	private String userPassword;
	
	private String salt;
	
	private Date exipreDate;
	
	@ManyToMany(mappedBy="systemUser")
	private Set<SystemRole> SystemRole;

	public Set<SystemRole> getSystemRole() {
		return SystemRole;
	}

	public void setSystemRole(Set<SystemRole> systemRole) {
		SystemRole = systemRole;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserDisplayName() {
		return userDisplayName;
	}

	public void setUserDisplayName(String userDisplayName) {
		this.userDisplayName = userDisplayName;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public Date getExipreDate() {
		return exipreDate;
	}

	public void setExipreDate(Date exipreDate) {
		this.exipreDate = exipreDate;
	}
}
