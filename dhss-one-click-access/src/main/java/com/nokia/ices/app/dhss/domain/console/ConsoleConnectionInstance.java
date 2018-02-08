package com.nokia.ices.app.dhss.domain.console;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners({AuditingEntityListener.class})
public class ConsoleConnectionInstance {
	/**
     * 
     */
    @Id
    @GeneratedValue
    private Long id;

    private String loginUserName;
    
    public String getIcesUserName() {
		return icesUserName;
	}


	public void setIcesUserName(String icesUserName) {
		this.icesUserName = icesUserName;
	}


	private String icesUserName;
    
    private String loginUnitName;
    
    private String sessionId;
    
    private String token;


    @CreatedDate
    private Date startTime;
    
    @LastModifiedDate
    private Date lastModifyTime;

	private String logPath;

    public String getSessionId() {
		return sessionId;
	}


	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}


	public String getToken() {
		return token;
	}


	public void setToken(String token) {
		this.token = token;
	}


	public Date getLastModifyTime() {
		return lastModifyTime;
	}


	public void setLastModifyTime(Date lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}

    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public Date getStartTime() {
        return startTime;
    }


    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getLogPath() {
        return logPath;
    }


    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public String getLoginUserName() {
        return loginUserName;
    }


    public void setLoginUserName(String loginUserName) {
        this.loginUserName = loginUserName;
    }


    public String getLoginUnitName() {
        return loginUnitName;
    }


    public void setLoginUnitName(String loginUnitName) {
        this.loginUnitName = loginUnitName;
    }
}
