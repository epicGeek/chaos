package com.nokia.ices.app.dhss.repository.maintain;


import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.maintain.MaintainResult;


@RepositoryRestResource(collectionResourceRel = "maintain-result", path = "maintain-result" ,itemResourceRel="maintain-result")
public interface MaintainResultRepository extends CrudRepository<MaintainResult, Long>,JpaSpecificationExecutor<MaintainResult>{
	List<MaintainResult> findResultByOperationIdAndResponseTimeIsNotNull(@Param("q") Long operationId);
	List<MaintainResult> findResultByOperationId(@Param("q") Long operationId);
	
	List<MaintainResult> findResultByUuIdEquals(@Param("q")String uuId);
	
	@SuppressWarnings("rawtypes")
	List<MaintainResult> findResultByUuIdIn(@Param("q")List uuIdList);
}
