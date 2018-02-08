package com.nokia.ices.app.dhss.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.nokia.ices.app.dhss.config.PropertiesConfig;
import com.nokia.ices.app.dhss.core.utils.Encodes;
import com.nokia.ices.app.dhss.core.utils.JsonMapper;
import com.nokia.ices.app.dhss.domain.command.CommandCheckItem;
import com.nokia.ices.app.dhss.domain.equipment.EquipmentUnit;
import com.nokia.ices.app.dhss.domain.maintain.MaintainOperation;
import com.nokia.ices.app.dhss.domain.maintain.MaintainResult;
import com.nokia.ices.app.dhss.domain.maintain.SecurityManageResult;
import com.nokia.ices.app.dhss.jms.OperationResultConsumer;
import com.nokia.ices.app.dhss.jms.model.JumpContent;
import com.nokia.ices.app.dhss.jms.model.MessageModel;
import com.nokia.ices.app.dhss.jpa.DynamicSpecifications;
import com.nokia.ices.app.dhss.jpa.SearchFilter;
import com.nokia.ices.app.dhss.repository.command.CommandCheckItemRepository;
import com.nokia.ices.app.dhss.repository.equipment.EquipmentUnitRepository;
import com.nokia.ices.app.dhss.repository.maintain.MaintainOperationRepository;
import com.nokia.ices.app.dhss.repository.maintain.MaintainResultRepository;
import com.nokia.ices.app.dhss.repository.maintain.SecurityManageResultRepository;
import com.nokia.ices.app.dhss.service.MaintainService;
import com.nokia.ices.app.dhss.vo.OperationMap;


@Component
public class MaintainServiceImpl implements MaintainService{
	
	SimpleDateFormat formats = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
	
	@Autowired
	private MaintainOperationRepository maintainOperationRepository;
	
	@Autowired
	private MaintainResultRepository maintainResultRepository;
	
	@Autowired
	private EquipmentUnitRepository equipmentUnitRepository;
	
	@Autowired
	private CommandCheckItemRepository commandCheckItemRepository;
	
	@Autowired
	private SecurityManageResultRepository securityManageResultRepository;
	
	@Autowired
    private JmsTemplate jmsTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(MaintainServiceImpl.class);
	

	@Override
	public MaintainOperation saveMaintainResult(OperationMap operationMap) {
		for (Map<String,Object> map :operationMap.getList()) {
			
			EquipmentUnit equipmentUnit = 
					equipmentUnitRepository.findEquipmentUnitByUnitName(map.get("unitName").toString());
			CommandCheckItem commandCheckItem = 
					commandCheckItemRepository.findOne(Long.parseLong(map.get("id").toString()));
			
			MaintainResult checkResult = new MaintainResult();
			String uuId = UUID.randomUUID().toString().replaceAll("-", "");
			checkResult.setUuId(uuId);
			checkResult.setNEInfo(equipmentUnit);
			checkResult.setOperation(operationMap.getOperation());
//			checkResult.setUnit(equipmentUnit);
//			checkResult.setCommandCheckItem(commandCheckItem);
			checkResult.setRequestTime(new Date());
			checkResult.setUnitName(equipmentUnit.getUnitName());
			checkResult.setItemName(commandCheckItem.getName());
			checkResult.setSuccess(false);
			checkResult = maintainResultRepository.save(checkResult);
			
			//替换指令参数
			commandCheckItem.setCommand(replaceCommand(map.get("defaultParam"), commandCheckItem.getCommand()));
			
			//发送指令
			sendCmd(equipmentUnit, commandCheckItem, checkResult);
			
		}
		
		return operationMap.getOperation();
	}
	
