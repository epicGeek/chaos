package com.nokia.ices.app.dhss.repository.subscriber;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import com.nokia.ices.app.dhss.domain.subscriber.SubscriberCommand;

@RepositoryRestResource(collectionResourceRel = "subcriber-command", path = "subcriber-command", itemResourceRel = "subcriber-command")
public interface SubscriberCommandRepository extends CrudRepository<SubscriberCommand, Long>, JpaSpecificationExecutor<SubscriberCommand> {
	
	Page<SubscriberCommand> findPageByIdNotNull(Pageable pageable);
	
	List<SubscriberCommand> findListByCategoryEquals(@Param("q")String category);
	

}
