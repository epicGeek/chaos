package com.nokia.ices.app.dhss.repository.monitor;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.monitor.MonitorTable;

@RepositoryRestResource(collectionResourceRel = "monitor-table", path = "monitor-table" ,itemResourceRel="monitor-table")
public interface MonitorTableRepository extends CrudRepository<MonitorTable,Long> ,JpaSpecificationExecutor<MonitorTable> {
	

}
