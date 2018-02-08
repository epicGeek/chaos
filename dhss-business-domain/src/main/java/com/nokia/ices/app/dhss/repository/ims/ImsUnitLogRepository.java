package com.nokia.ices.app.dhss.repository.ims;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.ims.ImsUnitLog;

@RepositoryRestResource(collectionResourceRel = "ims-unit-log", path = "ims-unit-log", itemResourceRel = "ims-unit-log")
public interface ImsUnitLogRepository extends CrudRepository<ImsUnitLog, Long>, JpaSpecificationExecutor<ImsUnitLog> {

}
