package com.nokia.ices.app.dhss.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.persistence.criteria.Predicate.BooleanOperator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.nokia.ices.app.dhss.domain.ims.ImsUnitLog;
import com.nokia.ices.app.dhss.jpa.DynamicSpecifications;
import com.nokia.ices.app.dhss.jpa.SearchFilter;
import com.nokia.ices.app.dhss.repository.ims.ImsUnitLogRepository;
import com.nokia.ices.app.dhss.service.ImsUnitLogService;
@Service
public class ImsUnitLogServiceImpl implements ImsUnitLogService {

	@Autowired
	private ImsUnitLogRepository imsUnitLogRepository;

	@Override
	public Page<ImsUnitLog> findImsUnitLog(Map<String, Object> paramMap, Pageable pageable) {
		Map<String, SearchFilter> filter = SearchFilter.parse(paramMap);
		Specification<ImsUnitLog> spec = DynamicSpecifications.bySearchFilter(filter.values(), BooleanOperator.AND,
				ImsUnitLog.class);
		return imsUnitLogRepository.findAll(spec, pageable);
	}
	
	@Override
	public void downloadImsUnitLogData(String fileAbsPath, HttpServletRequest request, HttpServletResponse response)
	  {
	    File downloadFile = new File(fileAbsPath);
	    String fileName = downloadFile.getName();

	    InputStream inStream = null;
	    try {
	      inStream = new FileInputStream(downloadFile.getAbsoluteFile());
	    } catch (FileNotFoundException e1) {
	      e1.printStackTrace();
	    }

	    response.reset();
	    response.setContentType("application/octet-stream");
	    response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

	    byte[] b = new byte[100];
	    try
	    {
	      int len;
	      while ((len = inStream.read(b)) > 0)
	        response.getOutputStream().write(b, 0, len);
	      inStream.close();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	  }

}
