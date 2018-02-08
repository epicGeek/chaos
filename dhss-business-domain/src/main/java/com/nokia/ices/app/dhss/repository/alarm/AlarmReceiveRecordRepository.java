package com.nokia.ices.app.dhss.repository.alarm;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.alarm.AlarmReceiveRecord;

@RepositoryRestResource(collectionResourceRel = "alarm-receive-record", path = "alarm-receive-record", itemResourceRel = "alarm-receive-record")
public interface AlarmReceiveRecordRepository extends CrudRepository<AlarmReceiveRecord, Long>, JpaSpecificationExecutor<AlarmReceiveRecord>{

}
