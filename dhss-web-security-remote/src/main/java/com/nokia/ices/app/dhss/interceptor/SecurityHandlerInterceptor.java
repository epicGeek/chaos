package com.nokia.ices.app.dhss.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.nokia.ices.app.dhss.service.SecurityService;

@Component
public class SecurityHandlerInterceptor implements HandlerInterceptor {
	
	private static final Logger logger = LoggerFactory.getLogger(SecurityHandlerInterceptor.class);

	private static final String ACCESS_TOKEN_HEADER = "Ices-Access-Token";

	private static final String NO_ACCESS_TOKEN_ERROR = "NO ACCESS TOKEN IN YOUR REQUEST";
	private static final String ACCESS_DENIED = "ACCESS DENIED";

	// private static final String TOKEN_TIMEOUT = "TOKEN TIMEOUT";
	
	@Autowired
	private SecurityService securityService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// CrossDomain preflight，skip
		if(StringUtils.equalsIgnoreCase(HttpMethod.OPTIONS.name(),request.getMethod())){
			logger.debug("preflight,skip");
			
			return true;
		}
		if(request.getRequestURI().endsWith("download")){
			return true;
		}
		if(request.getRequestURI().contains("upload")){
			return true;
		}
		// access-token，skip
		if(request.getRequestURI().endsWith("token") && StringUtils.equalsIgnoreCase(HttpMethod.POST.name(),request.getMethod())){
			logger.debug("access-token，skip");
			return true;
		}
		if(request.getRequestURI().endsWith("removeToken") && StringUtils.equalsIgnoreCase(HttpMethod.DELETE.name(),request.getMethod())){
			logger.debug("remove-token，skip");
			return true;
		}
		if(request.getRequestURI().contains("menu-check")) {
			return true;
		}
		if(request.getRequestURI().contains("checkToken") && StringUtils.equalsIgnoreCase(HttpMethod.GET.name(),request.getMethod())){
			logger.debug("check-token，skip");
			return true;
		}
		String token = request.getHeader(ACCESS_TOKEN_HEADER);
		if(StringUtils.isBlank(token)){
//			response.addHeader(ACCESS_DENIED, NO_ACCESS_TOKEN_ERROR);
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, NO_ACCESS_TOKEN_ERROR);
			return false;
		}
        if(!securityService.checkTokenExists(token)){
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ACCESS_DENIED);
			return false;
        }
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		
	}

}
