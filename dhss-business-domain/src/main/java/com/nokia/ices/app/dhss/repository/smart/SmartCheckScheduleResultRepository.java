package com.nokia.ices.app.dhss.repository.smart;

import java.util.Date;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.format.annotation.DateTimeFormat;

import com.nokia.ices.app.dhss.domain.smart.SmartCheckScheduleResult;

@RepositoryRestResource(collectionResourceRel = "smart-check-schedule-result", path = "smart-check-schedule-result", itemResourceRel = "smart-check-schedule-result")
public interface SmartCheckScheduleResultRepository
		extends JpaRepository<SmartCheckScheduleResult, Long>, JpaSpecificationExecutor<SmartCheckScheduleResult> {

	@Query(value = "SELECT result FROM SmartCheckScheduleResult result WHERE (startTime >= :startTime "
			+ " or :startTime is null) and (startTime < :endTime or :endTime is  null) and ( jobName like CONCAT('%',:jobName,'%') or :jobName ='' ) ", 
			
		   countQuery = "SELECT count(1) FROM SmartCheckScheduleResult result  WHERE (startTime >= :startTime or "
		   		+ ":startTime is  null)  and (startTime < :endTime or :endTime is  null) and ( jobName like CONCAT('%',:jobName,'%') or :jobName ='' ) ")
	public Page<SmartCheckScheduleResult> findSmartCheckScheduleResult(
			@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @Param("startTime") Date startTime,
			@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @Param("endTime") Date endTime,@Param("jobName")String jobName,Pageable page);

}
