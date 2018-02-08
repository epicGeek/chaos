package com.nokia.ices.app.auth.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.auth.domain.SystemRole;
@RepositoryRestResource(collectionResourceRel = "system-role", path = "system-role" ,itemResourceRel="system-role")
public interface SystemRoleRepository extends CrudRepository<SystemRole, Long>{

}
