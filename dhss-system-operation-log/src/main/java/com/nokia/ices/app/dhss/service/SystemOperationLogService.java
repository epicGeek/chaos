package com.nokia.ices.app.dhss.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nokia.ices.app.dhss.domain.system.SystemOperationLog;

public interface SystemOperationLogService  {
	Page<SystemOperationLog> querySystemOperatonLog(Map<String, Object> paramMap, Pageable page);

	@SuppressWarnings("rawtypes")
	List querySystemOperatonLogFromRemoteServer(Map<String, Object> queryParamMap);
}
