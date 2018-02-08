package com.nokia.ices.app.dhss.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.criteria.Predicate.BooleanOperator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nokia.ices.app.dhss.domain.equipment.EquipmentNe;
import com.nokia.ices.app.dhss.domain.number.NumberSection;
import com.nokia.ices.app.dhss.jpa.DynamicSpecifications;
import com.nokia.ices.app.dhss.jpa.SearchFilter;
import com.nokia.ices.app.dhss.jpa.SearchFilter.Operator;
import com.nokia.ices.app.dhss.repository.number.NumberSectionRepository;
import com.nokia.ices.app.dhss.service.NumberService;
import com.nokia.ices.app.dhss.vo.NumberSectionMap;

@RestController
@CrossOrigin
public class NumberController {

	@Autowired
	private NumberSectionRepository nmberSectionRepository;
	
	@Autowired
	private NumberService numberService;
	
	/*@RequestMapping("/api/v1/number-group-all")
	public List<NumberGroup> findGroupAll(){
		return numberService.findGroupAll(new HashMap<String,Object>());
	}*/
	
	@RequestMapping("/api/v1/number-group-select")
	public List<NumberSection> findSelectNumber(@RequestParam(value="q",required=false)Long groupId){
		return nmberSectionRepository.findByGroupIdEquals(groupId);
	}
	
	@RequestMapping("/api/v1/number-group-optional")
	public List<NumberSection> findOptionalNumber(){
		return nmberSectionRepository.findNumberSectionByGroupIdIsNull();
	}
	
	@RequestMapping(value="/api/v1/number-group-save",method=RequestMethod.POST,consumes={"application/json;charset=UTF-8"})
	public boolean saveGroupId(@RequestBody NumberSectionMap numberSectionMap){	
		return numberService.saveGroupId(numberSectionMap);
	}
	
	
	@RequestMapping("/api/v1/number-ne-optional")
	public List<NumberSection> findOptionalNeNumber(){
		return nmberSectionRepository.findNumberSectionByNeIdIsNull();
	}
	
	@RequestMapping("/api/v1/number-ne-all")
	public List<EquipmentNe> findNeAll(){
		return numberService.findNeAll(new HashMap<String,Object>());
	}
	
	@RequestMapping("/api/v1/number-ne-select")
	public List<NumberSection> findSelectNeNumber(@RequestParam(value="q",required=false)Long neId){
		return nmberSectionRepository.findByNeIdEquals(neId);
	}
	
	@RequestMapping(value="/api/v1/number-ne-save",method=RequestMethod.POST,consumes={"application/json;charset=UTF-8"})
	public boolean saveNeId(@RequestBody NumberSectionMap numberSectionMap){	
		return numberService.saveNeId(numberSectionMap);
	}
	
	@RequestMapping("api/v1/query-number-list-all")
	public List<NumberSection> queryNumberSectionAll(
			@RequestParam(value="paramStr",required=false)String paramStr,
			Pageable page
			){

		List<SearchFilter> searchFilterOR = new ArrayList<SearchFilter>();			
				
		Specification<NumberSection> speciFicationsOR = DynamicSpecifications
				.bySearchFilter(searchFilterOR, BooleanOperator.OR,NumberSection.class);
		
		return nmberSectionRepository.findAll(Specifications.where(speciFicationsOR));
		
	}
	

	@RequestMapping("api/v1/query-number-list")
	public Page<NumberSection> queryNumberSection(
			@RequestParam(value="paramStr",required=false)String paramStr,
			Pageable page
			){

		List<SearchFilter> searchFilterOR = new ArrayList<SearchFilter>();			
		if(StringUtils.isNotEmpty(paramStr)){
			searchFilterOR.add(new SearchFilter("imsi", Operator.LIKE,paramStr));
			searchFilterOR.add(new SearchFilter("msisdn", Operator.LIKE,paramStr));
		}
				
		Specification<NumberSection> speciFicationsOR = DynamicSpecifications
				.bySearchFilter(searchFilterOR, BooleanOperator.OR,NumberSection.class);
		
		return nmberSectionRepository.findAll(Specifications.where(speciFicationsOR), page);
		
	}
}
