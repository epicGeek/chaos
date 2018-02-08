package com.nokia.ices.app.dhss.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.persistence.criteria.Predicate.BooleanOperator;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.nokia.ices.app.dhss.StartApplication;
import com.nokia.ices.app.dhss.config.ProjectConfig;
import com.nokia.ices.app.dhss.consumer.CmdConsumer;
import com.nokia.ices.app.dhss.core.utils.Encodes;
import com.nokia.ices.app.dhss.core.utils.JsonMapper;
import com.nokia.ices.app.dhss.domain.alarm.AlarmMonitor;
import com.nokia.ices.app.dhss.domain.command.CommandCheckItem;
import com.nokia.ices.app.dhss.domain.equipment.EquipmentUnit;
import com.nokia.ices.app.dhss.domain.smart.SmartCheckJob;
import com.nokia.ices.app.dhss.domain.smart.SmartCheckResult;
import com.nokia.ices.app.dhss.domain.smart.SmartCheckResultTmp;
import com.nokia.ices.app.dhss.domain.smart.SmartCheckScheduleResult;
import com.nokia.ices.app.dhss.jms.model.JumpContent;
import com.nokia.ices.app.dhss.jms.model.MessageModel;
import com.nokia.ices.app.dhss.jpa.DynamicSpecifications;
import com.nokia.ices.app.dhss.jpa.SearchFilter;
import com.nokia.ices.app.dhss.repository.alarm.AlarmMonitorRepository;
import com.nokia.ices.app.dhss.repository.command.CommandCheckItemRepository;
import com.nokia.ices.app.dhss.repository.equipment.EquipmentUnitRepository;
import com.nokia.ices.app.dhss.repository.smart.SmartCheckJobRepository;
import com.nokia.ices.app.dhss.repository.smart.SmartCheckResultRepository;
import com.nokia.ices.app.dhss.repository.smart.SmartCheckResultTmpRepository;
import com.nokia.ices.app.dhss.repository.smart.SmartCheckScheduleResultRepository;
import com.nokia.ices.app.dhss.service.TaskService;
   
@Component
public class TaskServiceImpl implements TaskService {

	@Autowired
	private SmartCheckJobRepository smartCheckJobRepository;
	
	@Autowired
	private EquipmentUnitRepository equipmentUnitRepository;
	
	@Autowired
	private CommandCheckItemRepository commandCheckItemRepository;
	
	@Autowired
	private SmartCheckScheduleResultRepository smartCheckScheduleResultRepository;
	
	@Autowired
	private SmartCheckResultTmpRepository smartCheckResultTmpRepository;
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Autowired
	private AlarmMonitorRepository alarmMonitoRepository;
	
	@Autowired
	private SmartCheckResultRepository smartCheckResultRepository;
	
	private final static Logger logger = LogManager.getLogger(TaskServiceImpl.class);
	
	@Override
	public SmartCheckJob findSmartCheckJob(String name) {
		List<SmartCheckJob> list = smartCheckJobRepository.findSmartCheckJobByJobName(name);
		return list.size()>0 ? list.get(0) : null;
	}

	@Override
	public Set<EquipmentUnit> findListBySmartCheckJob(SmartCheckJob smartCheckJob) {
		Set<EquipmentUnit> set = equipmentUnitRepository.findListBySmartCheckJob(smartCheckJob);
		
		return set;
	}

	@Override
	public Set<CommandCheckItem> findSetBySmartCheckJob(SmartCheckJob smartCheckJob) {
		Set<CommandCheckItem> set = commandCheckItemRepository.findListBySmartCheckJob(smartCheckJob);
		
		return set;
	}

	@Override
	public SmartCheckScheduleResult saveSmartCheckSchedule(SmartCheckJob job,int size) {
		SmartCheckScheduleResult result = new SmartCheckScheduleResult();
		result.setStartTime(job.getExecDay());
		result.setAmountJob(size);
		result.setAmountUnit(size);
		result.setErrorUnit(0);
		result.setExecFlag(Byte.parseByte(String.valueOf("2")));
		result.setJobDesc(job.getJobDesc());
		result.setJobId(job.getId());
		result.setJobName(job.getJobName());
		
		return smartCheckScheduleResultRepository.save(result);
	}

