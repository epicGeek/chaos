package com.nokia.ices.app.dhss.repository.alarm;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.alarm.AlarmRecord;

@RepositoryRestResource(collectionResourceRel = "alarm-record", path = "alarm-record", itemResourceRel = "alarm-record")
public interface AlarmRecordRepository extends CrudRepository<AlarmRecord, Long>, JpaSpecificationExecutor<AlarmRecord> {

}
