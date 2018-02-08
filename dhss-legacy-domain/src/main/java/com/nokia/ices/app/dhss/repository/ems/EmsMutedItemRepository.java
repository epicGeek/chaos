package com.nokia.ices.app.dhss.repository.ems;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.ems.EmsMutedItem;

@RepositoryRestResource(collectionResourceRel = "ems-muted-item", path = "ems-muted-item")
public interface EmsMutedItemRepository extends CrudRepository<EmsMutedItem, Long> , JpaSpecificationExecutor<EmsMutedItem>{

}
