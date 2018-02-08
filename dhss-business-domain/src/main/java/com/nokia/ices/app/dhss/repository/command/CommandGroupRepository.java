package com.nokia.ices.app.dhss.repository.command;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.command.CommandGroup;
@RepositoryRestResource(collectionResourceRel = "command-group", path = "command-group", itemResourceRel = "command-group")
public interface CommandGroupRepository  extends CrudRepository<CommandGroup, Long>, JpaSpecificationExecutor<CommandGroup>{

}
