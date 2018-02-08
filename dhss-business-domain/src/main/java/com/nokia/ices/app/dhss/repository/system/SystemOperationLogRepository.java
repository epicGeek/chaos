package com.nokia.ices.app.dhss.repository.system;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.system.SystemOperationLog;
@RepositoryRestResource(collectionResourceRel = "system-operation-log", path = "system-operation-log" ,itemResourceRel="system-operation-log")
public interface SystemOperationLogRepository extends JpaRepository<SystemOperationLog, Long>,JpaSpecificationExecutor<SystemOperationLog>{
	
}
