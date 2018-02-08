package com.nokia.ices.app.dhss.service;

import java.util.List;
import java.util.Map;

import com.nokia.ices.app.dhss.domain.equipment.EquipmentNe;
import com.nokia.ices.app.dhss.domain.number.NumberGroup;
import com.nokia.ices.app.dhss.vo.NumberSectionMap;


public interface NumberService {

	List<NumberGroup> findGroupAll(Map<String, Object> map);

	boolean saveGroupId(NumberSectionMap numberSectionMap);

	boolean saveNeId(NumberSectionMap numberSectionMap);

	List<EquipmentNe> findNeAll(Map<String, Object> map);

}
