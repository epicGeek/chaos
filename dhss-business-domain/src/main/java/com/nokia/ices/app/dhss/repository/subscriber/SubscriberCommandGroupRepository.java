package com.nokia.ices.app.dhss.repository.subscriber;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.subscriber.SubscriberCommandGroup;

@RepositoryRestResource(collectionResourceRel = "subscriber-command-group", path = "subscriber-command-group", itemResourceRel = "subscriber-command-group")
public interface SubscriberCommandGroupRepository extends CrudRepository<SubscriberCommandGroup, Long>, JpaSpecificationExecutor<SubscriberCommandGroup> {

	public List<SubscriberCommandGroup> findByGroupIdEquals(@Param("q") Long groupId);

	public List<SubscriberCommandGroup> findByGroupIdIn(@Param("q") List<Long> ids);

}
