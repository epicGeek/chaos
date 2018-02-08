package com.nokia.ices.app.dhss.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nokia.ices.app.dhss.service.SecurityService;

@RestController
public class TokenController {

	@Autowired
	private SecurityService securityService;
	
	@RequestMapping(value = "/api/v1/menu-check")
	private Map<String, Object> menuCheck(@RequestHeader("Ices-Access-Token")String token,
			@RequestParam(value="menuFlag",required=false) String menuFlag) {
		return securityService.checkMenu(token, menuFlag);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/api/v1/token", method = RequestMethod.POST)
	private Map tokenAccess(@RequestBody Map userBeanMap) {
		System.out.println("com.nokia.ices.app.dhss.controller.TokenController:" + userBeanMap.toString());

		if (null != userBeanMap.get("isLdapUser")
				&& "false".equalsIgnoreCase(userBeanMap.get("isLdapUser").toString())) {
           Map m = securityService.accessToken(userBeanMap);
           return m;
		}else {
		   //Login with LDAP Auth
		Map<String,String> messageLdap = securityService.authWithLDAP(userBeanMap);
		   if("success".equalsIgnoreCase(messageLdap.get("result"))) {
               Map m = securityService.accessTokenAfterLdapAuth(userBeanMap);
               return m;
		   }else {
			   return messageLdap;
		   }
		}
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/api/v1/token-check")
	private Map tokenValidate(@RequestParam(name = "token", required = true) String token) {
		return securityService.checkToken(token);
	}
	
	@RequestMapping(value = "/api/v1/edit-user-pwd",method=RequestMethod.POST)
	public Object editUserPwd(@RequestHeader("Ices-Access-Token")String token,@RequestBody String newPwd){
		Map<String, Object> paramsMap = new HashMap<>();
		paramsMap.put("token", token);
		paramsMap.put("newPwd", newPwd);
		return securityService.editUserPwd(paramsMap);
	}
	
	
	@SuppressWarnings("rawtypes")
	@RequestMapping("/api/v1/resource/{type}")
	public List findGroupAll(@RequestHeader("Ices-Access-Token")String token,@PathVariable String type){
		return securityService.findResourceList(token,type,false);
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping("/api/v1/role/resource/{type}")
	public List findRoleResourceAll(@RequestHeader("Ices-Access-Token")String token,@PathVariable String type){
		return securityService.findResourceList(token,type,true);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/api/v1/removeToken", method=RequestMethod.DELETE)
	public Map removeTokenWhenLogOut(@RequestHeader("Ices-Access-Token")String token){
		Map paramsMap = new HashMap<>();
		paramsMap.put("token", token);
		return securityService.removeToken(paramsMap);
	}
}
