package com.nokia.ices.app.dhss.repository.subtool;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.subtool.SubtoolPgwLdapIp;


@RepositoryRestResource(collectionResourceRel = "subtool-ldap", path = "subtool-ldap" ,itemResourceRel="subtool-ldap")
public interface SubtoolLadpIpRepository extends JpaRepository<SubtoolPgwLdapIp, Long>,JpaSpecificationExecutor<SubtoolPgwLdapIp>{

	
}

