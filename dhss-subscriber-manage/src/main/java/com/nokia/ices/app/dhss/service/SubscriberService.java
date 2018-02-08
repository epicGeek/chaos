package com.nokia.ices.app.dhss.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

public interface SubscriberService {
	
	public void exportTemplate(String title, String name ,String defaultValue,HttpServletResponse response);
	
	public List<Map<String,String>> importTemplate(MultipartFile file,HttpServletRequest request);

	public int sendCommandSubtool(String command, String checkName,String userName, String token, String path);

}