	@Override
	public void execJob(SmartCheckScheduleResult smartCheckScheduleResult,Set<EquipmentUnit> equipmentUnitSet,Set<CommandCheckItem> checkItemSet,String moduleStr) {
		for (EquipmentUnit unit : equipmentUnitSet) {
					for (CommandCheckItem checkItem : checkItemSet) {
						String applyUnit = "/" + checkItem.getApplyUnit() + "/";
						if(applyUnit.indexOf("/" + unit.getUnitType() + "/") == -1){
							continue;
						}
						SmartCheckResultTmp resultTmp = new SmartCheckResultTmp();
						resultTmp.setUuId(UUID.randomUUID().toString().replace("-", "")+"@@"+moduleStr);
						resultTmp.setCheckItemId(checkItem.getId());
						resultTmp.setCheckItemName(checkItem.getName());
						resultTmp.setScheduleId(smartCheckScheduleResult.getId());
						String command = checkItem.getCommand();
						if(StringUtils.isNotEmpty(checkItem.getDefaultParamValues())){
							String [] values = checkItem.getDefaultParamValues().split(",");
							for (int i = 0; i < values.length; i++) {
								command.replace("$"+(i+1), values[i]);
							}
						}
						resultTmp.setHostname(unit.getHostname());
						resultTmp.setScript(checkItem.getScript());
						resultTmp.setNetFlag(/*unit.getNetFlag()*/"");
						resultTmp.setCommand(command);
						resultTmp.setLoginPwd(unit.getLoginPassword());
						resultTmp.setRootPwd(unit.getRootPassword());
						resultTmp.setPort(unit.getServerPort()+"");
						resultTmp.setProtocol(unit.getServerProtocol());
						resultTmp.setIp(unit.getServerIp());
						resultTmp.setUserName(unit.getLoginName() + "&&" + checkItem.getAccount());
						resultTmp.setUnitId(unit.getId());
						resultTmp.setUnitName(unit.getUnitName());
						resultTmp.setUnitType(unit.getUnitType());
						resultTmp.setNeId(unit.getNeId());
						resultTmp.setNeName(unit.getNeName());
						resultTmp.setNeType(unit.getNeType());
						resultTmp.setExecFlag("1");
						resultTmp.setStartTime(smartCheckScheduleResult.getStartTime());
						resultTmp.setResultCode(false);
						resultTmp.setDhssName(unit.getDhssName());
						resultTmp.setScriptType(checkItem.getScriptType());
						resultTmp = smartCheckResultTmpRepository.save(resultTmp);
						
						
						StringBuilder paramsBuilder = new StringBuilder();
						String pwd = Encodes.encodeHex(resultTmp.getRootPwd() != null ? resultTmp.getRootPwd().getBytes() : "".getBytes());
						paramsBuilder.append(unit.getLoginName() + "," + pwd + ":" + resultTmp.getCommand());
						String password = Encodes.encodeHex(resultTmp.getLoginPwd() != null ? resultTmp.getLoginPwd().getBytes() : "".getBytes());
						Map<String,String> content = new HashMap<String,String>();
						content.put("cmd", resultTmp.getProtocol() + "_DHLR_COMMAND|"+checkItem.getAccount()+","+
				        		Encodes.encodeHex(resultTmp.getRootPwd() != null ? resultTmp.getRootPwd().getBytes() : "".getBytes())+":"+resultTmp.getCommand());
						content.put("ct", "2");
						content.put("rt", "1");
						String unitType = unit.getUnitType();
						Integer maxNum = unitType.equals("SGW") || unitType.equals("SOAP_GW") ? 8  : 
											(StringUtils.isNotBlank(ProjectConfig.getMaxNum()) ? Integer.parseInt(ProjectConfig.getMaxNum()) : 4 );
						
						JumpContent jump = new JumpContent(unit.getJumperIp(), unit.getJumperPort(), unit.getJumperUserName(), 
								unit.getJumperPassword(), unit.getJumpProtocol());
						
						
						MessageModel model = new MessageModel(maxNum,"dhss", resultTmp.getNeType(), 
								CmdConsumer.CMD_TASK_NAME,ProjectConfig.getDesQName(),resultTmp.getUuId(), resultTmp.getUnitName(), 
								"DHSS_"+resultTmp.getProtocol(), password, Integer.valueOf(resultTmp.getPort()),5, resultTmp.getProtocol(), 
								unit.getLoginName(), resultTmp.getIp(), content, resultTmp.getHostname(), resultTmp.getNetFlag(),"", "", "", 
								resultTmp.getUnitType(), 71000,jump,unit.getIsDirect() ? 1 : 0);
						
				        
				        
				        jmsTemplate.setDefaultDestinationName(ProjectConfig.getDesQName());
						jmsTemplate.send(new MessageCreator() {
							public Message createMessage(Session session) throws JMSException {
								TextMessage txtMessage = session.createTextMessage("");
								txtMessage.setStringProperty("msgBody", new JsonMapper().toJson(model));
								txtMessage.setIntProperty("msgCode",model.getMsgCode());
								txtMessage.setJMSPriority(5);
								logger.info(moduleStr+"Send Message:{}",new JsonMapper().toJson(model));
								return txtMessage;
							}
						});
						
					}
					
		}
	}
	
