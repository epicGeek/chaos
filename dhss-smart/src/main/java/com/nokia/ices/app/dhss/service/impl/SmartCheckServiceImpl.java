package com.nokia.ices.app.dhss.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.persistence.criteria.Predicate.BooleanOperator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.nokia.ices.app.dhss.config.ProjectProperties;
import com.nokia.ices.app.dhss.config.SmartProjectProperties;
import com.nokia.ices.app.dhss.core.utils.Encodes;
import com.nokia.ices.app.dhss.core.utils.JsonMapper;
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
import com.nokia.ices.app.dhss.repository.command.CommandCheckItemRepository;
import com.nokia.ices.app.dhss.repository.equipment.EquipmentUnitRepository;
import com.nokia.ices.app.dhss.repository.smart.SmartCheckJobRepository;
import com.nokia.ices.app.dhss.repository.smart.SmartCheckResultRepository;
import com.nokia.ices.app.dhss.repository.smart.SmartCheckResultTmpRepository;
import com.nokia.ices.app.dhss.repository.smart.SmartCheckScheduleResultRepository;
import com.nokia.ices.app.dhss.service.SmartCheckService;

@Component
public class SmartCheckServiceImpl implements SmartCheckService{
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	private SmartCheckJobRepository smartCheckJobRepository;
	
	@Autowired
	private SmartCheckResultRepository smartCheckResultRepository;
	
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
	
	private final static Logger logger = LoggerFactory.getLogger(SmartCheckServiceImpl.class);
	
	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	
	@Override
	public void sendMessage(SmartCheckJob smart,int status){
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("sessionId", UUID.randomUUID().toString().replace("-", ""));
		map.put("messageCode", SmartProjectProperties.getMessageCode());
		map.put("srcQueue", SmartProjectProperties.smarTaskName);
		map.put("appQueue", SmartProjectProperties.getAppQueue());
		map.put("taskType", 4);
		map.put("taskName", SmartProjectProperties.getTaskName());
		map.put("serviceName", smart.getJobName());
		map.put("taskParam", "");
		map.put("startTime", sdf.format(smart.getNextDay()));
		map.put("endTime", "");
		
		int type = smart.getJobType();
		Map<String, Object> contentMap = new HashMap<String, Object>();
		contentMap.put("rule", type == 1 ? "day" : (type == 2 ? "week" : "month"));
		contentMap.put("ruleMintue", "");
		contentMap.put("ruleHour", "");
		contentMap.put("ruleDay", "");
		contentMap.put("ruleWeek", "");
		contentMap.put("ruleMonth", "");
		map.put("ruleContent", contentMap);
		map.put("ruleDesc", smart.getJobDesc());
		map.put("startStatus", smart.getExecFlag() == 1 ? 2 : 3);
		map.put("startMethod", 2);
		map.put("operateStatus", status);
		map.put("jarName", SmartProjectProperties.getJarName());
		map.put("isKill", 3);
		map.put("isLog", 2);
		map.put("flag1", "");
		map.put("flag2", "");
		map.put("flag3", "");
		map.put("app", "dhss");
		jmsTemplate.setDefaultDestinationName(SmartProjectProperties.getSmartJobQueue());
		jmsTemplate.send(new MessageCreator()  {
			public Message createMessage(Session session) throws JMSException {
				TextMessage txtMessage = session.createTextMessage("");
				txtMessage.setText(new JsonMapper().toJson(map));
//				txtMessage.setStringProperty("msgBody", new JsonMapper().toJson(map));
//				txtMessage.setIntProperty("msgCode",Integer.parseInt(map.get("messageCode").toString()));
//				txtMessage.setJMSPriority(5);
				logger.info("send message:{}",txtMessage);
				return txtMessage;
			}
		});
		
	}

	@Override
	public Page<SmartCheckJob> findSmartCheckJob(Map<String,Object> paramMap, Pageable pageable) {
		Map<String,SearchFilter> filter = SearchFilter.parse(paramMap);
		Specification<SmartCheckJob> spec = 
                DynamicSpecifications.bySearchFilter(filter.values(), BooleanOperator.OR, SmartCheckJob.class);
		return smartCheckJobRepository.findAll(spec, pageable);
	}
	
