package com.nokia.ices.app.dhss.repository.alarm;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.alarm.AlarmMonitor;

@RepositoryRestResource(collectionResourceRel = "alarm-monitor", path = "alarm-monitor", itemResourceRel = "alarm-monitor")
public interface AlarmMonitorRepository extends CrudRepository<AlarmMonitor, Long>, JpaSpecificationExecutor<AlarmMonitor> {
	public List<AlarmMonitor> findByMailSentIsNull();
	public List<AlarmMonitor> findAll();
}
