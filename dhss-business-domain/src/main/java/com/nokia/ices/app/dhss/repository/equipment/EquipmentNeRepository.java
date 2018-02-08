package com.nokia.ices.app.dhss.repository.equipment;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.equipment.EquipmentNe;

@RepositoryRestResource(collectionResourceRel = "equipment-ne", path = "equipment-ne" ,itemResourceRel="equipment-ne")
public interface EquipmentNeRepository extends CrudRepository<EquipmentNe, Long>,JpaSpecificationExecutor<EquipmentNe>{
//	public List<EquipmentNe> findEquipmentNeByNeTypeEquals(@Param("q") String neType);
////	public Page findEquipmentNePageByNeCode(@Param("q") String neType,Pageable page);
}
