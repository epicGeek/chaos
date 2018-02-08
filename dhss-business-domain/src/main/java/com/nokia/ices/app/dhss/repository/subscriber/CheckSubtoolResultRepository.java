package com.nokia.ices.app.dhss.repository.subscriber;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.subscriber.CheckSubtoolResult;


@RepositoryRestResource(collectionResourceRel = "check-subtool-result", path = "check-subtool-result" ,itemResourceRel="check-subtool-result")
public interface CheckSubtoolResultRepository extends JpaRepository<CheckSubtoolResult, Long>,JpaSpecificationExecutor<CheckSubtoolResult>{

	@Query(value = "SELECT result FROM CheckSubtoolResult result WHERE "
			+ "(( checkName like CONCAT('%',:checkName,'%') or :checkName ='' ) "
			+ "or ( userNumber like CONCAT('%',:checkName,'%') or :checkName ='' ) )"
			+ "and ( exeResults = CONCAT('',:status,'') or :status ='' )", 
			
		   countQuery = "SELECT count(1) FROM CheckSubtoolResult result  WHERE "
		    + " (( checkName like CONCAT('%',:checkName,'%') or :checkName ='' ) "
			+ "or ( userNumber like CONCAT('%',:checkName,'%') or :checkName ='' ) )"
			+ "and ( exeResults = CONCAT('',:status,'') or :status ='' )")
	public Page<CheckSubtoolResult> findCheckSubtoolResultResult(@Param("checkName")String checkName,
																 @Param("status")String status,Pageable page);
}

