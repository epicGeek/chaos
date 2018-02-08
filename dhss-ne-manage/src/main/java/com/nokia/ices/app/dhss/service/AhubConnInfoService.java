package com.nokia.ices.app.dhss.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

public interface AhubConnInfoService {

	public void downloadTemplate(HttpServletRequest request, HttpServletResponse response);

	public void exportAhubData(HttpServletRequest request, HttpServletResponse response);

	public Map<String,String> importAhubInfoData(MultipartFile multiQueryTemplate);

}
