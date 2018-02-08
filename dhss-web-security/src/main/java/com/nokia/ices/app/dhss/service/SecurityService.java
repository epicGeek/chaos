package com.nokia.ices.app.dhss.service;

import java.util.List;
import java.util.Map;
@SuppressWarnings("rawtypes")
public interface SecurityService {
	public Map checkToken(String token);

    public boolean checkToken(String resourceId, String token);

    public Map accessToken(String userName, String password);
    
    public Map accessToken(Map userMap);

	public boolean checkTokenExists(String token);

	public String getSystemUser(String token);
	
	public Map<String, Object> checkMenu(String token,String menuFlag);
	
	/**
	 * <p>1.  token:</p>
	 * 		<p>授权令牌</p>
	 * 
	 * <p>2.  assocResourceFlag:</p>
	 * 		<p>关联资源标识；如：需要获取某个地区下所有网数据；该字段需填写"地区"的定义标识</p>
	   
	   <p>3.  assocResourceAttr:</p>
	   		<p>关联资源名称；如：需要获取某个地区下所有网数据；该字段需填写该"地区" 资源中的属性名称如"name"</p>
		
	   <p>4.  assocResourceAttrValue:</p>
	   		<p>关联资源属性值；如：需要获取某个地区下所有网数据；该字段需填写该地区名称如：北京，上海，resourceFlag填写 网元的标识；</p>
			
	   <p>5.  resourceFlag:</p>
	   		<p>资源标示,事例如下： 
		   		all:获取所有的资源信息
				menu:获取菜单资源
				以上两种是系统固定参数值，
				其余资源标示可以在系统管理平台中自定义，可以
				根据自定义的资源标示，获取对应的资源信息</p>
				
	   <p>6.  contentFlag:</p>
	   		<p>0:代表返回资源内容id, 1代表返回资源内容</p>



	 * @param paramsMap
	 * @return
	 */
	public List getResource(Map paramsMap,boolean isPerssion);
	
	public Object editUserPwd(Map paramsMap);
    /**
     * 
     * @param token
     * @param resourceCode 资源名称
     * @param isPermission 是否需要权限控制
     * @return
     */
	List findResourceList(String token, String resourceCode, boolean isPermission);

	public Map removeToken(Map paramsMap);

	public Map accessTokenAfterLdapAuth(Map userMap);

	public Map<String,String> authWithLDAP(Map<String,String> userInfoMap);

}
