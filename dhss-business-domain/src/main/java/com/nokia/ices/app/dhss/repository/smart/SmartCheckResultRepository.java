package com.nokia.ices.app.dhss.repository.smart;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.smart.SmartCheckResult;


@RepositoryRestResource(collectionResourceRel="smart-check-result",path="smart-check-result",itemResourceRel="smart-check-result")
public interface SmartCheckResultRepository extends JpaRepository<SmartCheckResult, Long>,JpaSpecificationExecutor<SmartCheckResult>{
	
	
	
	/*
	@Query(value="select result from SmartCheckResult result where ( scheduleId = :scheduleId or :scheduleId is null ) "
																  +" and (checkItemId = :checkItemId or :checkItemId is null) "
																  +" and (neId = :neId or :neId is null ) "
																  +" and (resultCode = :resultCode or :resultCode is null) "
																  +" and (unitTypeName = :unitTypeName or :unitTypeName is null )"
																  +" and (unitName = :unitName or :unitName is null) "
																  +" and (startTime >= :startTime or :startTime is null )"
																  +" and (startTime < :endTime or :endTime is null ) "
																  +" and (checkItemName like CONCAT('%',:checkItemName,'%') or :checkItemName = '')",

			countQuery="select count(1) from SmartCheckResult result where ( scheduleId = :scheduleId or :scheduleId is null ) "
																  +" and (checkItemId = :checkItemId or :checkItemId is null) "
																  +" and (neId = :neId or :neId is null ) "
																  +" and (resultCode = :resultCode or :resultCode is null) "
																  +" and (unitTypeName = :unitTypeName or :unitTypeName is null )"
																  +" and (unitName = :unitName or :unitName is null) "
																  +" and (startTime >= :startTime or :startTime is null )"
																  +" and (startTime < :endTime or :endTime is null ) "
																  +" and (checkItemName like CONCAT('%',:checkItemName,'%') or :checkItemName = '')")
	public Page<SmartCheckResult> findSmartCheckResultList(@Param("scheduleId")Long scheduleId,
														   @Param("checkItemId")Long checkItemId,
														   @Param("neId")Long neId,
														   @Param("resultCode")String resultCode,
														   @Param("unitTypeName")String unitTypeName,
														   @Param("unitName")String unitName,
		   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")@Param("startTime")String startTime,
		   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")@Param("endTime")String endTime,
		   												   @Param("checkItemName")String checkItemName,Pageable page);*/

}