	@Override
	public List<SmartCheckResult> getfindResult(Map<String,Object> paramMap){
		Map<String,SearchFilter> filter = SearchFilter.parse(paramMap);
		Specification<SmartCheckResult> spec = 
                DynamicSpecifications.bySearchFilter(filter.values(), BooleanOperator.AND, SmartCheckResult.class);
		return smartCheckResultRepository.findAll(spec);
	}
	
	@Override
	public Page<SmartCheckResult> getfindResultPage(Map<String,Object> paramMap,Pageable page){
		Map<String,SearchFilter> filter = SearchFilter.parse(paramMap);
		Specification<SmartCheckResult> spec = 
                DynamicSpecifications.bySearchFilter(filter.values(), BooleanOperator.AND, SmartCheckResult.class);
		return smartCheckResultRepository.findAll(spec,page);
	}

	@Override
	public Iterable<SmartCheckJob> findSmartCheckJobAll() {
		return smartCheckJobRepository.findAll();
	}

	@Override
	public boolean saveSmartCheckJob(Iterable<SmartCheckJob> list) {
		smartCheckJobRepository.save(list);
		return true;
	}
	
	@Override
	public List<Map<String, Object>> getSmartCheckDetailResultPageList(String id) {
		StringBuffer sb = new StringBuffer("SELECT result.dhss_name,result.ID,result.ne_type_name,result.NE_ID,result.NE_NAME,"
				+ "result.UNIT_ID,result.UNIT_NAME,result.UNIT_TYPE_NAME,result.CHECK_ITEM_ID,result.CHECK_ITEM_NAME,"
				+ "result.RESULT_CODE,ifNull(result.ERROR_MESSAGE,'') ERROR_MESSAGE,ifNull(result.FILE_PATH,'') FILE_PATH,result.SCHEDULE_ID,"
				+ "DATE_FORMAT(result.START_TIME,'%Y%m%d%H%m%s') START_TIME,result.LOG_STATE FROM smart_check_result result "
				+ "where result.schedule_id in ("+id+") ;");
		return jdbcTemplate.queryForList(sb.toString());
	}

	@Override
	public List<Map<String, Object>> findSmartCheckResultList(String scheduleId,String type) {
		String jobSql = "select check_item_name as checkItemName,check_item_id as checkItemId,count(distinct unit_id) as unitCount,sum(if(result_code = '0',1,0)) "
				+ "as errorCount,if(!sum(if(result_code = '0',1,0)),'','red') as highlight from smart_check_result WHERE schedule_id = ? group by check_item_name,check_item_id;";
		 
		String neSql = "SELECT ne_name as neName,ne_id as neId,ne_type_name as neType,count(distinct check_item_name) as unitCount,sum(if(result_code = '0',1,0)) AS errorCount,if(!sum(if(result_code = '0',1,0)),'','red') as highlight  FROM "
				+ "smart_check_result WHERE schedule_id = ? GROUP BY ne_name,ne_id";
		
//		String neSql = "SELECT unit_name as neName,unit_id as neId,unit_type_name as neType,count(check_item_name) as unitCount,sum(if(result_code = '0',1,0)) AS errorCount,if(!sum(if(result_code = '0',1,0)),'','red') as highlight  FROM "
//				+ "smart_check_result WHERE schedule_id = ? GROUP BY ne_name,ne_id";
		
		String execSql = "ne".equals(type) ? neSql : jobSql;
		
		return jdbcTemplate.queryForList(execSql,scheduleId);
	}

	@Override
	public Set<EquipmentUnit> findSmartJobUnit(SmartCheckJob job) {
		return equipmentUnitRepository.findListBySmartCheckJob(job);
	}

	@Override
	public Set<CommandCheckItem> findSmartJobCommandCheckItem(SmartCheckJob job) {
		return commandCheckItemRepository.findListBySmartCheckJob(job);
	}

