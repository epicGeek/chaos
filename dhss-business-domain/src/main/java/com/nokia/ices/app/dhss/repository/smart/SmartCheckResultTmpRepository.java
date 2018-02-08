package com.nokia.ices.app.dhss.repository.smart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.smart.SmartCheckResultTmp;


@RepositoryRestResource(collectionResourceRel="smart-check-result-tmp",path="smart-check-result-tmp",itemResourceRel="smart-check-result-tmp")
public interface SmartCheckResultTmpRepository extends JpaRepository<SmartCheckResultTmp, Long>,JpaSpecificationExecutor<SmartCheckResultTmp> {

}
