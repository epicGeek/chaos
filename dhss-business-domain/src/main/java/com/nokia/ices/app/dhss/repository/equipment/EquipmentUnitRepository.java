package com.nokia.ices.app.dhss.repository.equipment;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nokia.ices.app.dhss.domain.equipment.EquipmentUnit;
import com.nokia.ices.app.dhss.domain.smart.SmartCheckJob;

@RepositoryRestResource(collectionResourceRel = "equipment-unit", path = "equipment-unit" ,itemResourceRel="equipment-unit")
public interface EquipmentUnitRepository extends CrudRepository<EquipmentUnit, Long>,JpaSpecificationExecutor<EquipmentUnit>{
    Set<EquipmentUnit> findListBySmartCheckJob(SmartCheckJob smartCheckJob);

    List<EquipmentUnit> findListByUnitNameIn(Set<String> unitNameSet);
    
    List<EquipmentUnit> findListByUnitTypeEquals(String unitType);
    
    EquipmentUnit findEquipmentUnitByUuId(@Param("q") String UuId);
    
    EquipmentUnit findEquipmentUnitByUnitName(@Param("q") String unitName);
    
    EquipmentUnit findEquipmentUnitByServerIpEquals(@Param("q")String ip);
    
    List<EquipmentUnit> findEquipmentUnitByNeIdInAndUnitTypeEquals(@Param("ids")List<Long> ids,@Param("unitType")String unitType);
}
