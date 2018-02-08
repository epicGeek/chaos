package com.nokia.ices.app.dhss.domain.subscriber;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class SubscriberCommandGroup {

	@Id
	@GeneratedValue
	private Long id;
	
	private Long subscriberCommandId;
	
	private Long groupId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSubscriberCommandId() {
		return subscriberCommandId;
	}

	public void setSubscriberCommandId(Long subscriberCommandId) {
		this.subscriberCommandId = subscriberCommandId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	
	
}
