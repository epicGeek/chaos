package com.nokia.ices.app.dhss.repository.command;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.command.CommandCheckItem;
import com.nokia.ices.app.dhss.domain.smart.SmartCheckJob;

@RepositoryRestResource(collectionResourceRel = "command-check-item", path = "command-check-item", itemResourceRel = "command-check-item")
public interface CommandCheckItemRepository extends CrudRepository<CommandCheckItem, Long>, JpaSpecificationExecutor<CommandCheckItem> {
	
	
	Set<CommandCheckItem> findListBySmartCheckJob(SmartCheckJob smartCheckJob);
	
	
	Page<CommandCheckItem> findPageByIdNotNull(Pageable pageable);
	
	List<CommandCheckItem> findListByCategoryEquals(@Param("q")String category);
 

}
