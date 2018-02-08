package com.nokia.ices.app.dhss.kpi.task;

//@Component
//@EnableScheduling
//public class TaskKPIPeiVersion {
//	private static final Logger logger = LoggerFactory.getLogger(TaskKPIPeiVersion.class);
////	private static Map<String, EquipmentNe> equipmentNeMap = new HashMap<String, EquipmentNe>();
////	private static Map<String, EquipmentUnit> equipmentUnitMap = new HashMap<String, EquipmentUnit>();
//	private static final SimpleDateFormat standardTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	private static final SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
//
////	@Autowired
////	private AlarmMonitorRepository alarmMonitorRepository;
//	
//	@Autowired
//	private JdbcTemplate jdbcTemplate;//mysql 数据源
//
//	@Autowired
//	@Qualifier("jdbcTemplateOss")
//	private JdbcTemplate jdbcTemplateOss; //OSS数据源
//	
//	//@Scheduled(cron = "0 5/15 * * * ?") //每小时5,20,35,50跑
//	//@Scheduled(initialDelay = 200, fixedDelay = 30000000) // local test
//	
//	
////	@Scheduled(cron = "0 0/1 * * * ?")
////		public void judgeToStart() throws IOException{
////			List<Map<String, Object>> listoss = jdbcTemplateOss.queryForList("select sysdate from dual");
////			Date ossTime = (Date)listoss.get(0).get("SYSDATE");
////			String ossTimeMinStr = minuteTimeFormat.format(ossTime);
////			logger.info("OSS time:" + ossTimeMinStr);
////			if(ossTimeMinStr.equalsIgnoreCase("05")||ossTimeMinStr.equalsIgnoreCase("20")||ossTimeMinStr.equalsIgnoreCase("35")||ossTimeMinStr.equalsIgnoreCase("50")){
////				logger.info("Required time on!:"+ossTimeMinStr);
////				executeKPITask();
////			}else{
////				logger.info("NOT REQUIRED TIME.Exit." + ossTimeMinStr);
////			}
////		}
//	//@Scheduled(initialDelay = 200, fixedDelay = 30000) // local test
//	@Scheduled(cron = "${spring.report.task-kpi-cron}") //每小时5,20,35,50跑
//	public void executeKPITask() {
//		getEquipmentInfo();
//		getOSSData();
//		cancelKpiAlarm();
//		judgeToAlarm();
//	}
//	@Scheduled(cron = "${spring.report.task-kpi-cron-daily}")
//	private void deleteExpiredData() {
//		
//	}
//	/**
//	 * 删除一个月之前的数据
//	 */
//	private void deleteOldData() {
//		try {
//		 	Calendar today = Calendar.getInstance();
//		 	Integer holdMonth = CustomSettings.getHoldKpiDataMonth();
//		 	logger.info("hold kpi data for "+holdMonth+" months");
//		 	today.add(Calendar.MONTH, -holdMonth);//获得一个月前的时间点
//	        String targetTime = standardTimeFormat.format(today.getTime());
//		 	String deleteSql = "DELETE FROM quota_monitor_history WHERE period_start_time < '"+targetTime+"'";
//		 	logger.info(deleteSql);
//		 	//删除一个月（默认）前的数据。
//		 	jdbcTemplate.update(deleteSql);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	/**
//	 * 查找有效的门限来判断是否触发告警
//	 */
//	private void judgeToAlarm() {}
//	/**
//	 * 门限判断方法
//	 * @param kpiThresholdList 门限配置
//	 * @param KpiMonitorList 最新周期KPI
//	 */
////	private void compareKpiAndThreshold(List<KpiConfig> kpiThresholdList, List<KpiMonitor> KpiMonitorList) {
////		for (KpiConfig kpiThreshold : kpiThresholdList) {
////			logger.info("Kpi threshold config --> kpi name:"+kpiThreshold.getKpiName()+",kpi code:"+kpiThreshold.getKpiCode()+", compared_method:"+kpiThreshold.getComparedMethod()+",threshold:"+kpiThreshold.getThreshold()+",cancel value:"+kpiThreshold.getThresholdCancel()+",monitoring time:"+kpiThreshold.getMonitorTimeString()+",base sample :"+kpiThreshold.getRequestSample());
////			for (KpiMonitor KpiMonitor : KpiMonitorList) {
////				if(KpiMonitor.getKpiCode().equals(kpiThreshold.getKpiCode())){//match kpi and its threshold
////					//KPICODE是这条KPI的后台标识，与KPI一对一且不重复。
////					Integer startHour = Integer.valueOf(kpiThreshold.getMonitorTimeString().split("-")[0]);
////					Integer stopHour = Integer.valueOf(kpiThreshold.getMonitorTimeString().split("-")[1]);
////					Integer kpiTime = Integer.valueOf(hourFormat.format(KpiMonitor.getPeriodStartTime()));
////					//告警条件 1：这条KPI的发生时间应该发生在监控时间段内
////						if(startHour<kpiTime&&kpiTime<stopHour){
//////							logger.info("monitor time string:"+kpiThreshold.getMonitorTimeString());
//////							logger.info("start hour:"+startHour+",stop hour:"+stopHour);
//////							logger.info("kpi time:"+KpiMonitor.getPeriodStartTime()+",kpi hour:"+kpiTime);
////							//if this kpi happened in the monitoring time:
////							//告警条件2：这条KPI的请求次数应该足够大，大于配置里的REQUESTSAMPLE这个字段。
////							if(KpiMonitor.getKpiRequestCount()>kpiThreshold.getRequestSample()){
////								//request is big enough
////								logger.info("kpi request count:"+KpiMonitor.getKpiRequestCount()+",base sample:"+kpiThreshold.getRequestSample());
////								
////								//根据门限判断方法来判断 这条KPI是不是应该触发告警。
////								if(kpiThreshold.getComparedMethod().equals("<")){
////									if(KpiMonitor.getKpiValue() < Double.valueOf(kpiThreshold.getThreshold())){
////										setAlarmContent(KpiMonitor, kpiThreshold);
////										logger.info("A kpi alarm has happened: this kpi value is lower than its threshold");
////										logger.info("this kpi:"+KpiMonitor.getKpiValue());
////										logger.info("its threshold config:"+kpiThreshold.getThreshold());
////									}
////								}
////								if(kpiThreshold.getComparedMethod().equals(">")){
////									if(KpiMonitor.getKpiValue() > Double.valueOf(kpiThreshold.getThreshold())){
////										setAlarmContent(KpiMonitor, kpiThreshold);
////										logger.info("A kpi alarm has happened: this kpi value is bigger than its threshold");
////										logger.info("this kpi:"+KpiMonitor.getKpiValue());
////										logger.info("its threshold config:"+kpiThreshold.getThreshold());
////									}
////								}
////							}
////						}
////				}
////			}
////		}
////	}
//	/**
//	 * 取消激活的KPI告警方法
//	 */
//	private void cancelKpiAlarm() {
//		try {
//			String sql = "SELECT * FROM alarm_monitor WHERE cancel_time IS NULL AND alarm_type = 'KPI'";
//			List<AlarmMonitor> kpiAlarmList = jdbcTemplate.query(sql, new Object[] {},
//					new BeanPropertyRowMapper<AlarmMonitor>(AlarmMonitor.class));
//			String sqlK = "SELECT * FROM quota_monitor ";
//			List<KpiMonitor> currentPeriodKpiList = jdbcTemplate.query(sqlK, new Object[] {},
//					new BeanPropertyRowMapper<KpiMonitor>(KpiMonitor.class));
//			for (AlarmMonitor kpiAlarm : kpiAlarmList) {
//				//try to cancel alarm
//				String unitName = kpiAlarm.getUnitName();
//				String kpiCode = kpiAlarm.getKpiCode();
//				for (KpiMonitor currentKpi : currentPeriodKpiList) {
//					if(currentKpi.getUnitName().equals(unitName)&&currentKpi.getKpiCode().equals(kpiCode)){
//						//match this unit's new period kpi
//						if(kpiAlarm.getKpiComparedMethod().equals("<")){
//							if(currentKpi.getKpiValue()>Double.valueOf(kpiAlarm.getAlarmLimit())){
//								kpiAlarm.setCancelTime(currentKpi.getPeriodStartTime().toString());
////								alarmMonitorRepository.save(kpiAlarm);
//								logger.info("A kpi alarm has been cancelled.");
//								logger.info("Unit:"+kpiAlarm.getUnitName()+",kpi code:"+kpiAlarm.getKpiCode()+",kpi value:"+currentKpi.getKpiValue()+",compared method:"+kpiAlarm.getKpiComparedMethod()+",cancel threshold:"+kpiAlarm.getAlarmLimit());
//							}
//						}
//						if(kpiAlarm.getKpiComparedMethod().equals(">")){
//							if(currentKpi.getKpiValue()<Double.valueOf(kpiAlarm.getAlarmLimit())){
//								kpiAlarm.setCancelTime(currentKpi.getPeriodStartTime().toString());
////								alarmMonitorRepository.save(kpiAlarm);
//								logger.info("A kpi alarm has been cancelled.");
//								logger.info("Unit:"+kpiAlarm.getUnitName()+",kpi code:"+kpiAlarm.getKpiCode()+",kpi value:"+currentKpi.getKpiValue()+",compared method:"+kpiAlarm.getKpiComparedMethod()+",cancel threshold:"+kpiAlarm.getAlarmLimit());
//							}
//						}
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	/**
//	 * 获取网管数据的方法
//	 */
//	private void getOSSData() {
//		logger.info("OSS task Begin");
//		List<KpiConfig> KpiConfigList = new ArrayList<KpiConfig>();
//		String queryKpiConfigList = "select * from kpi_config  ";
//		KpiConfigList = jdbcTemplate.query(queryKpiConfigList, new Object[] {},
//				new BeanPropertyRowMapper<KpiConfig>(KpiConfig.class));
//		jdbcTemplate.update("delete from kpi_monitor");
//		for (KpiConfig KpiConfig : KpiConfigList) {
//			try {
//				String KpiConfigQueryScriptSQL = KpiConfig.getKpiQueryScript().replaceAll("#period_duration_long#", "30/24/60")
//						.replaceAll("#period_duration_short#", "15/24/60");
////				String KpiConfigQueryScriptSQL = KpiConfig.getKpiQueryScript().replaceAll("#period_duration_long#", "2/24")
////						.replaceAll("#period_duration_short#", "1/24");
//				logger.info("real original sql:"+KpiConfigQueryScriptSQL);
//				List<Map<String, Object>> kpiValueList = jdbcTemplateOss.queryForList(KpiConfigQueryScriptSQL);
//				logger.info(KpiConfig.getKpiName() + " : " + kpiValueList.size());
//				saveKpiList(KpiConfig, kpiValueList);
//				logger.info(KpiConfig.getKpiName() + " : end");
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		logger.info("OSS task End");
//	}
//	/**
//	 * 数据入库方法
//	 * @param KpiConfig
//	 * @param kpiValueList
//	 */
//	private void saveKpiList(KpiConfig KpiConfig, List<Map<String, Object>> kpiValueList) {
//		String insertIntoKpiMonitorHistory = CustomSettings.getInsertQuotaHistoryTable();
//		String insertIntoKpiMonitor = CustomSettings.getInsertQuotaCurrentTable();
//		List<Object[]> batchArgs = new ArrayList<Object[]>();
//		for (Map<String, Object> map : kpiValueList) {
//			logger.info(map.toString());
//			String neIdString = map.getOrDefault("NE_ID", -1).toString();
//			String unitIdString = map.getOrDefault("UNIT_ID", -1).toString();
////			EquipmentNe equipmentNe = equipmentNeMap.getOrDefault(neIdString, new EquipmentNe());
////			EquipmentUnit equipmentUnit = equipmentUnitMap.getOrDefault(unitIdString, new EquipmentUnit());
//			Map<String, BigDecimal> kpiSuccessAndRequest = new HashMap<String, BigDecimal>();
//			kpiSuccessAndRequest.put("KPI_SUCCESS", new BigDecimal(map.getOrDefault("KPI_SUCCESS", 0d).toString()));
//			kpiSuccessAndRequest.put("KPI_REQUEST", new BigDecimal(map.getOrDefault("KPI_REQUEST", 0d).toString()));
//			kpiSuccessAndRequest.put("KPI_FAIL", new BigDecimal(map.getOrDefault("KPI_FAIL", 0d).toString()));
//			kpiSuccessAndRequest.put("KPI_VALUE", new BigDecimal(map.getOrDefault("KPI_VALUE", 0d).toString()));
//			Double kpi_value = calculateKpiValue(kpiSuccessAndRequest, KpiConfig.getOutPutField());// kpi_value
//			String kpi_code = KpiConfig.getKpiCode();
//			String kpi_name = KpiConfig.getKpiName();
//			Object[] insertParam = new Object[] { 
//					false, // flag
//					kpi_code, // kpi_code
//					kpi_name, // kpi_name
//					map.getOrDefault("KPI_SUCCESS", 0), // kpi_success_count
//					map.getOrDefault("KPI_FAIL", 0), // kpi_fail_count
//					map.getOrDefault("KPI_REQUEST", 0), // kpi_request_count
//					map.getOrDefault("PERIOD_START_TIME", null), 
//					neIdString, // ne_id
//					equipmentNe.getNeName(), // ne_name
//					unitIdString, // unit_id
//					equipmentUnit.getUnitName(), // unit_name
//					equipmentUnit.getUnitType()!=null?equipmentUnit.getUnitType().toString():"emptyUnitType", // unit_type
//					equipmentNe.getPhysicalLocation(), // node_name
//					equipmentNe.getDhssName(), // dhss_name
//					getKpiUnit(KpiConfig.getOutPutField()), // kpi_unit
//					kpi_value, // kpi_value
//					equipmentUnit.getNeType() != null ? equipmentUnit.getNeType().toString() : "emptyNeType", // ne_type
//					KpiConfig.getKpiCategory(), // scene
//					map.getOrDefault("UNIT_NEXT", 0), // unit_next
//					map.getOrDefault("UNIT_NEXT_ID", 0)// unit_next_id
//			};
//				batchArgs.add(insertParam);
//		}
//		try {
//			//logger.info("try to insert kpi info data");
//			jdbcTemplate.batchUpdate(insertIntoKpiMonitor, batchArgs);
//			jdbcTemplate.batchUpdate(insertIntoKpiMonitorHistory, batchArgs);
//			//logger.info("insert kpi info end");
//		} catch (Exception e) {
//			logger.error("insert params error!");
//			logger.error(batchArgs.toString());
//		}
//		}
//
//	/**
//	 * KPI计算方法
//	 * @param kpiSuccessAndRequest KPI中成功（分子）和请求次数（分母）数据集合
//	 * @param outPutField KPI计算方法
//	 * @return
//	 */
//	private Double calculateKpiValue(Map<String, BigDecimal> kpiSuccessAndRequest, String outPutField) {
//		Double kpiValue = 0d;
//		BigDecimal kpiRequest = kpiSuccessAndRequest.get("KPI_REQUEST");
//		BigDecimal kpiSuccess = kpiSuccessAndRequest.get("KPI_SUCCESS");
//		DecimalFormat df = new DecimalFormat("######0.0000");
//		Double kpiFail = kpiRequest.doubleValue() - kpiSuccess.doubleValue();
//		if (outPutField.equalsIgnoreCase("success_rate") && kpiRequest.intValue() != 0) {
//			kpiValue = (kpiSuccess.doubleValue() / kpiRequest.doubleValue()) * 100.0;
//			kpiValue = Double.parseDouble(df.format(kpiValue));
//			return kpiValue;
//		}
//		if (outPutField.equalsIgnoreCase("success_count")) {
//			kpiValue = kpiSuccess.doubleValue();
//			return kpiValue;
//
//		}
//		if (outPutField.equals("fail_rate") && kpiRequest.intValue() != 0) {
//			kpiValue = (1 - kpiSuccess.doubleValue() / kpiRequest.doubleValue()) * 100.0;
//			kpiValue = Double.parseDouble(df.format(kpiValue));
//			return kpiValue;
//		}
//		if (outPutField.equalsIgnoreCase("fail_count")) {
//			kpiValue = kpiFail;
//			return kpiValue;
//
//		}
//		if (outPutField.equalsIgnoreCase("total_count")) {
//			kpiValue = kpiRequest.doubleValue();
//			return kpiValue;
//
//		}
//		if (outPutField.equalsIgnoreCase("load_rate")) {
//			kpiValue = kpiSuccessAndRequest.get("KPI_VALUE").doubleValue();
//			return kpiValue;
//		}
//		return kpiValue;
//	}
//
//	private String getKpiUnit(String outPutField) {
//		if (outPutField.endsWith("_rate")) {
//			return "%";
//		}
//		if (outPutField.endsWith("_count")) {
//			return "次数";
//		} else
//			return null;
//	}
//	/**
//	 * 获取单元、网元信息
//	 */
//	private void getEquipmentInfo() {
//		logger.info("=============== ne info Start ===============");
////		List<EquipmentNe> neInfoList = jdbcTemplate.query("select * from equipment_ne", new Object[] {},
////				new BeanPropertyRowMapper<EquipmentNe>(EquipmentNe.class));
////		for (EquipmentNe equipmentNe : neInfoList) {
////			equipmentNeMap.put(equipmentNe.getCoGid(), equipmentNe);
////		}
//		logger.info("=============== ne info end ===============");
//
//		logger.info("=============== unit info Start ===============");
//
////		List<EquipmentUnit> unitInfoList = jdbcTemplate.query("select * from equipment_unit", new Object[] {},
////				new BeanPropertyRowMapper<EquipmentUnit>(EquipmentUnit.class));
////
////		for (EquipmentUnit equipmentUnit : unitInfoList) {
////			equipmentUnitMap.put(equipmentUnit.getCoGid(), equipmentUnit);
////		}
//		logger.info("=============== unit info end ===============");
//		/*
//		 * logger.info("===============开始从配置数据库取 unit info ===============");
//		 * List<EquipmentUnit> list_unitInfo = jdbcTemplate.query(
//		 * "select * from equipment_unit", new Object[]{},new
//		 * BeanPropertyRowMapper<EquipmentUnit>(EquipmentUnit.class) ); for
//		 * (EquipmentUnit equipmentUnit : list_unitInfo) {
//		 * equipmentUnitMap.put(equipmentUnit.getNeCode(), equipmentUnit); }
//		 * logger.info("===============结束从配置数据库取 unit info ===============");
//		 * 
//		 * 
//		 * logger.info("===============开始从网管数据库取 dn ===============");
//		 * List<Map<String, Object>> list_dn = jdbcTemplateOss.queryForList(
//		 * "select distinct co_gid,co_oc_id,co_dn from nasda_objects ");
//		 * dnInfoMap = list_dn.get(0); for (Map<String, Object> map : list_dn) {
//		 * logger.info(map.toString()); } logger.info(
//		 * "===============结束从网管数据库取 dn ===============");
//		 */
//	}
//}
