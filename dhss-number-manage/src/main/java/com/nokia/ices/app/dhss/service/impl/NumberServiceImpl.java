package com.nokia.ices.app.dhss.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Predicate.BooleanOperator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.nokia.ices.app.dhss.domain.equipment.EquipmentNe;
import com.nokia.ices.app.dhss.domain.number.NumberGroup;
import com.nokia.ices.app.dhss.domain.number.NumberSection;
import com.nokia.ices.app.dhss.jpa.DynamicSpecifications;
import com.nokia.ices.app.dhss.jpa.SearchFilter;
import com.nokia.ices.app.dhss.repository.equipment.EquipmentNeRepository;
import com.nokia.ices.app.dhss.repository.number.NeGroupRepository;
import com.nokia.ices.app.dhss.repository.number.NumberGroupRepository;
import com.nokia.ices.app.dhss.repository.number.NumberSectionRepository;
import com.nokia.ices.app.dhss.service.NumberService;
import com.nokia.ices.app.dhss.vo.NumberSectionMap;

@Service
public class NumberServiceImpl implements NumberService{
	@Autowired
	private NumberSectionRepository nmberSectionRepository;
	
	@Autowired
	private NumberGroupRepository numberGroupRepository;
	
	@Autowired
	private EquipmentNeRepository equipmentNeRepository;
	
	@Autowired
	private NeGroupRepository neGroupRepository;
	
	@Override
	public List<EquipmentNe> findNeAll(Map<String, Object> map) {
		Map<String,SearchFilter> filter = SearchFilter.parse(map);
		Specification<EquipmentNe> spec = 
                DynamicSpecifications.bySearchFilter(filter.values(), BooleanOperator.AND, EquipmentNe.class);
		return equipmentNeRepository.findAll(spec);
	}
	
	@Override
	public boolean saveNeId(NumberSectionMap numberSectionMap){
		neGroupRepository.delete(neGroupRepository.findNeGroupByNeIdEquals(numberSectionMap.getSelId()));
		neGroupRepository.save(numberSectionMap.getNeGroupResult());
		return true;
	}
	
	
	@Override
	public List<NumberGroup> findGroupAll(Map<String, Object> map) {
		Map<String,SearchFilter> filter = SearchFilter.parse(map);
		Specification<NumberGroup> spec = 
                DynamicSpecifications.bySearchFilter(filter.values(), BooleanOperator.AND, NumberGroup.class);
		return numberGroupRepository.findAll(spec);
	}
	
	@Override
	public boolean saveGroupId(NumberSectionMap numberSectionMap){
		List<Long> numberIdList = new ArrayList<>();
		for (NumberGroup group : numberSectionMap.getList()) {
			numberIdList.add(group.getNumberId());
		}
		List<NumberGroup> groups = numberGroupRepository.findListByNumberIdInOrGroupIdEquals(numberIdList,numberSectionMap.getSelId());
		numberGroupRepository.delete(groups);
		
		numberGroupRepository.save(numberSectionMap.getList());
		
		return true;
	}
}
