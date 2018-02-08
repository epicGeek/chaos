package com.nokia.ices.app.dhss.repository.ems;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.ems.EmsMonitorHistory;

@RepositoryRestResource(collectionResourceRel = "ems-monitor-history", path = "ems-monitor-history")
public interface EmsMonitorHistoryRepository extends CrudRepository<EmsMonitorHistory, Long> ,JpaSpecificationExecutor<EmsMonitorHistory>{

}
