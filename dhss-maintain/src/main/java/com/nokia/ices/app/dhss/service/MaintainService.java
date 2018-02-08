package com.nokia.ices.app.dhss.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nokia.ices.app.dhss.domain.maintain.MaintainOperation;
import com.nokia.ices.app.dhss.domain.maintain.MaintainResult;
import com.nokia.ices.app.dhss.domain.maintain.SecurityManageResult;
import com.nokia.ices.app.dhss.vo.OperationMap;

public interface MaintainService {
	
	public MaintainOperation saveMaintainResult(OperationMap operationMap);

	public Page<MaintainOperation> findMaintainOperationPage(Map<String, Object> paramMap, Pageable page);
	
	public MaintainOperation findMaintainOperation(Long id);
	
	public List<MaintainResult> findMaintainResultListByOperationId(String id);
	
	public List<MaintainResult> findMaintainResultListByUUId(String uuids);
	
	public void resultData(File operationLogFile,List<MaintainResult> resultList) throws Exception;
	
	public void downloadFile(HttpServletRequest request,HttpServletResponse response,File operationLogFile,String operationLogName)throws Exception;

	public Page<SecurityManageResult> findSecurityManageResultPage(Map<String, Object> paramMap, Pageable page);
	
	public Iterable<SecurityManageResult> saveSecurityManageResult(List<SecurityManageResult> list);
}
