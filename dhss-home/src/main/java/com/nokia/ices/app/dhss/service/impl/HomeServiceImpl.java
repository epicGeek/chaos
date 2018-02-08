package com.nokia.ices.app.dhss.service.impl;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.persistence.criteria.Predicate.BooleanOperator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import com.nokia.ices.app.dhss.core.utils.JsonMapper;
import com.nokia.ices.app.dhss.domain.alarm.AlarmReceiveRecord;
import com.nokia.ices.app.dhss.domain.alarm.NotImportantAlarm;
import com.nokia.ices.app.dhss.domain.kpi.KpiConfig;
import com.nokia.ices.app.dhss.domain.kpi.KpiMonitor;
import com.nokia.ices.app.dhss.jpa.DynamicSpecifications;
import com.nokia.ices.app.dhss.jpa.SearchFilter;
import com.nokia.ices.app.dhss.jpa.SearchFilter.Operator;
import com.nokia.ices.app.dhss.repository.alarm.AlarmReceiveRecordRepository;
import com.nokia.ices.app.dhss.repository.alarm.NotImportantAlarmRepository;
import com.nokia.ices.app.dhss.repository.kpi.KpiConfigRepository;
import com.nokia.ices.app.dhss.repository.kpi.KpiMonitorRepository;
import com.nokia.ices.app.dhss.service.HomeService;
import com.nokia.ices.app.dhss.service.SecurityService;

@Component
public class HomeServiceImpl implements HomeService {

	@Autowired
	private AlarmReceiveRecordRepository alarmReceiveRecordRepository;

	@Autowired
	private NotImportantAlarmRepository notImportantAlarmRepository;

	@Autowired
	private KpiConfigRepository kpiConfigRepository;

	@Autowired
	private KpiMonitorRepository kpiMonitorRepository;

	@Autowired
	private SecurityService securityService;

	// @Autowired
	// private SystemResourceRepository systemResourceRepository;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List findHomeNavItem(String token) {
		List menuList = new ArrayList();

