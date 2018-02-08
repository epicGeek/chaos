package com.nokia.ices.app.dhss.repository.subscriber;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.subscriber.SubtoolPgwLdapIp;


@RepositoryRestResource(collectionResourceRel = "subtool-ldap", path = "subtool-ldap" ,itemResourceRel="subtool-ldap")
public interface SubtoolLadpIpRepository extends JpaRepository<SubtoolPgwLdapIp, Long>,JpaSpecificationExecutor<SubtoolPgwLdapIp>{
	
	public List<SubtoolPgwLdapIp> findSubtoolByPgwIpEquals(@Param("q")String pgwIp);

	
}