	@Override
	public boolean execJob(Long id) {
		try {
			SmartCheckJob job = smartCheckJobRepository.findOne(id);
			Set<EquipmentUnit> equipmentUnitSet = equipmentUnitRepository.findListBySmartCheckJob(job);
			Set<CommandCheckItem> checkItemSet = commandCheckItemRepository.findListBySmartCheckJob(job);
			
			Integer size = equipmentUnitSet.size();
			SmartCheckScheduleResult result = new SmartCheckScheduleResult();
			result.setStartTime(new Date());
			result.setAmountJob(size);
			result.setAmountUnit(size);
			result.setErrorUnit(0);
			result.setExecFlag(Byte.parseByte(String.valueOf("2")));
			result.setJobDesc(job.getJobDesc());
			result.setJobId(job.getId());
			result.setJobName(job.getJobName());
			result = smartCheckScheduleResultRepository.save(result);
			
			
			
			for (EquipmentUnit unit : equipmentUnitSet) {
						for (CommandCheckItem checkItem : checkItemSet) {
							String applyUnit = "/" + checkItem.getApplyUnit() + "/";
							if(applyUnit.indexOf("/" + unit.getUnitType() + "/") == -1){
								continue;
							}
							SmartCheckResultTmp resultTmp = new SmartCheckResultTmp();
							resultTmp.setUuId(UUID.randomUUID().toString().replace("-", "")+"@@"+SmartProjectProperties.getTaskName()+"|"+result.getJobName());
							resultTmp.setCheckItemId(checkItem.getId());
							resultTmp.setCheckItemName(checkItem.getName());
							resultTmp.setScheduleId(result.getId());
							String command = checkItem.getCommand();
							if(StringUtils.isNotEmpty(checkItem.getDefaultParamValues())){
								String [] values = checkItem.getDefaultParamValues().split(",");
								for (int i = 0; i < values.length; i++) {
									command.replace("$"+i, values[i]);
								}
							}
							resultTmp.setHostname(unit.getHostname());
							resultTmp.setScript(checkItem.getScript()); 
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
							resultTmp.setStartTime(/*result.getStartTime()*/new Date());
							resultTmp.setResultCode(false);
							resultTmp.setDhssName(unit.getDhssName());
							resultTmp = smartCheckResultTmpRepository.save(resultTmp);
							
							
							StringBuilder paramsBuilder = new StringBuilder();
							String pwd = Encodes.encodeHex(resultTmp.getRootPwd() != null ? resultTmp.getRootPwd().getBytes() : "".getBytes());
							String password = Encodes.encodeHex(resultTmp.getLoginPwd() != null ? resultTmp.getLoginPwd().getBytes() : "".getBytes());
							String [] unames = resultTmp.getUserName().split("&&");
							paramsBuilder.append(unames[0] + "," + pwd + ":" + resultTmp.getCommand());
							
							
							Map<String,String> content = new HashMap<String,String>();
					        content.put("cmd",resultTmp.getProtocol() + "_DHLR_COMMAND|"+unames[1]+","+
					        		Encodes.encodeHex(resultTmp.getRootPwd() != null ? resultTmp.getRootPwd().getBytes() : "".getBytes())+":"+resultTmp.getCommand());
					        content.put("ct", "2");
					        content.put("rt", "1");
							String unitType = unit.getUnitType();
							Integer maxNum = unitType.equals("SGW") || unitType.equals("SOAP_GW") ? 8 :
								(StringUtils.isNotBlank(SmartProjectProperties.getMaxNum()) ? Integer.parseInt(SmartProjectProperties.getMaxNum()) : 4 );
							
							JumpContent jump = new JumpContent(unit.getJumperIp(), unit.getJumperPort(), unit.getJumperUserName(), 
									unit.getJumperPassword(), unit.getJumpProtocol());
							
							MessageModel model = new MessageModel(maxNum,"dhss", resultTmp.getNeType(), 
									"smart-task-cmd-consumer",ProjectProperties.getDesQName(),resultTmp.getUuId(), resultTmp.getUnitName(), 
									"DHSS_"+resultTmp.getProtocol(), password, Integer.valueOf(resultTmp.getPort()),5, resultTmp.getProtocol(), 
									unit.getLoginName(), resultTmp.getIp(), content, resultTmp.getHostname(), resultTmp.getNetFlag(),"", "", "", 
									resultTmp.getUnitType(), 71000,jump,unit.getIsDirect() ? 1 : 0);
							
							
							
					        
					        
					        jmsTemplate.setDefaultDestinationName(ProjectProperties.getDesQName());
							jmsTemplate.send(new MessageCreator() {
								public Message createMessage(Session session) throws JMSException {
									TextMessage txtMessage = session.createTextMessage("");
									txtMessage.setStringProperty("msgBody", new JsonMapper().toJson(model));
									txtMessage.setIntProperty("msgCode",model.getMsgCode());
									txtMessage.setJMSPriority(5);
	
									logger.debug("message.getSrcQ() = {},message.getDestQ() = {}", model.getMsgCode(),
											model.getDestQ());
									logger.debug(txtMessage.toString());
									return txtMessage;
								}
							});
							
						}
						
			}
			
			
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public HttpServletResponse downloadZip(File file, HttpServletResponse response, HttpServletRequest request) {
		try {
			// 以流的形式下载文件。
			InputStream fis = new BufferedInputStream(new FileInputStream(file.getPath()));
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
			// 清空response
			response.reset();

			OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/octet-stream");
			String operationLogName = new String(file.getName().getBytes("UTF-8"), "iso-8859-1");
			if (request.getHeader("User-Agent").indexOf("Trident") != -1) {
				operationLogName = java.net.URLEncoder.encode(operationLogName, "UTF-8");
			}
			// 如果输出的是中文名的文件，在此处就要用URLEncoder.encode方法进行处理
			response.setHeader("Content-Disposition", "attachment;filename=" + operationLogName);
			toClient.write(buffer);
			toClient.flush();
			toClient.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				File f = new File(file.getPath());
				f.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}
	
	@Override
	public void zipFile(List<File> files,ZipOutputStream outputStream) {
		int size = files.size();
		for(int i = 0; i < size; i++) {
		    File file = (File) files.get(i);
		    zipFile(file, outputStream);
		}
	}

	private void zipFile(File inputFile, ZipOutputStream ouputStream) {
		try {
            if(inputFile.exists()) {
                /**如果是目录的话这里是不采取操作的，
                 * 至于目录的打包正在研究中*/
                if (inputFile.isFile()) {
                    FileInputStream IN = new FileInputStream(inputFile);
                    BufferedInputStream bins = new BufferedInputStream(IN, 512);
                    //org.apache.tools.zip.ZipEntry
                    ZipEntry entry = new ZipEntry(inputFile.getName());
                    ouputStream.putNextEntry(entry);
                    // 向压缩文件中输出数据   
                    int nNumber;
                    byte[] buffer = new byte[512];
                    while ((nNumber = bins.read(buffer)) != -1) {
                        ouputStream.write(buffer, 0, nNumber);
                    }
                    // 关闭创建的流对象   
                    bins.close();
                    IN.close();
                } else {
                    try {
                        File[] files = inputFile.listFiles();
                        for (int i = 0; i < files.length; i++) {
                            zipFile(files[i], ouputStream);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	@Override
	public boolean checkJob(Long id) {
		SmartCheckJob job = smartCheckJobRepository.findOne(id);
		Set<EquipmentUnit> equipmentUnitSet = equipmentUnitRepository.findListBySmartCheckJob(job);
		Set<CommandCheckItem> checkItemSet = commandCheckItemRepository.findListBySmartCheckJob(job);
		
		for (EquipmentUnit unit : equipmentUnitSet) {
			for (CommandCheckItem checkItem : checkItemSet) {
				String applyUnit = "/" + checkItem.getApplyUnit() + "/";
				if(applyUnit.indexOf("/" + unit.getUnitType() + "/") != -1){
					return true;
				}
			}
		}
		return false;
	}

	

}
