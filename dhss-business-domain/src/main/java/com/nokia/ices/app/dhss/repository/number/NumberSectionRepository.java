package com.nokia.ices.app.dhss.repository.number;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.number.NumberSection;

@RepositoryRestResource(collectionResourceRel = "number-section", path = "number-section" ,itemResourceRel="number-section")
public interface NumberSectionRepository  extends CrudRepository<NumberSection, Long>,JpaSpecificationExecutor<NumberSection>{
	public List<NumberSection> findByGroupIdEquals(@Param("q")Long groupId);
	public List<NumberSection> findNumberSectionByGroupIdIsNull();
	
	public List<NumberSection> findByNeIdEquals(@Param("q")Long neId);
	public List<NumberSection> findNumberSectionByNeIdIsNull();
	
	public List<NumberSection> findNumberByIdIn(@Param("q")List<Long> ids);
}
