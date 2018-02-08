package com.nokia.ices.app.dhss.repository.number;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.number.NumberGroup;

@RepositoryRestResource(collectionResourceRel = "number-group", path = "number-group" ,itemResourceRel="number-group")
public interface NumberGroupRepository  extends CrudRepository<NumberGroup, Long>,JpaSpecificationExecutor<NumberGroup>{
	
	public List<NumberGroup> findListByGroupIdEquals(@Param("groupId")Long groupId);
	
	public List<NumberGroup> findListByGroupIdIn(@Param("ids")List<Long> ids);
	
	public List<NumberGroup> findListByNumberIdInOrGroupIdEquals(@Param("ids")List<Long> ids,@Param("groupId")Long id);

}
