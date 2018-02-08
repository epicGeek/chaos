//package com.nokia.ices.app.dhss.repository.userdata;
//
//
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
//import org.springframework.data.rest.core.annotation.RepositoryRestResource;
//
//import com.nokia.ices.app.dhss.domain.userdata.UserDataLog;
//
//@RepositoryRestResource(collectionResourceRel = "user-data-log", path = "user-data-log" ,itemResourceRel="user-data-log")
//public interface UserDataLogRepository extends JpaRepository<UserDataLog, Long>,JpaSpecificationExecutor<UserDataLog>{
//	public Page<UserDataLog> findAllByOrderByCreateTimeDesc(Pageable pageable);
//	
//}
//
