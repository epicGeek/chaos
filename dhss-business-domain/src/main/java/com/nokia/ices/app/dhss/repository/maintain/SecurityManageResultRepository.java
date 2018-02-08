package com.nokia.ices.app.dhss.repository.maintain;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.maintain.SecurityManageResult;

@RepositoryRestResource(collectionResourceRel = "security-manage-result", path = "security-manage-result" ,itemResourceRel="security-manage-result")
public interface SecurityManageResultRepository extends CrudRepository<SecurityManageResult, Long>,JpaSpecificationExecutor<SecurityManageResult> {

}
