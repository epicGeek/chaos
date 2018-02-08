package com.nokia.ices.app.dhss.kpi.service.impl;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.nokia.ices.app.dhss.domain.kpi.KpiConfig;
import com.nokia.ices.app.dhss.domain.kpi.KpiMonitor;
import com.nokia.ices.app.dhss.kpi.config.CustomSettings;
import com.nokia.ices.app.dhss.kpi.service.KpiTaskService;
import com.nokia.ices.app.dhss.kpi.task.TaskKPI;
import com.nokia.ices.app.dhss.repository.kpi.KpiConfigRepository;

@Component
public class KpiTaskServiceImpl implements KpiTaskService {

	private Logger logger = LogManager.getLogger(TaskKPI.class);
	private static final DecimalFormat percentFormat = new DecimalFormat("######0.00");

	@Autowired
	private CustomSettings appCustomSettings;

	@Autowired
	private JdbcTemplate jdbcTemplate;// mysql 数据源

	@Autowired
	@Qualifier("jdbcTemplateSource")
	private JdbcTemplate jdbcTemplateSource; // OSS数据源

//	@Autowired
//	@Qualifier("jdbcTemplateTarget")
//	private JdbcTemplate jdbcTemplateTarget; // Target数据源

	@Autowired
	private KpiConfigRepository kpiConfigRepository; // KPI-SETTING

	// @Autowired
	// private KpiMonitorRepository kpiMonitorRepository; // KPI-SETTING

//	private static final String condition = "peroid_start_time > 1.5/24";

	private static final String opt_param = "/*+ opt_param('_optimizer_use_feedback' 'false')*/";