	@Override
	public  SmartCheckJob getNextExecuteTime(SmartCheckJob job) throws ParseException {
		Long nextExecuteTime = 0l;
		Long currData = job.getNextDay().getTime();
		switch (job.getJobType()) {
		case 4:
			// 15分钟
			nextExecuteTime = currData + 1000 * 60 * 15;
			break;
		case 5:
			// HOUR
			nextExecuteTime = currData + 1000 * 60 * 60;
			break;
		case 1:
			// DAY
			nextExecuteTime = currData + 1000 * 60 * 60 * 24;
			break;
		case 2:
			// WEEK
			nextExecuteTime = currData + 1000 * 60 * 60 * 24 * 7;
			break;
		case 3:
			// MONTH
			int maxDate = 0;
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(new Date());
			maxDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			nextExecuteTime = currData + 1000 * 60 * 60 * 24 * maxDate;
			break;
		default:
			break;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		job.setExecDay(job.getNextDay());
		job.setExecTime(String.valueOf(job.getNextDay()));
		job.setNextDay(format.parse(format.format(new Date(nextExecuteTime))));
		logger.info(StartApplication.moduleStr+"updateNextDate:"+job.getNextDay());
		return smartCheckJobRepository.save(job);
	}
	
	@Override
	public  SmartCheckJob saveJobNextDate(SmartCheckJob job) {
		return smartCheckJobRepository.save(job);
	}

	@Override
	public SmartCheckResultTmp getSmartCheckResultTmpByUUID(Map<String,Object> session) {
		Map<String,SearchFilter> filter = SearchFilter.parse(session);
		Specification<SmartCheckResultTmp> spec = 
                DynamicSpecifications.bySearchFilter(filter.values(), BooleanOperator.AND, SmartCheckResultTmp.class);
		List<SmartCheckResultTmp> tmp = smartCheckResultTmpRepository.findAll(spec);
		return tmp.size() == 0 ? new SmartCheckResultTmp() : tmp.get(0);
	}

	@Override
	public SmartCheckResultTmp saveSmartCheckResultTmp(SmartCheckResultTmp smartCheckResultTmp) {
		return smartCheckResultTmpRepository.save(smartCheckResultTmp);
	}

	@Override
	public SmartCheckScheduleResult getSmartCheckScheduleResultById(Long id) {
		return smartCheckScheduleResultRepository.findOne(id);
	}

	@Override
	public SmartCheckScheduleResult saveSmartCheckScheduleResult(SmartCheckScheduleResult smartCheckScheduleResult) {
		return smartCheckScheduleResultRepository.save(smartCheckScheduleResult);
	}

	@Override
	public AlarmMonitor saveAlarmMonitor(AlarmMonitor monitor) {
		return alarmMonitoRepository.save(monitor);
	}

	@Override
	public SmartCheckResult saveSmartCheckResult(SmartCheckResult smartCheckResult) {
		return smartCheckResultRepository.save(smartCheckResult);
	}

	@Override
	public Integer findSmartCheckResultErrorSize(Long id) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("scheduleId_EQ", String.valueOf(id));
		paramMap.put("resultCode_EQ", false);
		Map<String,SearchFilter> filter = SearchFilter.parse(paramMap);
		Specification<SmartCheckResult> spec = 
                DynamicSpecifications.bySearchFilter(filter.values(), BooleanOperator.AND, SmartCheckResult.class);
		List<SmartCheckResult> tmp = smartCheckResultRepository.findAll(spec);
		Set<String> tempSet = new HashSet<>();
		for (SmartCheckResult smartCheckResult : tmp) {
			tempSet.add(smartCheckResult.getUnitName());
		}
		return tempSet.size();
	}

	@Override
	public boolean deleteSmartCheckTempData() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String,Object> map = new HashMap<>();
		try {
			map.put("startTime_LT",format.parse(format.format(new Date().getTime() - 1000 * 60 * 60 * 24)) );
			Map<String,SearchFilter> filter = SearchFilter.parse(map);
			Specification<SmartCheckResultTmp> spec = 
	                DynamicSpecifications.bySearchFilter(filter.values(), BooleanOperator.AND, SmartCheckResultTmp.class);
			List<SmartCheckResultTmp> tmp = smartCheckResultTmpRepository.findAll(spec);
			smartCheckResultTmpRepository.delete(tmp);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		try {
			map.put("startTime_LT", format.parse(getDate()));
			
			Map<String,SearchFilter> filter = SearchFilter.parse(map);
			Specification<SmartCheckScheduleResult> spec = 
	                DynamicSpecifications.bySearchFilter(filter.values(), BooleanOperator.AND, SmartCheckScheduleResult.class);
			List<SmartCheckScheduleResult> tmp = smartCheckScheduleResultRepository.findAll(spec);
			smartCheckScheduleResultRepository.delete(tmp);
			
			Specification<SmartCheckResult> specResult = 
	                DynamicSpecifications.bySearchFilter(filter.values(), BooleanOperator.AND, SmartCheckResult.class);
			List<SmartCheckResult> result = smartCheckResultRepository.findAll(specResult);
			smartCheckResultRepository.delete(result);
			
			
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		
		return true;
	}
	
	
	public static String getDate() {
		Date dNow = new Date();   //当前时间
		Date dBefore = new Date();
		Calendar calendar = Calendar.getInstance(); //得到日历
		calendar.setTime(dNow);//把当前时间赋给日历
		calendar.add(Calendar.MONTH, -3);  //设置为前3月
		dBefore = calendar.getTime();   //得到前3月的时间
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //设置时间格式
		String defaultStartDate = sdf.format(dBefore);    //格式化前3月的时间
		return defaultStartDate;
	}
	
	

}
