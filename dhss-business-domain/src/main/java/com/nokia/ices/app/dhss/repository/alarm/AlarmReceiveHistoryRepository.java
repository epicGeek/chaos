package com.nokia.ices.app.dhss.repository.alarm;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.alarm.AlarmReceiveHistory;

@RepositoryRestResource(collectionResourceRel = "alarm-receive-history", path = "alarm-receive-history", itemResourceRel = "alarm-receive-history")
public interface AlarmReceiveHistoryRepository extends CrudRepository<AlarmReceiveHistory, Long>,
															JpaSpecificationExecutor<AlarmReceiveHistory>{

}
