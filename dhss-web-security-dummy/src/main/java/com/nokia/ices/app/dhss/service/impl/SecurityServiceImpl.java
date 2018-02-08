package com.nokia.ices.app.dhss.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.nokia.ices.app.dhss.service.SecurityService;
@SuppressWarnings("rawtypes")
@Service
public class SecurityServiceImpl implements SecurityService {

	@Override
	public Map checkToken(String token) {
		// TODO Auto-generated method stub
		return new HashMap<>();
	}

	@Override
	public boolean checkToken(String resourceId, String token) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Map accessToken(String userName, String password) {
		// TODO Auto-generated method stub
		Map<String,String> responseMap = new HashMap<String,String>();
		String fakeToken = UUID.randomUUID().toString().replaceAll("-", "");
		responseMap.put("token", fakeToken);
		responseMap.put("role", "管理员");
		responseMap.put("username", userName);
		return responseMap;
	}

	@Override
	public Map accessToken(Map userMap) {
		Map<String,Object> responseMap = new HashMap<String,Object>();
		String fakeToken = UUID.randomUUID().toString().replaceAll("-", "");
		responseMap.put("token", fakeToken);
		responseMap.put("role", "管理员");
		responseMap.put("status", "1");
		responseMap.put("username", userMap.get("username"));
		return responseMap;
	}

	@Override
	public boolean checkTokenExists(String token) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getSystemUser(String token) {
		// TODO Auto-generated method stub
		return "root";
	}

	@Override
	public List getResource(Map paramsMap, boolean isPerssion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object editUserPwd(Map paramsMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List findResourceList(String token, String type, boolean flag) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map removeToken(Map paramsMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map accessTokenAfterLdapAuth(Map userMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String,String> authWithLDAP(Map<String, String> userInfoMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String,Object> checkMenu(String token, String menuFlag) {
		// TODO Auto-generated method stub
		return null;
	}}
