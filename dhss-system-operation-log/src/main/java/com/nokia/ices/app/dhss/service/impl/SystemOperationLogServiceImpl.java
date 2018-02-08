package com.nokia.ices.app.dhss.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Predicate.BooleanOperator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.nokia.ices.app.dhss.config.SecurityGlobalSetting;
import com.nokia.ices.app.dhss.domain.system.SystemOperationLog;
import com.nokia.ices.app.dhss.jpa.DynamicSpecifications;
import com.nokia.ices.app.dhss.jpa.SearchFilter;
import com.nokia.ices.app.dhss.repository.system.SystemOperationLogRepository;
import com.nokia.ices.app.dhss.service.SystemOperationLogService;
@Component
public class SystemOperationLogServiceImpl implements SystemOperationLogService  {
//    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Autowired
	private SystemOperationLogRepository systemOperationLogRepository;
	@Autowired
	RestTemplate restTemplate;
	@Autowired
	SecurityGlobalSetting securityGlobalSetting;
	@Override
	public Page<SystemOperationLog> querySystemOperatonLog(Map<String, Object> paramMap, Pageable page) {
		Map<String,SearchFilter> filter = SearchFilter.parse(paramMap);
		Specification<SystemOperationLog> spec = 
                DynamicSpecifications.bySearchFilter(filter.values(), BooleanOperator.AND, SystemOperationLog.class);
		return systemOperationLogRepository.findAll(spec,page);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List querySystemOperatonLogFromRemoteServer(Map<String, Object> queryParamMap) {
		String url = securityGlobalSetting.getEventManagerDataUrl();
		ResponseEntity<List> responseMap = restTemplate.getForEntity(url, List.class, queryParamMap);
		List<Map<String, String>> resultList = responseMap.getBody();
		Collections.sort(resultList, new Comparator<Map<String, String>>() {
			@Override
			public int compare(Map<String, String> m1, Map<String, String> m2) {
				//乱序排序
//				String date1 = m1.get("oper_time");
//
//				try {
//					Long time1 = sdf.parse(date1).getTime();
//					String date2 = m2.get("oper_time");
//					Long time2 = sdf.parse(date2).getTime();
//					int i = (int) (time2 - time1);
//					return i;
//				} catch (ParseException e) {
//					e.printStackTrace();
//					return 1;
//				}   
/****************************************************************/
				//已知给的必然是正序：
				return -1;
			}
		});
		return resultList;
	}

}
