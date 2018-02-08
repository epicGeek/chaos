package com.nokia.ices.app.dhss.controller;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nokia.ices.app.dhss.domain.ims.ImsUnitLog;
import com.nokia.ices.app.dhss.service.ImsUnitLogService;

@RestController
public class ImsUnitLogController {

	@Autowired
	private ImsUnitLogService imsUnitLogService;

	@RequestMapping(value = "api/v1/ims-unit-log/data", method = RequestMethod.POST)
	public Page<ImsUnitLog> findImsUnitLog(@RequestBody ImsUnitLogQuery imsUnitLogQuery) throws ParseException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if (null != imsUnitLogQuery.getStartTime()) {
			paramMap.put("createDate_GE", imsUnitLogQuery.getStartTime());
		}
		if (null != imsUnitLogQuery.getEndTime()) {
			paramMap.put("createDate_LE", imsUnitLogQuery.getEndTime());
		}
		if (StringUtils.isNotEmpty(imsUnitLogQuery.getUnitName())) {
			paramMap.put("unitName_LIKE", imsUnitLogQuery.getUnitName());
		}
		if (StringUtils.isNotEmpty(imsUnitLogQuery.getFileName())) {
			paramMap.put("fileName_LIKE", imsUnitLogQuery.getFileName());
		}
		Pageable pageable = new PageRequest(imsUnitLogQuery.getPage(), imsUnitLogQuery.getSize(),
				new Sort(Direction.DESC, "createDate"));
		return imsUnitLogService.findImsUnitLog(paramMap, pageable);
	}

	@RequestMapping(value = "api/v1/ImsUnitLogData/download", method = RequestMethod.GET)
	public void downloadImsUnitLogData(@RequestParam("filePath") String filePath, HttpServletRequest request,
			HttpServletResponse response) {
		imsUnitLogService.downloadImsUnitLogData(filePath, request, response);
	}

}
