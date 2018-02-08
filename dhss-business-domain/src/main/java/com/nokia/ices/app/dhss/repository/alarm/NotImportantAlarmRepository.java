package com.nokia.ices.app.dhss.repository.alarm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.alarm.NotImportantAlarm;

@RepositoryRestResource(collectionResourceRel = "not-important-alarm", path = "not-important-alarm", itemResourceRel = "not-important-alarm")
public interface NotImportantAlarmRepository  extends JpaRepository<NotImportantAlarm, Long>, JpaSpecificationExecutor<NotImportantAlarm>{

}
