package com.nokia.ices.app.dhss.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.nokia.ices.app.dhss.config.SecurityGlobalSetting;
import com.nokia.ices.app.dhss.service.SecurityService;

@Service
public class SecurityServiceImpl implements SecurityService {
	@Autowired
	private SecurityGlobalSetting securityGlobalSetting;
	private static final Logger logger = LoggerFactory.getLogger(SecurityServiceImpl.class);
	private final RestTemplate restTemplate;
	private Boolean isThereLicenseModule = true;
	public SecurityServiceImpl(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String,String> authWithLDAP(Map<String,String> userInfoMap) {
		Map<String, String> resultMap = new HashMap<>();
		resultMap.put("result", "failed");

		Hashtable env = new Hashtable();
		String ldapAddress = securityGlobalSetting.getLdapServerAddress();
		String dn = securityGlobalSetting.getLdapDN();
		String ddc = securityGlobalSetting.getLdapDC();

		// String LDAP_URL = "ldap://"+ip+":"+port; // LDAP访问地址
		String userName = userInfoMap.get("username");
		String password = userInfoMap.get("password");
		String adminName = ddc + "\\" + userName; // 注意用户名的写法：domain\User或
		String adminPassword = password; // 密码
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapAddress);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, adminName);
		env.put(Context.SECURITY_CREDENTIALS, adminPassword);
		DirContext dc = null;
		try {
			dc = new InitialDirContext(env);// 初始化上下文
			System.out.println("success:dn="+dn);// 这里可以改成异常抛出。
			resultMap.put("result", "success");

		} catch (javax.naming.AuthenticationException e) {
			resultMap.put("error", e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			resultMap.put("error", e.getMessage());
			e.printStackTrace();
		} finally {
			if (dc != null) {
				try {
					dc.close();
				} catch (NamingException e) {
					resultMap.put("error", e.getMessage());
				}
			}
		}
		return resultMap;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Map checkToken(String token) {
		Map<String, String> requestMap = new HashMap<>();
		requestMap.put("token", token);
		logger.info(securityGlobalSetting.getValidateTokenUrl());
		logger.info(requestMap.toString());
		try {

			ResponseEntity<Map> responseMap = restTemplate.getForEntity(securityGlobalSetting.getValidateTokenUrl(),
					Map.class, requestMap);
			return responseMap.getBody();
		} catch (RuntimeException e) {
			e.printStackTrace();
			return new HashMap();
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map accessToken(String userName, String password) {
		logger.info(securityGlobalSetting.getAccessTokenUrl());
		Map<String, String> requestMap = new HashMap<>();
		requestMap.put("username", userName);
		requestMap.put("password", password);
		ResponseEntity<Map> responseMap = restTemplate.postForEntity(securityGlobalSetting.getAccessTokenUrl(),
				requestMap, Map.class);
		return responseMap.getBody();
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map accessToken(Map userMap) {
		logger.info(securityGlobalSetting.getAccessTokenUrl());
		ResponseEntity<Map> responseMap = restTemplate.getForEntity(securityGlobalSetting.getAccessTokenUrl(),
				Map.class, userMap);
		return responseMap.getBody();
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map accessTokenAfterLdapAuth(Map userMap) {
		ResponseEntity<Map> responseMap = restTemplate.getForEntity(securityGlobalSetting.getAfterLdapAuthUrl(),
				Map.class, userMap);
		return responseMap.getBody();
	}

	@Override
	public boolean checkToken(String resourceId, String token) {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean checkTokenExists(String token) {
		Map<String, Object> tokenMap = new HashMap<String, Object>();
		try {
			tokenMap = this.checkToken(token);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return false;
		}
		if (!tokenMap.containsKey("status")) {
			return false;
		}
		if (StringUtils.equals("0", tokenMap.get("status").toString())) {
			return true;
		}
		return false;
	}

	@Override
	public String getSystemUser(String token) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Map<String, String>> getResource(Map paramsMap, boolean isPermission) {
		try {
			String url = isPermission ? securityGlobalSetting.getResourceUrl()
					: securityGlobalSetting.getNoPermissionResourceUrl();
			logger.info(url);
			logger.info(paramsMap.toString());

			ResponseEntity<Map> responseMap = restTemplate.getForEntity(url, Map.class, paramsMap);
			Map resultMap = responseMap.getBody();
			List data = (List) resultMap.get("sourceData");
			if (data == null)
				return new ArrayList<>();
			if (data.size() == 0) {
				return data;
			}
			Map sourceData = (Map) data.get(0);
			List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
			List resultData = (List) sourceData.get("sourceData");
			for (Object object : resultData) {
				mapList.add((Map) object);
			}
			return mapList;
		} catch (RestClientException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object editUserPwd(Map paramsMap) {

		String url = securityGlobalSetting.getEditUserPwdUrl();
		ResponseEntity<Map> responseMap = restTemplate.getForEntity(url, Map.class, paramsMap);
		Map resultMap = responseMap.getBody();
		return resultMap;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List findResourceList(String token, String type, boolean flag) {
		Map paramsMap = new HashMap<>();
		paramsMap.put("token", token);
		paramsMap.put("resourceFlag", type);
		paramsMap.put("contentFlag", "1");
		paramsMap.put("assocResourceFlag", "");
		paramsMap.put("assocResourceAttr", "");
		paramsMap.put("assocResourceAttrValue", "");
		List sourceData = getResource(paramsMap, flag);
		return sourceData;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map removeToken(Map paramsMap) {
		// TODO Auto-generated method stub
		String url = securityGlobalSetting.getRemoveTokenUrl();
		restTemplate.getForEntity(url, null, paramsMap);
		Map resultMap = new HashMap<>();
		return resultMap;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String, Object> checkMenu(String token, String menuFlag) {
		Map<String, Object> resultMessage = new HashMap<String, Object>();
		if(!checkTokenExists(token)) {
			resultMessage.put("status", false);
			resultMessage.put("message", null);
			return resultMessage;
		};
		Map paramsMap = new HashMap<>();
		paramsMap.put("token", token);
		paramsMap.put("menuFlag", menuFlag);
		ResponseEntity<Map> responseMap = restTemplate.getForEntity(securityGlobalSetting.getMenuCheckUrl(), Map.class, paramsMap);
		Map resultMap = responseMap.getBody();
		
		
		String message = null;
		boolean menuCheckResult = (resultMap.get("status") != null && resultMap.get("status").equals("0")) ? true : false;
		if(menuCheckResult && isThereLicenseModule) {
			// isThereLicenseModule 表示是否系统管理平台有License验证功能
			// 如果系统管理平台没有License验证功能，isThereLicenseModule 为false,否则为true
			paramsMap.clear();
			paramsMap.put("projectName", StringUtils.isNotBlank(securityGlobalSetting.getProjectName()) ? securityGlobalSetting.getProjectName() : "dhss");
			paramsMap.put("functionName", menuFlag);
			paramsMap.put("proviceName", securityGlobalSetting.getProviceName());
			paramsMap.put("cityName", securityGlobalSetting.getCityName());
			paramsMap.put("serviceName", securityGlobalSetting.getServiceName());
			responseMap = restTemplate.getForEntity(securityGlobalSetting.getLicenseCheckUrl(), Map.class, paramsMap);
			resultMap = responseMap.getBody();
			menuCheckResult = (resultMap.get("status") != null && (resultMap.get("status").equals("0") || resultMap.get("status").equals("2"))) ? true : false;
			message = resultMap.get("message") != null ? resultMap.get("message").toString() : "";
			resultMessage.put("isAlart", resultMap.get("status").equals("2"));
		}
		resultMessage.put("status", menuCheckResult);
		resultMessage.put("message", message);
		return resultMessage;
	}

}
