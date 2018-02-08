
package com.nokia.ices.app.dhss.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.nokia.ices.app.dhss.domain.kpi.KpiMonitorHistory;

public interface KpiMonitorHistoryService {

	Page<KpiMonitorHistory> getKpiDataByCondition(Map<String,Specification<KpiMonitorHistory>> map,Pageable page);
	Map<String,String> KpiNameAndCodeMap();
	Map<String,Object> getKpiDropdownList();
	public Page<KpiMonitorHistory> getExportData(Map<String,Specification<KpiMonitorHistory>> map);
	public Boolean exportData(Page<KpiMonitorHistory> data,HttpServletRequest request,HttpServletResponse response) throws Exception;
	public Boolean fakeDownload(HttpServletRequest request, HttpServletResponse response) throws Exception;
	public List<String> getNeList(String token);
}

