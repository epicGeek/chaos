package com.nokia.ices.app.pgw.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface PgwLogDataQueryService {
	public List<Map<String,Object>> getPgwDataTableByCondition(Map<String,Object> paramMap);
	public Integer getRealPageableCount(Map<String,Object> paramMap);
	public Map<String,List<String>> getDropdownListData();
	public String getPgwXmlLogByRequestId(String requestId);
	public void exportPgwReport(Map<String,String> paramMap,HttpServletRequest request,HttpServletResponse response);
	public Integer getExportLimit();
	public Boolean isSearchLogMode();
	public Map<String,String> getOldestDataTime() throws ParseException;
}
	