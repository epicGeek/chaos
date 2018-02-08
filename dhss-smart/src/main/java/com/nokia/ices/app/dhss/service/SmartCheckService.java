package com.nokia.ices.app.dhss.service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nokia.ices.app.dhss.domain.command.CommandCheckItem;
import com.nokia.ices.app.dhss.domain.equipment.EquipmentUnit;
import com.nokia.ices.app.dhss.domain.smart.SmartCheckJob;
import com.nokia.ices.app.dhss.domain.smart.SmartCheckResult;

public interface SmartCheckService {
	
	public Page<SmartCheckJob> findSmartCheckJob(Map<String,Object> paramMap, Pageable pageable);
	
	public Iterable<SmartCheckJob> findSmartCheckJobAll();
	
	public boolean saveSmartCheckJob(Iterable<SmartCheckJob> list);

	List<Map<String, Object>> getSmartCheckDetailResultPageList(String id);
	
	public List<Map<String,Object>> findSmartCheckResultList(String scheduleId,String type);

	List<SmartCheckResult> getfindResult(Map<String, Object> paramMap);

	Page<SmartCheckResult> getfindResultPage(Map<String, Object> paramMap, Pageable page);
	
	void sendMessage(SmartCheckJob smart, int status);
	
	Set<EquipmentUnit> findSmartJobUnit(SmartCheckJob job);
	
	Set<CommandCheckItem> findSmartJobCommandCheckItem(SmartCheckJob job);
	
	boolean execJob(Long id);
	
	void zipFile(List<File> files,ZipOutputStream outputStream);
	
	HttpServletResponse downloadZip(File file, HttpServletResponse response, HttpServletRequest request);
	
	public boolean checkJob(Long id);
}
