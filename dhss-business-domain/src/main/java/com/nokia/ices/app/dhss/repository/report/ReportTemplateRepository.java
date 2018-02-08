package com.nokia.ices.app.dhss.repository.report;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.report.ReportTemplate;

@RepositoryRestResource(collectionResourceRel = "report-template", path = "report-template" ,itemResourceRel="report-template")
public interface ReportTemplateRepository extends CrudRepository<ReportTemplate, Long>,JpaSpecificationExecutor<ReportTemplate>  {

}
