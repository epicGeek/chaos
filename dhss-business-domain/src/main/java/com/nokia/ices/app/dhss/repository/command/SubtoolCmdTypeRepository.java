package com.nokia.ices.app.dhss.repository.command;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.command.SubtoolCmdType;

@RepositoryRestResource(collectionResourceRel = "subtool-cmd-type", path = "subtool-cmd-type", itemResourceRel = "subtool-cmd-type")
public interface SubtoolCmdTypeRepository extends CrudRepository<SubtoolCmdType, Long>, JpaSpecificationExecutor<SubtoolCmdType> {

}
