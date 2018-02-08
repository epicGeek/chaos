package com.nokia.ices.app.dhss.repository.equipment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.equipment.EquipmentTypeRelUnitWeb;

@RepositoryRestResource(collectionResourceRel = "equipment-type-rel-unit-web", path = "equipment-type-rel-unit-web" ,itemResourceRel="equipment-type-rel-unit-web")
public interface EquipmentUnitTypeWebTypeRelRepository extends CrudRepository<EquipmentTypeRelUnitWeb, Long>,JpaSpecificationExecutor<EquipmentTypeRelUnitWeb>{
	List<EquipmentTypeRelUnitWeb> findByUnitType(@Param("unitType") String unitType);
}
