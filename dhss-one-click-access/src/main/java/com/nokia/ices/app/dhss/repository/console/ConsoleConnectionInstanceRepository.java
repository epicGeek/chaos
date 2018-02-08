package com.nokia.ices.app.dhss.repository.console;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.console.ConsoleConnectionInstance;

@RepositoryRestResource(collectionResourceRel = "console-connection-instance", path = "console-connection-instance" ,itemResourceRel="console-connection-instance")
public interface ConsoleConnectionInstanceRepository extends CrudRepository<ConsoleConnectionInstance, Long>,JpaSpecificationExecutor<ConsoleConnectionInstance>{
	public List<ConsoleConnectionInstance> findByLoginUnitNameOrderByStartTimeDesc(@Param ("unitName") String unitName);
}
