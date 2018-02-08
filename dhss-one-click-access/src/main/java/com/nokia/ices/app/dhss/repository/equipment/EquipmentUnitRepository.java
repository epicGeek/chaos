package com.nokia.ices.app.dhss.repository.equipment;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.equipment.EquipmentUnit;

@RepositoryRestResource(collectionResourceRel = "equipment-unit", path = "equipment-unit" ,itemResourceRel="equipment-unit")
public interface EquipmentUnitRepository extends CrudRepository<EquipmentUnit, Long>,JpaSpecificationExecutor<EquipmentUnit>{

}
