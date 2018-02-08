package com.nokia.ices.app.auth.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.auth.domain.SystemUser;
@RepositoryRestResource(collectionResourceRel = "system-user", path = "system-user" ,itemResourceRel="system-user")
public interface SystemUserRepository extends CrudRepository<SystemUser, Long>{

}
