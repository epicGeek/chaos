package com.nokia.ices.app.dhss.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nokia.ices.app.dhss.domain.ims.ImsUnitLog;

public interface ImsUnitLogService {

	public Page<ImsUnitLog> findImsUnitLog(Map<String, Object> paramMap, Pageable pageable);

	public void downloadImsUnitLogData(String paramString, HttpServletRequest paramHttpServletRequest,
			HttpServletResponse paramHttpServletResponse);

}
