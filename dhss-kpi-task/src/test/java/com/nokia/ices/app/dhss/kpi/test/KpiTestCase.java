package com.nokia.ices.app.dhss.kpi.test;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import com.nokia.ices.app.dhss.domain.kpi.KpiConfig;
import com.nokia.ices.app.dhss.domain.kpi.KpiMonitor;
import com.nokia.ices.app.dhss.kpi.config.CustomSettings;
import com.nokia.ices.app.dhss.kpi.service.KpiTaskService;


@RunWith(SpringRunner.class)
@SpringBootTest
public class KpiTestCase {
	private static final DecimalFormat percentFormat = new DecimalFormat("######0.00");
	private static final Logger LOGGER = LoggerFactory.getLogger(KpiTestCase.class);
	@Autowired
	private KpiTaskService kpiTaskService;
	@Autowired
	private CustomSettings appCustomSettings;
	@Autowired
	private JdbcTemplate jdbcTemplate;// mysql 数据源
	@Autowired
	@Qualifier("jdbcTemplateSource")
	private JdbcTemplate jdbcTemplateSource; // OSS数据源
	private static final SimpleDateFormat standardTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static Map<String, Map<String, Object>> equipmentUnitMap = Collections
			.synchronizedMap(new HashMap<String, Map<String, Object>>());
	private static Map<String, Map<String, Object>> equipmentUnitMapUnitNameKey = Collections
			.synchronizedMap(new HashMap<String, Map<String, Object>>());
	private static Map<String, Map<String, Object>> equipmentNeMap = Collections
			.synchronizedMap(new HashMap<String, Map<String, Object>>());
	private static final String opt_param = "/*+ opt_param('_optimizer_use_feedback' 'false')*/";
	@Test
	public void testKpiBug(){
		Long startTimeStamp = System.currentTimeMillis();
		String startTime = new DateTime(startTimeStamp).toString(standardTimeFormat.toPattern());
		LOGGER.info(startTime);
		// load 网元信息缓存
		equipmentUnitMap.clear();
		equipmentUnitMapUnitNameKey.clear();

		List<Map<String,Object>> equipmentUnitMapList = kpiTaskService.getEquipmentUnitMapList();
		equipmentUnitMapList.forEach(unitItem -> {
			equipmentUnitMap.put(unitItem.get("co_gid").toString(), unitItem);
			equipmentUnitMapUnitNameKey.put(unitItem.get("unit_name").toString(), unitItem);
		});
		LOGGER.info("equipmentUnitMap and equipmentUnitMapUnitNameKey is updated,size =" + equipmentUnitMap.size());
		equipmentNeMap.clear();
		equipmentNeMap = getMockDeviceInfoMap();
		LOGGER.info("equipmentNeMap is updated,size =" + equipmentNeMap.size());
		// load 网元信息缓存结束
		List<KpiConfig> kpiDefineList = kpiTaskService.getKpiConfigList();
		LOGGER.info("Kpi congig count:"+kpiDefineList.size());
		Map<String, Integer> kpiDataMap = mockTestData(kpiDefineList);
		kpiDataMap.forEach((key, value) -> {
			LOGGER.info(key + " -> " + value);
		});
		Long endTimeStamp = System.currentTimeMillis();
		Long periodTime = (endTimeStamp - startTimeStamp);
		String endTime = new DateTime(endTimeStamp).toString(standardTimeFormat.toPattern());
		LOGGER.info(endTime + " Cost:" + periodTime);

	}
	private Map<String, Map<String, Object>> getMockDeviceInfoMap() {
		// TODO Auto-generated method stub
		String sql = "select CO_GID as co_gid,CO_DN as co_gid from oracle_co_table";
		List<Map<String, Object>> list = jdbcTemplate
				.queryForList(sql);
		Map<String, Map<String, Object>> resultMap = new HashMap<String, Map<String, Object>>();
		list.forEach(neInfo -> {
			resultMap.put(neInfo.get("co_gid").toString(), neInfo);
		});
		return resultMap;
	}
	private Map<String, Integer> mockTestData(List<KpiConfig> kpiDefineList) {

		Map<String, Integer> kpiData = new HashMap<String, Integer>();
		for (KpiConfig kpiConfig : kpiDefineList) {
		//	String loadScript = kpiConfig.getKpiQueryScript();
			String loadScript = "SELECT * FROM oracle_cancel_data";
			String kpiCode = kpiConfig.getKpiCode();
			String kpiName = kpiConfig.getKpiName();
			String kpiCategory = kpiConfig.getKpiCategory();

			String kpiNeType = kpiConfig.getKpiNeType();

			String kpiComparedMethod = kpiConfig.getComparedMethod();
			// Integer kpiRequestSample = kpiConfig.getRequestSample();
			// String kpiThreshold = kpiConfig.getThreshold();

			String kpiUnit = kpiConfig.getKpiUnit();
			// logger.info(loadScript);

		//	loadScript = loadScript.replaceFirst("SELECT ", "SELECT " + opt_param + " ");
			List<KpiMonitor> kpiResultFromSource = new ArrayList<KpiMonitor>();
			try {
				kpiResultFromSource = jdbcTemplate.query(loadScript, new Object[] {},
						new BeanPropertyRowMapper<KpiMonitor>(KpiMonitor.class));
			} catch (Exception e) {
				LOGGER.error("kpiCode:" + kpiCode + "\n" + e.getMessage());
			}
		//	List<Object[]> kpiResultToAlarm = new ArrayList<Object[]>();

			for (KpiMonitor kpiMonitorItem : kpiResultFromSource) {
				//if(kpiMonitorItem.getKpiValue()>0 && kpiMonitorItem.getKpiTotal()>0){
					kpiMonitorItem.setKpiCode(kpiCode);
					kpiMonitorItem.setKpiName(kpiName);
					kpiMonitorItem.setKpiCategory(kpiCategory);
					kpiMonitorItem.setKpiUnit(kpiUnit);
					
					// neType 默认值从 kpiConfig 中获得,如果dn匹配到某个单元，用单元的网元类型
					kpiMonitorItem.setNeType(kpiNeType);
					
					kpiMonitorItem.setKpiCompareMethod(kpiComparedMethod);

					generateOutPutValue(kpiMonitorItem);
					setNeUnitInfo(kpiMonitorItem);

			//	}

				// 设置自定义告警
//				try {
//					Object[] objAlarm = processAlarmInfo(kpiMonitorItem, kpiConfig);
//					if (objAlarm.length != 0) {
//						kpiResultToAlarm.add(objAlarm);
//					}
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
			}
			clearCurrentKpiMonitor(kpiCode);
			saveKpiMonitor(kpiResultFromSource);
			// 保存自定义告警
//			try {
//				sendAlarm(kpiResultToAlarm);
//			} catch (Exception e) {
//				logger.error(e.getMessage() + " Can't send custom alarm");
//			}
			kpiData.put(kpiConfig.getKpiCode(), kpiResultFromSource.size());

		}
		return kpiData;
	
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
			LOGGER.error(e.getMessage());
		}
		return false;
	}
	public void clearCurrentKpiMonitor(String kpiCode) {
//		jdbcTemplateTarget.update("delete from kpi_monitor where kpi_code = ?", new Object[] { kpiCode });
		jdbcTemplate.update("delete from kpi_monitor where kpi_code = ?", new Object[] { kpiCode });
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
	

	private void setNeUnitInfo(KpiMonitor kpiMonitor) {
		String coGID = kpiMonitor.getCoGid();

		Map<String, Object> unitInfo = KpiTestCase.getEquipmentUnitMap().get(coGID);

		if (unitInfo == null) {
			// 如果找不到co_gid的精确匹配，开始按照 unitName 模糊匹配
			// step1.根据co_gid找到dn(网管数据)
			Map<String, Object> dnInfo = KpiTestCase.getEquipmentNeMap().get(coGID);
			if (dnInfo == null) {
				LOGGER.info(kpiMonitor.toString());
				return;
			}
			String dn = dnInfo.getOrDefault("co_dn", "").toString();
			// step2.根据dn来匹配 从 namekey 里面查找dn
			KpiTestCase.getEquipmentUnitMapUnitNameKey().forEach((unitNameKey, unitItem) -> {
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

	public static Map<String, Map<String, Object>> getEquipmentUnitMap() {
		return equipmentUnitMap;
	}

	public static void setEquipmentUnitMap(Map<String, Map<String, Object>> equipmentUnitMap) {
		KpiTestCase.equipmentUnitMap = equipmentUnitMap;
	}

	public static Map<String, Map<String, Object>> getEquipmentNeMap() {
		return equipmentNeMap;
	}

	public static void setEquipmentNeMap(Map<String, Map<String, Object>> equipmentNeMap) {
		KpiTestCase.equipmentNeMap = equipmentNeMap;
	}
	public static Map<String, Map<String, Object>> getEquipmentUnitMapUnitNameKey() {
		return equipmentUnitMapUnitNameKey;
	}

	public static void setEquipmentUnitMapUnitNameKey(Map<String, Map<String, Object>> equipmentUnitMapUnitNameKey) {
		KpiTestCase.equipmentUnitMapUnitNameKey = equipmentUnitMapUnitNameKey;
	}
}
