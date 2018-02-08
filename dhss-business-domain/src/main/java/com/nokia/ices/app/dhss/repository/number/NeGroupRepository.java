package com.nokia.ices.app.dhss.repository.number;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.number.NeGroup;

@RepositoryRestResource(collectionResourceRel = "ne-group", path = "ne-group" ,itemResourceRel="ne-group")
public interface NeGroupRepository extends CrudRepository<NeGroup, Long>,JpaSpecificationExecutor<NeGroup>{

	public List<NeGroup> findNeGroupByNeIdEquals(@Param("neId")Long neId);
	
	public List<NeGroup> findNeGroupByNumberIdIn(@Param("ids")List<Long> ids);
	
}
