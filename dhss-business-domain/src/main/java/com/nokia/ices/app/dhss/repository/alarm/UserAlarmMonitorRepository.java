package com.nokia.ices.app.dhss.repository.alarm;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.alarm.UserAlarmMonitor;

@RepositoryRestResource(collectionResourceRel = "user-alarm-monitor", path = "user-alarm-monitor", itemResourceRel = "user-alarm-monitor")
public interface UserAlarmMonitorRepository extends CrudRepository<UserAlarmMonitor, Long>, JpaSpecificationExecutor<UserAlarmMonitor>  {

}
