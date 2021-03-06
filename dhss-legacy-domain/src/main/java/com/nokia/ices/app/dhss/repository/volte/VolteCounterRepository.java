package com.nokia.ices.app.dhss.repository.volte;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.volte.VolteCounter;


@RepositoryRestResource(collectionResourceRel = "volte-message", path = "volte-message" ,itemResourceRel="volte-message")
public interface VolteCounterRepository extends JpaRepository<VolteCounter, Long>,JpaSpecificationExecutor<VolteCounter>{

}
