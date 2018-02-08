package com.nokia.ices.app.dhss.repository.subtool;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.subtool.CheckSubtoolResult;


@RepositoryRestResource(collectionResourceRel = "subtool-item", path = "subtool-item" ,itemResourceRel="subtool-item")
public interface SubtoolRepository extends JpaRepository<CheckSubtoolResult, Long>,JpaSpecificationExecutor<CheckSubtoolResult>{

	
}

