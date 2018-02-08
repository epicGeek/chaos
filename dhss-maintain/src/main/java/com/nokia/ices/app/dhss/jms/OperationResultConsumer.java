package com.nokia.ices.app.dhss.jms;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.jms.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.nokia.ices.app.dhss.core.utils.JsonMapper;
import com.nokia.ices.app.dhss.domain.maintain.MaintainOperation;
import com.nokia.ices.app.dhss.domain.maintain.MaintainResult;
import com.nokia.ices.app.dhss.repository.equipment.EquipmentUnitRepository;
import com.nokia.ices.app.dhss.repository.maintain.MaintainOperationRepository;
import com.nokia.ices.app.dhss.repository.maintain.MaintainResultRepository;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//
//import com.nokia.ices.app.websocket.WebSocketStorage;
@Component
public class OperationResultConsumer {
	
	public static final String MAINTAIN_QNAME = "DHSS_DAILY_MAINTAIN";
	
	private final static Logger logger = LoggerFactory.getLogger(OperationResultConsumer.class);

	@Autowired
	MaintainOperationRepository maintainOperationRepository;
	
	@Autowired
	MaintainResultRepository maintainResultRepository;
	
	@Autowired
	EquipmentUnitRepository equipmentUnitRepository;
	
	
	@JmsListener(destination = MAINTAIN_QNAME, containerFactory = "jmsContainerFactory")
	public void reseiveMessageForMaintain(Message message) {
		logger.debug("Received <" + message + ">");
		String msgBody = null;
		Integer msgCode = new Integer(0);
		try {
			msgBody = message.getStringProperty("msgBody");
			msgCode = message.getIntProperty("msgCode");
			logger.debug("消息返回信息：{}", msgBody);
			logger.debug("消息返回Code：{}", msgCode);

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null != msgBody) {
			@SuppressWarnings("unchecked")
			Map<String, String> json = (Map<String, String>) new JsonMapper().fromJson(msgBody, Map.class);
			String session = String.valueOf(json.get("sessionid"));
			logger.debug("消息返回session：{}", session);
			String resultCode = String.valueOf(json.get("flag"));
			logger.debug("消息返回resultCode：{}", resultCode);
			// 根据msgCode来区分执行类型
			if (msgCode == 76000) {// 日志运维
				resultCode.equalsIgnoreCase("0");// 表示正常返回结果，需要保存 Log 的路径，
				
				String msg = resultCode.equals("0") ? String.valueOf(json.get("src")) : String.valueOf(json.get("msg"));// 网元操作日志返回msg
				logger.debug("消息返回msg：{}", msg);
				try {
					persist(session, resultCode, msg/*, eqType*/);
				}catch(Exception e) {
					e.printStackTrace();
				}				
				logger.debug("session=   " + session + "resultCode=   "+ resultCode + "path=   " + msg);
			}  else {
				logger.error("UNKNOWN msgCode:" + msgCode);
			}

		}
	}
	
	public void persist(String session, String resultCode, String log/*,String eqType*/) {
		logger.debug("执行persist方法！");
		try {
			List<MaintainResult> maintainResultList = maintainResultRepository.findResultByUuIdEquals(session);// 根据UUID得到maintain_result
			for (MaintainResult maintainResult : maintainResultList) {
				if (maintainResult!=null) {
					logger.debug("执行persist方法");				
					if ("0".equalsIgnoreCase(resultCode)) {
						maintainResult.setReportPath(log);
						maintainResult.setSuccess(true);
					} else {		
						maintainResult.setErrorLog(log);
						maintainResult.setSuccess(false);
					}
					maintainResult.setResponseTime(new Date());
					maintainResultRepository.save(maintainResult);
					
					MaintainOperation maintainOperation = maintainResult.getOperation();							
					List<MaintainResult> return_number_list = maintainResultRepository.findResultByOperationIdAndResponseTimeIsNotNull(maintainOperation.getId());
					List<MaintainResult> return_number_list2 =  maintainResultRepository.findResultByOperationId(maintainOperation.getId());
					Integer result_number = return_number_list2.size();
					Integer return_number = return_number_list.size();
					logger.debug("执行persist方法result_number:" + result_number+ ",return_number:" + return_number);
					if (result_number == return_number) {
						maintainOperation.setIsDone(true);
						maintainOperationRepository.save(maintainOperation);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