	@Override
	public boolean connectVertify() {
		Boolean kpiConfigConnAvailable = false;
		Boolean outputKpiConnAvailable = false;
		Boolean oracleOssConnAvailable = false;
		try {
			Map<String,Object> resultMap = jdbcTemplate.queryForMap("SELECT 'CONFIG'");
			logger.info("KPI-config database is still available."+resultMap.toString());
			kpiConfigConnAvailable = true;
		} catch (Exception e) {
			logger.info("KPI-config database is unavailable.");
			logger.info(e.getMessage());
		}

		try {
//			Map<String,Object> resultMap = jdbcTemplateTarget.queryForMap("SELECT 'TARGET'");
			Map<String,Object> resultMap = jdbcTemplate.queryForMap("SELECT 'TARGET'");
			logger.info("Output-KPI database is still available."+resultMap.toString());
			outputKpiConnAvailable = true;
		} catch (Exception e) {
			logger.info("Output-KPI database is unavailable");
			logger.info(e.getMessage());
		}

		try {
			Map<String,Object> resultMap = jdbcTemplateSource.queryForMap("SELECT 'SOURCE' FROM DUAL");
			logger.info("Oracle OSS database is available."+resultMap.toString());
			oracleOssConnAvailable = true;
		} catch (Exception e) {
			logger.info("Oracle OSS database is unavailable");
			logger.info(e.getMessage());
		}
		logger.info("Connection details:");
		logger.info("Is KPI-config Database available?    :"+kpiConfigConnAvailable);
		logger.info("Is Output target Database available? :"+outputKpiConnAvailable);
		logger.info("Is Oracle OSS Database available?    :"+oracleOssConnAvailable);
		if(kpiConfigConnAvailable&&outputKpiConnAvailable&&oracleOssConnAvailable){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public List<KpiConfig> getKpiConfigList() {
		List<KpiConfig> configList = new ArrayList<KpiConfig>();
		Iterable<KpiConfig> configListIt = kpiConfigRepository.findAllByKpiEnabledIsTrue();
		configListIt.forEach(kpiConfigItem -> {
			configList.add(kpiConfigItem);
		});
		return configList;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public JdbcTemplate getJdbcTemplateSource() {
		return jdbcTemplateSource;
	}

	public void setJdbcTemplateSource(JdbcTemplate jdbcTemplateSource) {
		this.jdbcTemplateSource = jdbcTemplateSource;
	}
//
//	public JdbcTemplate getJdbcTemplateTarget() {
//		return jdbcTemplateTarget;
//	}
//
//	public void setJdbcTemplateTarget(JdbcTemplate jdbcTemplateTarget) {
//		this.jdbcTemplateTarget = jdbcTemplateTarget;
//	}

	public KpiConfigRepository getKpiConfigRepository() {
		return kpiConfigRepository;
	}

	public void setKpiConfigRepository(KpiConfigRepository kpiConfigRepository) {
		this.kpiConfigRepository = kpiConfigRepository;
	}
	public void clearCurrentKpiMonitor(String kpiCode) {
//		jdbcTemplateTarget.update("delete from kpi_monitor where kpi_code = ?", new Object[] { kpiCode });
		jdbcTemplate.update("delete from kpi_monitor where kpi_code = ?", new Object[] { kpiCode });
	}

	@Override
	public Map<String, Integer> loadDataByKpiDefineList(List<KpiConfig> kpiDefineList) {
		Map<String, Integer> kpiData = new HashMap<String, Integer>();
		String filterMode = appCustomSettings.getFilterMode();
		logger.info("Filter mode:"+filterMode);
		for (KpiConfig kpiConfig : kpiDefineList) {
			String loadScript = kpiConfig.getKpiQueryScript();
			String kpiCode = kpiConfig.getKpiCode();
			String kpiName = kpiConfig.getKpiName();
			String kpiCategory = kpiConfig.getKpiCategory();

			String kpiNeType = kpiConfig.getKpiNeType();

			String kpiComparedMethod = kpiConfig.getComparedMethod();
			// Integer kpiRequestSample = kpiConfig.getRequestSample();
			// String kpiThreshold = kpiConfig.getThreshold();

			String kpiUnit = kpiConfig.getKpiUnit();
			// logger.info(loadScript);

			loadScript = loadScript.replaceFirst("SELECT ", "SELECT " + opt_param + " ");
			List<KpiMonitor> kpiResultFromSource = new ArrayList<KpiMonitor>();
			try {
				kpiResultFromSource = jdbcTemplateSource.query(loadScript, new Object[] {},
						new BeanPropertyRowMapper<KpiMonitor>(KpiMonitor.class));
			} catch (Exception e) {
				logger.info("kpiCode:" + kpiCode + "\n" + e.getMessage());
				e.printStackTrace();
			}
			List<Object[]> kpiResultToAlarm = new ArrayList<Object[]>();

			for (KpiMonitor kpiMonitorItem : kpiResultFromSource) {
				kpiMonitorItem.setKpiCode(kpiCode);
				kpiMonitorItem.setKpiName(kpiName);
				kpiMonitorItem.setKpiCategory(kpiCategory);
				kpiMonitorItem.setKpiUnit(kpiUnit);
				// neType 默认值从 kpiConfig 中获得,如果dn匹配到某个单元，用单元的网元类型
				kpiMonitorItem.setNeType(kpiNeType);
				kpiMonitorItem.setKpiCompareMethod(kpiComparedMethod);
				generateOutPutValue(kpiMonitorItem);
				setNeUnitInfo(kpiMonitorItem);
				// 设置自定义告警
				try {
					Object[] objAlarm = processAlarmInfo(kpiMonitorItem, kpiConfig);
					if (objAlarm.length != 0) {
						kpiResultToAlarm.add(objAlarm);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			clearCurrentKpiMonitor(kpiCode);
			kpiResultFromSource = kpiFilter(kpiResultFromSource,filterMode);
			saveKpiMonitor(kpiResultFromSource);
			try {
				sendAlarm(kpiResultToAlarm);
			} catch (Exception e) {
				logger.info(e.getMessage() + " Can't send custom alarm");
			}
			kpiData.put(kpiConfig.getKpiCode(), kpiResultFromSource.size());

		}
		return kpiData;
	}

	private List<KpiMonitor> kpiFilter(List<KpiMonitor> kpiResultFromSource,String filterMode) {
		List<KpiMonitor> filteredKpiList = new ArrayList<>();
		if (filterMode.equals("fujian-fz")) {
			for (KpiMonitor kpiMonitor : kpiResultFromSource) {
				if (!kpiMonitor.getUnitName().contains("PLMN")) {
					filteredKpiList.add(kpiMonitor);
					logger.info(kpiMonitor.toString());
				}
			}
			return filteredKpiList;
		} else if (filterMode.equals("fujian-qz")) {
			for (KpiMonitor kpiMonitor : kpiResultFromSource) {
				if (!kpiMonitor.getUnitName().contains("PLMN")) {
					filteredKpiList.add(kpiMonitor);
				}
			}
			return filteredKpiList;
		}else{
			return kpiResultFromSource;
		}
	}

	private void sendAlarm(List<Object[]> kpiResultToAlarm) {
		String alarmSQL = (appCustomSettings.getAddCustomAlarm());
		//jdbcTemplateTarget.batchUpdate(alarmSQL, kpiResultToAlarm);
		jdbcTemplate.batchUpdate(alarmSQL, kpiResultToAlarm);
	}

	private void generateOutPutValue(KpiMonitor kpiMonitorItem) {
		Number formattedDoubleData = -1d;

		if (kpiMonitorItem.getKpiTotal() != null && kpiMonitorItem.getKpiTotal() != 0) {
			Double value = kpiMonitorItem.getKpiValue();
			Integer total = kpiMonitorItem.getKpiTotal();
			formattedDoubleData = value * 100 / total;
		} else {
			formattedDoubleData = kpiMonitorItem.getKpiValue();
		}
		try {
			formattedDoubleData = percentFormat.parse(percentFormat.format(formattedDoubleData));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		kpiMonitorItem.setKpiOutputValue(formattedDoubleData.doubleValue());

	}

	private Object[] processAlarmInfo(KpiMonitor kpiMonitor, KpiConfig kpiConfig) {

		String kpiComparedMethod = kpiConfig.getComparedMethod();
		Integer kpiRequestSample = kpiConfig.getRequestSample();

		if (StringUtils.isEmpty(kpiConfig.getThreshold()) || StringUtils.isEmpty(kpiComparedMethod)) {
			return new Object[] {};
		}
		Double kpiThreshold = Double.parseDouble(kpiConfig.getThreshold());

		Double kpiValue = kpiMonitor.getKpiOutputValue();

		Integer kpiTotal = kpiMonitor.getKpiTotal();

		String monitorString = kpiConfig.getMonitorTimeString();
		Date kpiPeriodStartTime = kpiMonitor.getPeriodStartTime();
		boolean isKpiTimeInMonitorTime = isKpiTimeInMonitorTime(kpiPeriodStartTime, monitorString);
		if (isKpiValueOverload(kpiThreshold, kpiValue, kpiComparedMethod) && kpiTotal != null
				&& kpiTotal > kpiRequestSample && isKpiTimeInMonitorTime) {

			String alarmTemplate = (appCustomSettings.getAlarmTemplate());
			String alarmContent = String.format(alarmTemplate, new Object[] { kpiMonitor.getKpiName(),
					kpiMonitor.getKpiCode(), kpiMonitor.getKpiOutputValue(), kpiThreshold });
			return new Object[] { alarmContent, kpiConfig.getAlarmLevel(), null, kpiConfig.getKpiName(), "KPI",
					kpiMonitor.getNeSite(), "", kpiMonitor.getNeName(), kpiMonitor.getNeType(), new Date(),
					kpiConfig.getThreshold(), null, kpiMonitor.getUnitName(), kpiMonitor.getUnitType(),
					kpiMonitor.getKpiCode() };

		}
		return new Object[] {};

	}

	private boolean isKpiValueOverload(Double kpiThreshold, Double kpiValue, String compareMethod) {
		if (">".equalsIgnoreCase(compareMethod.trim()))
			return kpiValue > kpiThreshold;
		if ("<".equalsIgnoreCase(compareMethod.trim()))
			return kpiValue < kpiThreshold;
		return false;
	}
	private boolean isKpiTimeInMonitorTime(Date periodStartTime,String monitorTimeStirng){
		boolean isKpiTimeInMonitorTime = false;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
			Integer kpiTime = Integer.valueOf(sdf.format(periodStartTime));
			String startTimeStr = monitorTimeStirng.split("-")[0].replace(":", "");
			String endTimeStr = monitorTimeStirng.split("-")[1].replace(":", "");
			Integer startTime = Integer.valueOf(startTimeStr);
			Integer endTime = Integer.valueOf(endTimeStr);
			if(kpiTime>startTime && kpiTime<endTime){
				isKpiTimeInMonitorTime = true;
			}
			return isKpiTimeInMonitorTime;
		} catch (Exception e) {
			e.printStackTrace();
			return isKpiTimeInMonitorTime;
		}
	}

	private boolean saveKpiMonitor(List<KpiMonitor> kpiResultFromSource) {
		// logger.info(customSettings.getInsertKpiCurrentTable());
		List<Object[]> paramArray = new ArrayList<Object[]>();

		for (KpiMonitor kpiMonitorItem : kpiResultFromSource) {
			Object[] singleParam = new Object[] { kpiMonitorItem.getKpiCode(), kpiMonitorItem.getKpiName(),
					kpiMonitorItem.getKpiCategory(), kpiMonitorItem.getKpiValue(), kpiMonitorItem.getKpiTotal(),
					kpiMonitorItem.getKpiOutputValue(), kpiMonitorItem.getPeriodStartTime(),
					kpiMonitorItem.getDhssName(), kpiMonitorItem.getNeSite(), kpiMonitorItem.getNeId(),
					kpiMonitorItem.getNeName(), kpiMonitorItem.getNeType(), kpiMonitorItem.getCoGid(),
					kpiMonitorItem.getUnitId(), kpiMonitorItem.getUnitName(), kpiMonitorItem.getUnitType(),

					kpiMonitorItem.getKpiUnit(), kpiMonitorItem.getKpiCompareMethod(), kpiMonitorItem.getUnitNext(),
					kpiMonitorItem.getUnitNextId() };
			paramArray.add(singleParam);
		}
		try {
//			jdbcTemplateTarget.batchUpdate(appCustomSettings.getInsertKpiCurrentTable(), paramArray);
			jdbcTemplate.batchUpdate(appCustomSettings.getInsertKpiCurrentTable(), paramArray);
	//		jdbcTemplateTarget.batchUpdate(appCustomSettings.getInsertKpiHistoryTable(), paramArray);
			jdbcTemplate.batchUpdate(appCustomSettings.getInsertKpiHistoryTable(), paramArray);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	// @Override
	// public Map<String, Map<String, Object>> getEquipmentUnitMap() {
	// List<Map<String, Object>> list = jdbcTemplate.queryForList(
	// "select distinct co_gid,id as
	// unit_id,unit_name,unit_type,ne_id,ne_type,ne_name,ne_site,dhss_name"
	// + " from equipment_unit where co_gid is not null and co_gid <> ''");
	// Map<String, Map<String, Object>> resultMap = new HashMap<String,
	// Map<String, Object>>();
	// list.forEach(neInfo -> {
	// resultMap.put(neInfo.get("co_gid").toString(), neInfo);
	// });
	// return resultMap;
	// }

	@Override
	public List<Map<String, Object>> getEquipmentUnitMapList() {
		List<Map<String, Object>> list = jdbcTemplate.queryForList(
				"select distinct co_gid,id as unit_id,unit_name,unit_type,ne_id,ne_type,ne_name,ne_site,dhss_name"
						+ " from equipment_unit where co_gid is not null and co_gid <> ''");
		return list;
	}

	@Override
	public Map<String, Map<String, Object>> getEquipmentNeMap() {
		List<Map<String, Object>> list = jdbcTemplateSource
				.queryForList("select distinct to_char(co_gid) as co_gid,co_dn from utp_common_objects "
						+ " where co_gid <> 0 and co_dn is not null ");
		Map<String, Map<String, Object>> resultMap = new HashMap<String, Map<String, Object>>();
		list.forEach(neInfo -> {
			resultMap.put(neInfo.get("co_gid").toString(), neInfo);
		});
		return resultMap;
	}

	private void setNeUnitInfo(KpiMonitor kpiMonitor) {
		String coGID = kpiMonitor.getCoGid();

		Map<String, Object> unitInfo = TaskKPI.getEquipmentUnitMap().get(coGID);

		if (unitInfo == null) {
			// 如果找不到co_gid的精确匹配，开始按照 unitName 模糊匹配
			// step1.根据co_gid找到dn(网管数据)
			Map<String, Object> dnInfo = TaskKPI.getEquipmentNeMap().get(coGID);
			if (dnInfo == null) {
				logger.info(kpiMonitor.toString());
				return;
			}
			String dn = dnInfo.getOrDefault("co_dn", "").toString();
			// step2.根据dn来匹配 从 namekey 里面查找dn
			TaskKPI.getEquipmentUnitMapUnitNameKey().forEach((unitNameKey, unitItem) -> {
				// 2.1 赋值条件 key 模糊匹配到并且没有被赋值过
				if (dn.contains(unitNameKey) && StringUtils.isEmpty(kpiMonitor.getUnitId())) {
					fillKpiMonitorField(kpiMonitor, unitItem);
				}
			});
			// step3. 如果循环完毕unit_id也没有被赋值,直接输出dn作为unitName
			if (StringUtils.isEmpty(kpiMonitor.getUnitId())) {
				kpiMonitor.setUnitName(dn);
			}
		} else {
			fillKpiMonitorField(kpiMonitor, unitInfo);
		}
	}

	private void fillKpiMonitorField(KpiMonitor kpiMonitor, Map<String, Object> unitItem) {
		String unitId = unitItem.getOrDefault("unit_id", "")==null?"":unitItem.getOrDefault("unit_id", "").toString();
		kpiMonitor.setUnitId(unitId);
		String unitName = unitItem.getOrDefault("unit_name", "")==null?"":unitItem.getOrDefault("unit_name", "").toString();
		kpiMonitor.setUnitName(unitName);
		String unitType = unitItem.getOrDefault("unit_type", "")==null?"":unitItem.getOrDefault("unit_type", "").toString();
		kpiMonitor.setUnitType(unitType);
		String neId = unitItem.getOrDefault("ne_id", "") == null?"":unitItem.getOrDefault("ne_id", "").toString();
		kpiMonitor.setNeId(neId);
		String neName = unitItem.getOrDefault("ne_name", "") == null?"":unitItem.getOrDefault("ne_name", "").toString();
		kpiMonitor.setNeName(neName);
		String neType = unitItem.getOrDefault("ne_type", "") == null ?"":unitItem.getOrDefault("ne_type", "").toString();
		kpiMonitor.setNeType(neType);
		String neSite = unitItem.getOrDefault("ne_site", "") == null ?"":unitItem.getOrDefault("ne_site", "").toString();
		kpiMonitor.setNeSite(neSite);
		String dhssName = unitItem.getOrDefault("dhss_name", "") == null ? "":unitItem.getOrDefault("dhss_name", "").toString();
		kpiMonitor.setDhssName(dhssName);
	}

	public CustomSettings getAppCustomSettings() {
		return appCustomSettings;
	}

	public void setAppCustomSettings(CustomSettings appCustomSettings) {
		this.appCustomSettings = appCustomSettings;
	}

	@Override
	public void deleteOldData() {
		String sql = "delete from kpi_monitor_history where period_start_time < '#time_stamp#'";
		Integer saveMonth = getAppCustomSettings().getHoldKpiDataMonth();
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -saveMonth);
		Date saveDate = c.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timeStamp = sdf.format(saveDate);
		sql = sql.replace("#time_stamp#", timeStamp);
		try {
			logger.info("Delete old data SQL:");
			logger.info(sql);
			//jdbcTemplateTarget.execute(sql);
			jdbcTemplate.execute(sql);
			logger.info("Delete success.");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
