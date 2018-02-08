package com.nokia.ices.app.dhss.config;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "dhss.kpi")
public class KpiCustomSetting implements BeanClassLoaderAware{

	private ClassLoader classLoader; 
	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}


	public ClassLoader getClassLoader() {
		return classLoader;
	}
	public Integer exportRecordNumberLimit = 10000;
	
	public Integer getExportRecordNumberLimit() {
		return exportRecordNumberLimit;
	}
	public Boolean showMoreGrains = false;
	
	public List<String> noCountDataKpiCodeSet ;
	
	
	public void setNoCountDataKpiCodeSet(List<String> noCountDataKpiCodeSet) {
		this.noCountDataKpiCodeSet = noCountDataKpiCodeSet;
	}


	public List<String> getNoCountDataKpiCodeSet() {
		return noCountDataKpiCodeSet;
	}

	public Boolean getShowMoreGrains() {
		return showMoreGrains;
	}


	public void setShowMoreGrains(Boolean showMoreGrains) {
		this.showMoreGrains = showMoreGrains;
	}


	public void setExportRecordNumberLimit(Integer exportRecordNumberLimit) {
		this.exportRecordNumberLimit = exportRecordNumberLimit;
	}
}