	/**
	 * 替换指令参数
	 * @param defaultParam
	 * @param command
	 * @return
	 */
	public String replaceCommand(Object defaultParam,String command){
		String[] paramsArray = (defaultParam == null ? "" : defaultParam.toString()).split(",");
		for (int i = 0; i < paramsArray.length; i++) {
			String _oldStr = "$" + (i + 1);
			String _newStr = paramsArray[i];
			// 按照顺序替换动态值
			if (command.indexOf(_oldStr) != -1) {
				
				command = command.replace(_oldStr, _newStr);
			}
		}
		return command;
	}
	
	
	/**
	 * 发送指令
	 * @param equipmentUnit
	 * @param item
	 * @param result
	 * @return
	 */
	public boolean sendCmd(EquipmentUnit equipmentUnit,CommandCheckItem item,MaintainResult result){
		try {
//			String unitType = equipmentUnit.getUnitType();
			
			String password = Encodes.encodeHex(equipmentUnit.getLoginPassword() != null
					? equipmentUnit.getLoginPassword().getBytes() : "".getBytes());
			
			Map<String,String> content = new HashMap<String,String>();
			content.put("cmd", equipmentUnit.getServerProtocol() + "_DHLR_COMMAND|" + item.getAccount()/*equipmentUnit.getLoginName()*/ + ","
					+ Encodes.encodeHex(equipmentUnit.getRootPassword() != null ? equipmentUnit.getRootPassword().getBytes() : "".getBytes())+ ":" + item.getCommand());
			content.put("ct", "2");
			content.put("rt", "1");
			
			JumpContent jump = new JumpContent(equipmentUnit.getJumperIp(), equipmentUnit.getJumperPort(), equipmentUnit.getJumperUserName(), 
					equipmentUnit.getJumperPassword(), equipmentUnit.getJumpProtocol());
			
			MessageModel model = new MessageModel(/*unitType.equals("SGW") || unitType.equals("SOAP_GW") ? 8  :*/ 8,"dhss", 
					equipmentUnit.getNeType(),OperationResultConsumer.MAINTAIN_QNAME,PropertiesConfig.getDesQName(),result.getUuId(), equipmentUnit.getUnitName(), 
					"DHSS_"+equipmentUnit.getServerProtocol(), password, equipmentUnit.getServerPort(),5, 
					equipmentUnit.getServerProtocol(),equipmentUnit.getLoginName(), equipmentUnit.getServerIp(), 
					content, equipmentUnit.getHostname(), "","", "", "", 
					equipmentUnit.getUnitType(), 71000,jump, equipmentUnit.getIsDirect() ? 1 : 0);
			
			
			jmsTemplate.setDefaultDestinationName(PropertiesConfig.getDesQName());
	    	jmsTemplate.send(new MessageCreator() {
				public Message createMessage(Session session) throws JMSException {
					TextMessage txtMessage = session.createTextMessage("");
	                txtMessage.setStringProperty("msgBody", new JsonMapper().toJson(model));
	                txtMessage.setIntProperty("msgCode", model.getMsgCode());
	                txtMessage.setJMSPriority(5);
	                System.out.println(txtMessage);
	                logger.debug("send message:{}",txtMessage);
	                return txtMessage;
				}
			});
	    	return true;
		} catch (Exception e) {
			return false;
		}
	}
	

	@Override
	public Page<MaintainOperation> findMaintainOperationPage(Map<String, Object> paramMap, Pageable page) {
		Map<String,SearchFilter> filter = SearchFilter.parse(paramMap);
		Specification<MaintainOperation> spec = 
                DynamicSpecifications.bySearchFilter(filter.values(), BooleanOperator.AND, MaintainOperation.class);
		return maintainOperationRepository.findAll(spec,page);
	}

	@Override
	public MaintainOperation findMaintainOperation(Long id) {
		return maintainOperationRepository.findOne(id);
	}

