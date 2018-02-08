package com.nokia.ices.app.dhss.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.UserDataLogMulti;

@RepositoryRestResource(collectionResourceRel = "user-data-log-multi", path = "user-data-log-multi" ,itemResourceRel="user-data-log-multi")
public interface UserDataLogMultiRepository extends JpaRepository<UserDataLogMulti, Long>,JpaSpecificationExecutor<UserDataLogMulti>{
	public Page<UserDataLogMulti> findAllByOrderByCreateTimeDesc(Pageable pageable);
	
}

