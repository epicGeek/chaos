package com.nokia.ices.app.dhss.repository.alarm;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.alarm.AlarmRule;

@RepositoryRestResource(collectionResourceRel = "alarm-rule", path = "alarm-rule", itemResourceRel = "alarm-rule")
public interface AlarmRuleRepository extends CrudRepository<AlarmRule, Long>, JpaSpecificationExecutor<AlarmRule> {
	public AlarmRule findByAlarmNo(String alarmNo);
	public List<AlarmRule> findAllByUnitType(String alarmType);
	
	public List<AlarmRule> findByAlarmNoAndUnitType(@Param("alarmNo")String alarmNo,@Param("unitType")String unitType);
}
