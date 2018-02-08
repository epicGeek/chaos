package com.nokia.ices.app.dhss.repository.kpi;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import com.nokia.ices.app.dhss.domain.kpi.KpiMonitorHistory;

public interface KpiMonitorHistoryRepository extends CrudRepository<KpiMonitorHistory, Long>, JpaSpecificationExecutor<KpiMonitorHistory> {
	
}
