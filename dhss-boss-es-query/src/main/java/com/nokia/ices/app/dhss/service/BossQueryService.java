package com.nokia.ices.app.dhss.service;


import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface BossQueryService {

	List<String> getHlrsnList();

	List<String> getCommandList();

	List<Map<String,Object>> getErrorCodeList();

	Map<String,Object> getBossDataByCondition(Map<String, Object> paramMap);
	
	Map<String,List<String>> pickMsisdnAndImsi(String numberString);

	void downloadExportData(List<Map<String, Object>> dataHits, HttpServletRequest request, HttpServletResponse response);

	List<Map<String, Object>> getbusinessList();

	Map<String,Object> getBossStatistic(Map<String, Object> paramMap);

	void testDeleteIndex() throws IOException, InterruptedException;

}
