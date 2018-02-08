package com.nokia.ices.app.dhss.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.AnalysisTemplateConfig;

@RepositoryRestResource(collectionResourceRel = "analysis-template-config", path = "analysis-template-config" ,itemResourceRel="analysis-template-config")
public interface AnalysisTemplateConfigRepository extends JpaRepository<AnalysisTemplateConfig, Long>,JpaSpecificationExecutor<AnalysisTemplateConfig>{
	public List<AnalysisTemplateConfig> findAllByIsInUseOrderByUploadTimeDesc(String isInUse);
	public List<AnalysisTemplateConfig> findAllByTemplateType(String type);
}