		try {
			Map paramsMap = new HashMap<>();
			paramsMap.put("token", token);
			paramsMap.put("resourceFlag", "menu");
			paramsMap.put("contentFlag", "1");
			paramsMap.put("assocResourceFlag", "");
			paramsMap.put("assocResourceAttr", "");
			paramsMap.put("assocResourceAttrValue", "");
			List sourceData = securityService.getResource(paramsMap, true);
			for (Object object : sourceData) {
				Map menuMap = (Map) object;
				if (StringUtils.isBlank(menuMap.get("menu_desc").toString())) {
					continue;
				}
				if (!StringUtils.trim(menuMap.get("menu_desc").toString()).startsWith("{")) {
					continue;
				}
				Map menuObject = new JsonMapper().fromJson(menuMap.get("menu_desc").toString(), Map.class);
				menuObject.put("menu_flag", menuMap.get("menu_flag"));
				menuList.add(menuObject);
			}
			// menuList.forEach(menuItem -> {
			// System.out.println(menuItem.toString());
			// });
			return menuList;
			// Map restMap = restTemplate.getForObject(new
			// URI("http://127.0.0.1:8888/api/v1/system-menu"), Map.class);
			// Map embedded = new HashMap<>();
			// System.out.println(restMap.toString());
			// if(restMap.containsKey("_embedded")){
			// embedded = (Map)restMap.get("_embedded");
			// }
			// if(embedded.containsKey("system-menu")){
			// menuList = (List)embedded.get("system-menu");
			// }
		} catch (RestClientException e) {
			e.printStackTrace();
			return menuList;
		}
	}

	@Override
	public List<String> findDayAlarmCount() {
		Map<String, Integer> dhssNameAlarm = new HashMap<String, Integer>();
		List<AlarmReceiveRecord> record = this.findAlarm();
		List<String> AlarmNos = this.findNotAlarmNo();
		for (AlarmReceiveRecord alarmReceiveRecord : record) {
			if (!AlarmNos.contains(alarmReceiveRecord.getAlarmNo())) {
				String dhssName = !StringUtils.isNotBlank(alarmReceiveRecord.getDhssName()) ? "unknown"
						: alarmReceiveRecord.getDhssName();
				Integer value = dhssNameAlarm.get(dhssName);
				dhssNameAlarm.put(dhssName, value == null ? 1 : (value + 1));
			}
		}
		// TODO counter ?
		// Integer counter = 0;
		List<String> dhssCounts = new ArrayList<String>();
		for (String string : dhssNameAlarm.keySet()) {
			dhssCounts.add(string + " : " + dhssNameAlarm.get(string));
			// counter+=dhssNameAlarm.get(string);
		}
		// dhssCounts.add(counter+"");
		return dhssCounts;
	}

	// private List<String> findHomeKpiList(){
	//
	// }

	/*@Override
	public List<Map<String, Object>> findKpiCount() {
		List<Map<String, Object>> resultlist = new ArrayList<Map<String, Object>>();

		DecimalFormat df = new DecimalFormat("######0.00"); // 保留两位小数

		List<SearchFilter> searchFilterOR = new ArrayList<SearchFilter>();
		List<String> kpiCodes = HomeProjectProperties.getHomeKpiCodeList();
		for (String kpiCode : kpiCodes) {
			if (StringUtils.isNotEmpty(kpiCode)) {
				searchFilterOR.add(new SearchFilter("kpiCode", Operator.EQ, kpiCode));
			}
		}
		Specification<KpiConfig> speciFicationsOR = DynamicSpecifications.bySearchFilter(searchFilterOR,
				BooleanOperator.OR, KpiConfig.class);
		List<KpiConfig> items = kpiConfigRepository.findAll(Specifications.where(speciFicationsOR));
		Map<String, Object> itemMap = new HashMap<String, Object>();
		//// itemMap.put("kpiCode_IN", );
		// List<KpiConfig> items = findKpiItems(itemMap);

		for (KpiConfig kpiConfig : items) {
			Map<String, Object> listMap = new HashMap<String, Object>();
			listMap.put("name", kpiConfig.getKpiName());
			itemMap.put("kpiCode_EQ", kpiConfig.getKpiCode());
			List<KpiMonitor> list = findKpiMonitor(itemMap);
			Map<String, Integer> requestCountMap = new HashMap<String, Integer>();
			Map<String, Integer> successCountMap = new HashMap<String, Integer>();
			for (KpiMonitor monitor : list) {
				Integer requestCount = requestCountMap.get(monitor.getDhssName());
				Integer total = monitor.getKpiTotal() == null ? 0 : monitor.getKpiTotal();
				requestCountMap.put(monitor.getDhssName(), requestCount == null ? total : requestCount + total);
				Integer successCount = successCountMap.get(monitor.getDhssName());
				Integer success = monitor.getKpiValue() == null ? 0 : monitor.getKpiValue().intValue();
				successCountMap.put(monitor.getDhssName(), successCount == null ? success : successCount + success);
			}

			List<String> lMap = new ArrayList<String>();
			for (String key : requestCountMap.keySet()) {
				String value = "0";
				switch (kpiConfig.getKpiUnit()) {
				case "ratio":
					Integer success = successCountMap.get(key);
					Integer request = requestCountMap.get(key);
					if (request == null || request == 0) {
						value = "100%";
					} else {
						value = df.format(
								Double.parseDouble(success.toString()) / Double.parseDouble(request.toString()) * 100)
								+ "%";
					}
					break;
				// case "fail_count":
				// value = (requestCountMap.get(key) - successCountMap.get(key)) + "次";
				// break;
				case "value":
					value = successCountMap.get(key) + "";
					break;
				}
				lMap.add( key + " : " +  value);
			}
			listMap.put("list", lMap);
			listMap.put("kpiCode", kpiConfig.getKpiCode());
			listMap.put("color", randomColor());
			resultlist.add(listMap);

			if (resultlist.size() >= 5) {
				break;
			}
		}
		return resultlist;
	}*/

	@Override
	public List<Map<String, Object>> findKpiCount(String kpiCodeString) {
		List<Map<String, Object>> resultlist = new ArrayList<Map<String, Object>>();
		if (StringUtils.isEmpty(kpiCodeString)) {
			return resultlist;
		}
		DecimalFormat df = new DecimalFormat("######0.00"); // 保留两位小数

		List<SearchFilter> searchFilterOR = new ArrayList<SearchFilter>();
		String[] kpiCodes = kpiCodeString.split(",");
		for (String kpiCode : kpiCodes) {
			if (StringUtils.isNotEmpty(kpiCode)) {
				searchFilterOR.add(new SearchFilter("kpiCode", Operator.EQ, kpiCode));
			}
		}
		Specification<KpiConfig> speciFicationsOR = DynamicSpecifications.bySearchFilter(searchFilterOR,
				BooleanOperator.OR, KpiConfig.class);
		List<KpiConfig> items = kpiConfigRepository.findAll(Specifications.where(speciFicationsOR));
		Map<String, Object> itemMap = new HashMap<String, Object>();
		//// itemMap.put("kpiCode_IN", );
		// List<KpiConfig> items = findKpiItems(itemMap);

		for (KpiConfig kpiConfig : items) {
			Map<String, Object> listMap = new HashMap<String, Object>();
			listMap.put("name", kpiConfig.getKpiName());
			itemMap.put("kpiCode_EQ", kpiConfig.getKpiCode());
			List<KpiMonitor> list = findKpiMonitor(itemMap);
			Map<String, Integer> requestCountMap = new HashMap<String, Integer>();
			Map<String, Integer> successCountMap = new HashMap<String, Integer>();
			for (KpiMonitor monitor : list) {
				Integer requestCount = requestCountMap.get(monitor.getDhssName());
				Integer total = monitor.getKpiTotal() == null ? 0 : monitor.getKpiTotal();
				requestCountMap.put(monitor.getDhssName(), requestCount == null ? total : requestCount + total);
				Integer successCount = successCountMap.get(monitor.getDhssName());
				Integer success = monitor.getKpiValue() == null ? 0 : monitor.getKpiValue().intValue();
				successCountMap.put(monitor.getDhssName(), successCount == null ? success : successCount + success);
			}

			List<Map<String, String>> lMap = new ArrayList<Map<String, String>>();
			for (String key : requestCountMap.keySet()) {
				if(!StringUtils.isNotBlank(key)) {
					continue;
				}
				Map<String, String> valueMap = new HashMap<>();
				String value = "0";
				switch (kpiConfig.getKpiUnit()) {
					case "ratio":
						Integer success = successCountMap.get(key);
						Integer request = requestCountMap.get(key);
						if (request == null || request == 0) {
							value = "100%";
						} else {
							value = df.format(
									Double.parseDouble(success.toString()) / Double.parseDouble(request.toString()) * 100)
									+ "%";
						}
						break;
					case "value":
						value = successCountMap.get(key) + "";
						break;
				}
				valueMap.put("key", key);
				valueMap.put("value", value);
				lMap.add(valueMap);
			}
			listMap.put("list", lMap);
			listMap.put("kpiCode", kpiConfig.getKpiCode());
			listMap.put("color", randomColor());
			resultlist.add(listMap);

			if (resultlist.size() >= 5) {
				break;
			}
		}
		return resultlist;
	}

	private int randomColor() {
		Random random = new java.util.Random();// 定义随机类
		int result = random.nextInt(9);// 返回[0,10)集合中的整数，注意不包括10
		return result + 1;
	}

	@Override
	public List<KpiConfig> findKpiItems(Map<String, Object> paramMap) {
		Map<String, SearchFilter> filter = SearchFilter.parse(paramMap);
		Specification<KpiConfig> spec = DynamicSpecifications.bySearchFilter(filter.values(), BooleanOperator.OR,
				KpiConfig.class);
		return kpiConfigRepository.findAll(spec);
	}

	@Override
	public List<KpiMonitor> findKpiMonitor(Map<String, Object> paramMap) {
		Map<String, SearchFilter> filter = SearchFilter.parse(paramMap);
		Specification<KpiMonitor> spec = DynamicSpecifications.bySearchFilter(filter.values(), BooleanOperator.AND,
				KpiMonitor.class);
		return kpiMonitorRepository.findAll(spec);

	}

	/**
	 * 查询非重要告警的告警号
	 *
	 * @return
	 */
	private List<String> findNotAlarmNo() {
		List<String> notAlarmNo = new ArrayList<String>();
		List<NotImportantAlarm> notAlarmNos = notImportantAlarmRepository.findAll();
		for (NotImportantAlarm notImportantAlarm : notAlarmNos) {
			notAlarmNo.add(notImportantAlarm.getAlarmNum());
		}
		return notAlarmNo;
	}

	/**
	 * 查询当天全部告警信息
	 *
	 * @return
	 */
	private List<AlarmReceiveRecord> findAlarm() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Date date = new Date();
		String str = sdf.format(date);
		try {
			modelMap.put("receiveStartTime_GE", sdf1.parse(str + " 00:00:00"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Map<String, SearchFilter> filter = SearchFilter.parse(modelMap);
		Specification<AlarmReceiveRecord> spec = DynamicSpecifications.bySearchFilter(filter.values(),
				BooleanOperator.AND, AlarmReceiveRecord.class);
		return alarmReceiveRecordRepository.findAll(spec);
	}
}
