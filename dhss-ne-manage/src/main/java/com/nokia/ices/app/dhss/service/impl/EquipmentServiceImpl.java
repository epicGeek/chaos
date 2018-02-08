package com.nokia.ices.app.dhss.service.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.Predicate.BooleanOperator;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Component;

import com.nokia.ices.app.dhss.domain.equipment.EquipmentUnit;
import com.nokia.ices.app.dhss.jpa.DynamicSpecifications;
import com.nokia.ices.app.dhss.jpa.SearchFilter;
import com.nokia.ices.app.dhss.jpa.SearchFilter.Operator;
import com.nokia.ices.app.dhss.repository.equipment.EquipmentUnitRepository;
import com.nokia.ices.app.dhss.service.EquipmentService;
import com.nokia.ices.app.dhss.service.SecurityService;

@Component
public class EquipmentServiceImpl implements EquipmentService {

	@Autowired
	private EquipmentUnitRepository equipmentUnitRepository;
	
	@Autowired
	private SecurityService securityService;

	@SuppressWarnings({ "deprecation", "resource" })
	@Override
	public void exportUnitData(String token,String type,String flag,String queryNeType,String queryNeName,String queryUnitType,String paramStr, HttpServletResponse response) {
		
		try {
			response.setHeader("content-disposition", "attachment;filename="  
			        + URLEncoder.encode("unit_"+new SimpleDateFormat("yyyy_MM_dd_HH_mm").format(new Date())+".xls", "UTF-8"));
			List<EquipmentUnit> unitList = findUnitList(token, type, flag, queryNeType, queryNeName, queryUnitType, paramStr);
			
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("unit");
			int index = 0;
			HSSFRow headerRow = sheet.createRow(index);
			Field[] headers = EquipmentUnit.class.getDeclaredFields();
			int columnIndex = 0;
			for (int i = 0; i < headers.length; i++) {
				String headerName = headers[i].getName();
				if(isExport(headerName)){
					sheet.setColumnWidth(i, 5000);
					CellStyle style = workbook.createCellStyle();  
			        style.setFillForegroundColor(IndexedColors.AQUA.getIndex());  
			        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
			        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); 
			        
			        style.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
			        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
			        style.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
			        style.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
			        
			        HSSFFont font = workbook.createFont();
			        font.setFontName("仿宋_GB2312");
			        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体显示
			        style.setFont(font);//选择需要用到的字体格式
			        
			        
					HSSFCell cell = headerRow.createCell(columnIndex);
					cell.setCellStyle(style);
					cell.setCellValue(new HSSFRichTextString(headerName));
					columnIndex ++;
				}
			}
			for (EquipmentUnit unit : unitList) {
				index ++;
				HSSFRow row = sheet.createRow(index);
				columnIndex = 0;
				for (int i = 0; i < headers.length; i++) {
					String fieldName = headers[i].getName();
					if(isExport(fieldName)){
						String methodName = fieldName.substring(0, 1).toUpperCase()+ fieldName.substring(1);  
						Method method = unit.getClass().getMethod("get" + methodName);
						Object value = method.invoke(unit); 
						row.createCell(columnIndex).setCellValue(String.valueOf(value));
						columnIndex++;
					}
				}
			}
			
			workbook.write(response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean isExport(String headerName){
		if(headerName.equals("loginPassword") || headerName.equals("rootPassword") || headerName.equals("smartCheckJob")){
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EquipmentUnit> findUnitList(String token,String type,String flag,String queryNeType,String queryNeName,String queryUnitType,String paramStr) {
		List<Long> neIds = new ArrayList<>();
		List<Map<String,String>> neId = getSystemManageNeList(token,type,flag);
		neId.forEach(action ->{
			neIds.add(Long.parseLong(action.get("source_data_id")));
		});
		
		List<Map<String, String>> typeList = getSystemManageNeList(token, "neUnitType", "1");
		List<SearchFilter> searchFilterNeTypeOr = new ArrayList<SearchFilter>();
		List<SearchFilter> searchFilterUnitTypeOr = new ArrayList<SearchFilter>();
		Set<String> neTypeSet = new HashSet<>();
		Set<String> unitTypeSet = new HashSet<>();
		for (Map<String, String> map : typeList) {
			if(neTypeSet.add(map.get("neType"))) {
				searchFilterNeTypeOr.add(new SearchFilter("neType", Operator.EQ,map.get("neType")));
			}
			if(unitTypeSet.add(map.get("unitType"))) {
				searchFilterUnitTypeOr.add(new SearchFilter("unitType", Operator.EQ,map.get("unitType")));
			}
		}
		if(null == typeList || typeList.size() == 0) {
			searchFilterNeTypeOr.add(new SearchFilter("neType", Operator.EQ,"none"));
			searchFilterUnitTypeOr.add(new SearchFilter("unitType", Operator.EQ,"none"));
		}
		Specification<EquipmentUnit> speciFicationsNeTypeAND = DynamicSpecifications
				.bySearchFilter(searchFilterNeTypeOr, BooleanOperator.OR,EquipmentUnit.class);
		Specification<EquipmentUnit> speciFicationsUnitTypeAND = DynamicSpecifications
				.bySearchFilter(searchFilterUnitTypeOr, BooleanOperator.OR,EquipmentUnit.class);
		
		
		if (neId.size() == 0) {
			return new ArrayList<>();
		}
		List<SearchFilter> searchFilterAND = new ArrayList<SearchFilter>();
		List<SearchFilter> searchFilterOR = new ArrayList<SearchFilter>();
		
		if(StringUtils.isNotEmpty(queryNeType)){
			searchFilterAND.add(new SearchFilter("neType", Operator.EQ,queryNeType));
		}
		if(StringUtils.isNotEmpty(queryNeName)){
			searchFilterAND.add(new SearchFilter("neId", Operator.EQ,queryNeName));
		}
		if(StringUtils.isNotEmpty(queryUnitType)){
			searchFilterAND.add(new SearchFilter("unitType", Operator.EQ,queryUnitType));
		}
		if(StringUtils.isNotEmpty(paramStr)){
			searchFilterOR.add(new SearchFilter("unitName", Operator.LIKE,paramStr));
			searchFilterOR.add(new SearchFilter("serverIp", Operator.LIKE,paramStr));
		}
		searchFilterAND.add(new SearchFilter("neId", Operator.IN,neIds));
		Specification<EquipmentUnit> speciFicationsAND = DynamicSpecifications
				.bySearchFilter(searchFilterAND, BooleanOperator.AND,EquipmentUnit.class);
		Specification<EquipmentUnit> speciFicationsOR = DynamicSpecifications
				.bySearchFilter(searchFilterOR, BooleanOperator.OR,EquipmentUnit.class);
		
		return equipmentUnitRepository.findAll(Specifications.where(speciFicationsAND).and(speciFicationsOR).and(speciFicationsUnitTypeAND).and(speciFicationsNeTypeAND));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List getSystemManageNeList(String token, String type, String flag) {
		Map paramsMap = new HashMap<>();
		paramsMap.put("token", token);
		paramsMap.put("resourceFlag", type);
		paramsMap.put("contentFlag", flag);
		paramsMap.put("assocResourceFlag", "");
		paramsMap.put("assocResourceAttr", "");
		paramsMap.put("assocResourceAttrValue", "");
		List data = securityService.getResource(paramsMap,true);
		return data;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public EquipmentUnit addOrEditEquipmentUnit(Map map) {
		try{
			EquipmentUnit equipmentUnit = null;

			if(map.get("id") != null)
			{
				equipmentUnit = equipmentUnitRepository.findOne(Long.parseLong(map.get("id").toString()));
			}else{
				equipmentUnit = new EquipmentUnit();
			}
			if(map.get("neId")!= null){
			    equipmentUnit.setNeId(Long.parseLong(map.get("neId").toString()));
			    equipmentUnit.setNeName(map.get("neName").toString());
			}
			if(map.get("neType")!=null ){
				equipmentUnit.setNeType(map.get("neType").toString());
			}
			if(map.get("unitType")!= null){
				equipmentUnit.setUnitType(map.get("unitType").toString());
			}
			if(map.get("unitName")!= null){
				equipmentUnit.setUnitName(map.get("unitName").toString());
			}
			if(map.get("serverIp")!= null){
				equipmentUnit.setServerIp(map.get("serverIp").toString());
			}
			if(map.get("serverPort")!= null){
				equipmentUnit.setServerPort(Integer.parseInt(map.get("serverPort").toString()));
			}
			if(map.get("loginName")!= null){
				equipmentUnit.setLoginName(map.get("loginName").toString());
			}
			if(map.get("loginPassword")!= null){
				equipmentUnit.setLoginPassword(map.get("loginPassword").toString());
			}
			if(map.get("rootPassword")!= null){
				equipmentUnit.setRootPassword(map.get("rootPassword").toString());
			}
			if(map.get("serverProtocol")!= null){
				equipmentUnit.setServerProtocol(map.get("serverProtocol").toString());
			}
//			if(map.get("coDn")!= null){//建议前台也改成coDn和coGid
//				equipmentUnit.setCoDn(map.get("coDn").toString());
//			}
//			if(map.get("coGid")!= null){//建议前台也改成coDn和coGid
//				equipmentUnit.setCoGid(map.get("coGid").toString());
//			}
//			if(map.get("physical_location")!= null){//建议前台也改成coDn和coGid
//				equipmentUnit.setPhysicalLocation(map.get("physical_location").toString());
//			}
			equipmentUnit.setCoDn(map.get("coDn")==null?"":map.get("coDn").toString());
			equipmentUnit.setCoGid(map.get("coGid")==null?"":map.get("coGid").toString());
			equipmentUnit.setPhysicalLocation(map.get("physicalLocation")==null?"":map.get("physicalLocation").toString());
			equipmentUnit.setUnitIdsVersion(map.get("unitIdsVersion")==null?"":map.get("unitIdsVersion").toString());
			equipmentUnit.setUnitSwVersion(map.get("unitSwVersion")==null?"":map.get("unitSwVersion").toString());
			equipmentUnit.setUnitDesc(map.get("unitDesc")==null?"":map.get("unitDesc").toString());
			equipmentUnit.setDhssName(map.get("dhssName")==null?"":map.get("dhssName").toString());
			equipmentUnit.setNeSite(map.get("neSite")==null?"":map.get("neSite").toString());
			equipmentUnit.setIsDirect(map.get("isDirect")==null?false:Boolean.valueOf(map.get("isDirect").toString()));
			equipmentUnit.setJumperIp(map.get("jumperIp")==null?"":map.get("jumperIp").toString());
			equipmentUnit.setJumperPort(map.get("jumperPort")==null?"":map.get("jumperPort").toString());
			equipmentUnit.setJumperUserName(map.get("jumperUserName")==null?"":map.get("jumperUserName").toString());
			equipmentUnit.setJumperPassword(map.get("jumperPassword")==null?"":map.get("jumperPassword").toString());
			equipmentUnit.setJumpProtocol(map.get("jumpProtocol")==null?"":map.get("jumpProtocol").toString());
			if(map.get("isForbidden") == null){
				equipmentUnit.setIsForbidden(false);
			}else{
				equipmentUnit.setIsForbidden(Boolean.parseBoolean(map.get("isForbidden").toString()));
			 }
			equipmentUnit = equipmentUnitRepository.save(equipmentUnit);
			
			return equipmentUnit;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page<EquipmentUnit> queryequipmentUnit(String queryNeType, String queryNeName, String queryUnitType,
			String paramStr, String token, String type, String flag, Pageable page) {
		List<Long> neIds = new ArrayList<>();
		List<Map<String,String>> neId = getSystemManageNeList(token,type,flag);
		neId.forEach(action ->{
			neIds.add(Long.parseLong(action.get("source_data_id")));
		});
		
		List<Map<String, String>> typeList = getSystemManageNeList(token, "neUnitType", "1");
		List<SearchFilter> searchFilterNeTypeOr = new ArrayList<SearchFilter>();
		List<SearchFilter> searchFilterUnitTypeOr = new ArrayList<SearchFilter>();
		Set<String> neTypeSet = new HashSet<>();
		Set<String> unitTypeSet = new HashSet<>();
		for (Map<String, String> map : typeList) {
			if(neTypeSet.add(map.get("neType"))) {
				searchFilterNeTypeOr.add(new SearchFilter("neType", Operator.EQ,map.get("neType")));
			}
			if(unitTypeSet.add(map.get("unitType"))) {
				searchFilterUnitTypeOr.add(new SearchFilter("unitType", Operator.EQ,map.get("unitType")));
			}
		}
		
		if(null == typeList || typeList.size() == 0) {
			searchFilterNeTypeOr.add(new SearchFilter("neType", Operator.EQ,"none"));
			searchFilterUnitTypeOr.add(new SearchFilter("unitType", Operator.EQ,"none"));
		}
		
		Specification<EquipmentUnit> speciFicationsNeTypeAND = DynamicSpecifications
				.bySearchFilter(searchFilterNeTypeOr, BooleanOperator.OR,EquipmentUnit.class);
		Specification<EquipmentUnit> speciFicationsUnitTypeAND = DynamicSpecifications
				.bySearchFilter(searchFilterUnitTypeOr, BooleanOperator.OR,EquipmentUnit.class);
		
		
		List<SearchFilter> searchFilterAND = new ArrayList<SearchFilter>();
		List<SearchFilter> searchFilterOR = new ArrayList<SearchFilter>();
		
		if(StringUtils.isNotEmpty(queryNeType)){
			searchFilterAND.add(new SearchFilter("neType", Operator.EQ,queryNeType));
		}
		if(StringUtils.isNotEmpty(queryNeName)){
			searchFilterAND.add(new SearchFilter("neId", Operator.EQ,queryNeName));
		}
		if(StringUtils.isNotEmpty(queryUnitType)){
			searchFilterAND.add(new SearchFilter("unitType", Operator.EQ,queryUnitType));
		}
		if(StringUtils.isNotEmpty(paramStr)){
			searchFilterOR.add(new SearchFilter("unitName", Operator.LIKE,paramStr));
			searchFilterOR.add(new SearchFilter("serverIp", Operator.LIKE,paramStr));
		}
		searchFilterAND.add(new SearchFilter("neId", Operator.IN,neIds));
		Specification<EquipmentUnit> speciFicationsAND = DynamicSpecifications
				.bySearchFilter(searchFilterAND, BooleanOperator.AND,EquipmentUnit.class);
		Specification<EquipmentUnit> speciFicationsOR = DynamicSpecifications
				.bySearchFilter(searchFilterOR, BooleanOperator.OR,EquipmentUnit.class);
		
		return equipmentUnitRepository.findAll(Specifications.where(speciFicationsAND).and(speciFicationsOR).and(speciFicationsUnitTypeAND).and(speciFicationsNeTypeAND), page);
	}

	
	
	

}
