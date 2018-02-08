package com.nokia.ices.app.dhss.repository.command;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.command.CommandCategory;
@RepositoryRestResource(collectionResourceRel = "command-category", path = "command-category", itemResourceRel = "command-category")
public interface CommandCategoryRepository  extends CrudRepository<CommandCategory, Long>, JpaSpecificationExecutor<CommandCategory>{

}
