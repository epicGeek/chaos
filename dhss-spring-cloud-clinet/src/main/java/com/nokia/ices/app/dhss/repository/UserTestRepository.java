package com.nokia.ices.app.dhss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.UserTest;

@RepositoryRestResource(collectionResourceRel="user-test",itemResourceRel="user-test",path="user-test")
public interface UserTestRepository extends JpaRepository<UserTest, Long>,CrudRepository<UserTest, Long>{

}
