package com.nokia.ices.app.dhss.repository.command;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.command.CheckItemGroup;

@RepositoryRestResource(collectionResourceRel = "check-group", path = "check-group", itemResourceRel = "check-group")
public interface CheckItemGroupRepository extends CrudRepository<CheckItemGroup, Long>, JpaSpecificationExecutor<CheckItemGroup>{
	
	public List<CheckItemGroup> findByGroupIdEquals(@Param("q")Long groupId);
	
	public List<CheckItemGroup> findByGroupIdIn(@Param("q")List<Long> ids);
	
}
