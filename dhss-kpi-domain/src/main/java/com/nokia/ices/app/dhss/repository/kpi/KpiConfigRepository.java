package com.nokia.ices.app.dhss.repository.kpi;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import com.nokia.ices.app.dhss.domain.kpi.KpiConfig;

public interface KpiConfigRepository extends CrudRepository<KpiConfig, Long>,JpaSpecificationExecutor<KpiConfig> {
    public List<KpiConfig> findAllByKpiEnabledIsTrue();
    
}
