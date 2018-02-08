package com.nokia.ices.app.auth.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.auth.domain.SystemResource;
@RepositoryRestResource(collectionResourceRel = "system-resource", path = "system-resource" ,itemResourceRel="system-resource")
public interface SystemResourceRepository extends CrudRepository<SystemResource, Long>,SystemResourceRepositoryCustom{
	
}
