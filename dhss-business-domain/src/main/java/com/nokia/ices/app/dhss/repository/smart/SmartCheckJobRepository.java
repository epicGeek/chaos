package com.nokia.ices.app.dhss.repository.smart;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.smart.SmartCheckJob;



@RepositoryRestResource(collectionResourceRel = "smart-check-job", path = "smart-check-job" ,itemResourceRel="smart-check-job")
public interface SmartCheckJobRepository extends CrudRepository<SmartCheckJob, Long>,JpaSpecificationExecutor<SmartCheckJob> {
	List<SmartCheckJob> findSmartCheckJobListByJobNameContains(@Param("jobName") String jobName,Sort sort);
	
	List<SmartCheckJob> findSmartCheckJobByJobName(@Param("jobName") String jobName);
}
