package com.nokia.ices.app.dhss.repository.equipment;


import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.equipment.EquipmentTypeRelNeUnit;

@RepositoryRestResource(collectionResourceRel = "equipment-type-rel-ne-unit", path = "equipment-type-rel-ne-unit" ,itemResourceRel="equipment-type-rel-ne-unit")
public interface EquipmentTypeRelNeUnitRepository extends CrudRepository<EquipmentTypeRelNeUnit, Long>,JpaSpecificationExecutor<EquipmentTypeRelNeUnit>{
	List<EquipmentTypeRelNeUnit> findByNeType(@Param("neType") String neType,Sort sort);
	
	@Query("select distinct new EquipmentTypeRelNeUnit(etrnu.neType)  from EquipmentTypeRelNeUnit etrnu order by etrnu.id" )
	List<EquipmentTypeRelNeUnit> findDistinctNeType();

}