	@Override
	public List<MaintainResult> findMaintainResultListByOperationId(String id) {
		Map<String,Object> paramMap = new HashMap<String,Object>();
		if(StringUtils.isNotEmpty(id)){
			paramMap.put("operation.id_EQ", id);
		}
		Map<String,SearchFilter> filter = SearchFilter.parse(paramMap);
		Specification<MaintainResult> spec = 
                DynamicSpecifications.bySearchFilter(filter.values(), BooleanOperator.AND, MaintainResult.class);
		return maintainResultRepository.findAll(spec,new Sort(Direction.ASC, "unitName"));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<MaintainResult> findMaintainResultListByUUId(String uuids) {
		String [] uuidArray = uuids.split(",");
		List uuidList = new ArrayList<>();
		for (String string : uuidArray) {
			uuidList.add(string);
		}
		return maintainResultRepository.findResultByUuIdIn(uuidList);
	}
	
	
	private String createLogTitle(MaintainResult result) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        StringBuilder string = new StringBuilder();
        string.append("unit：");
        string.append(result.getUnitName());
        string.append("  ");
        string.append("check：");
        string.append(result.getItemName());
        string.append("  ");
        string.append("requestTime：");
        string.append(format.format(result.getRequestTime()));

        return string.toString();
    }
	
	@Override
	public void resultData(File operationLogFile,List<MaintainResult> resultList) throws Exception {
		OutputStream writer = null;
        try {
            writer = new FileOutputStream(operationLogFile);
            
            for (MaintainResult result : resultList) {
            	
                writer.write(createLogTitle(result).getBytes());
                writer.write("\r\n".getBytes());
                writer.write("\r\n".getBytes());

                // 若检查结果成功，则读取检查日志
                // 若检查结果失败，则直接读取错误信息

                if (result.getReportPath() != null && !"".equals(result.getReportPath())) {

                	
                    String resultLog = PropertiesConfig.getBaseLogPath() + result.getReportPath();
                    InputStream reader = null;
                    try {
                        reader = new FileInputStream(resultLog);
                        byte[] b = new byte[1024];
                        int len = 0;
                        while ((len = reader.read(b)) != -1) {
                            writer.write(b, 0, len);
                        }

                        writer.write("\r\n".getBytes());
                        writer.write("----------------------------------------------------------------------"
                                .getBytes());
                        writer.write("\r\n".getBytes());
                    } finally {
                        if (null != reader) {
                            reader.close();
                        }
                    }
                } else {
                    writer.write(result.getErrorLog().getBytes());
                    writer.write("\r\n".getBytes());
                    writer.write(
                            "----------------------------------------------------------------------".getBytes());
                    writer.write("\r\n".getBytes());
                }

                writer.flush();
            }
        } finally {
            if (null != writer) {
                writer.close();
            }
        }
	}
	
	
	

	@Override
	public void downloadFile(HttpServletRequest request,HttpServletResponse response,File operationLogFile,String operationLogName) throws  Exception {
		// TODO Auto-generated method stub
		// 下载日志
        request.setCharacterEncoding("UTF-8");
        InputStream is = null;
        OutputStream os = null;

        try {
            long fileLength = operationLogFile.length();

            response.setContentType("application/octet-stream");

            // 如果客户端为IE
            // System.out.println(request.getHeader("User-Agent"));
            if (request.getHeader("User-Agent").indexOf("Trident") != -1) {
                operationLogName = java.net.URLEncoder.encode(operationLogName, "UTF-8");
            } else {
                operationLogName = new String(operationLogName.getBytes("UTF-8"), "iso-8859-1");
            }

            response.setHeader("Content-disposition", "attachment; filename=" + operationLogName);
            response.setHeader("Content-Length", String.valueOf(fileLength));

            is = new FileInputStream(operationLogFile);
            os = response.getOutputStream();

            byte[] b = new byte[1024];
            int len = 0;
            while ((len = is.read(b)) != -1) {
                os.write(b, 0, len);
            }
            os.flush();
        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }
	}

	@Override
	public Page<SecurityManageResult> findSecurityManageResultPage(Map<String, Object> paramMap, Pageable page) {
		Map<String,SearchFilter> filter = SearchFilter.parse(paramMap);
		Specification<SecurityManageResult> spec = 
                DynamicSpecifications.bySearchFilter(filter.values(), BooleanOperator.AND, SecurityManageResult.class);
		return securityManageResultRepository.findAll(spec,page);
	}

	@Override
	public Iterable<SecurityManageResult> saveSecurityManageResult(List<SecurityManageResult> list) {
		return securityManageResultRepository.save(list);
	}

	
	

}
