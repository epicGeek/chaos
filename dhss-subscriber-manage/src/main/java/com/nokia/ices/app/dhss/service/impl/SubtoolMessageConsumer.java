package com.nokia.ices.app.dhss.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.nokia.ices.app.dhss.config.ProjectProperties;
import com.nokia.ices.app.dhss.core.utils.JsonMapper;
import com.nokia.ices.app.dhss.domain.subscriber.CheckSubtoolResult;
import com.nokia.ices.app.dhss.jms.model.MessageModel;
import com.nokia.ices.app.dhss.repository.subscriber.CheckSubtoolResultRepository;

@Component
public class SubtoolMessageConsumer {

	public static Map<String,MessageModel> cacheCheckName = new HashMap<>();
	
	public static final String RESULT_CODE_SUCCESS = "0";

	@Autowired
	private CheckSubtoolResultRepository subtoolRepository;
	
    private static final Logger logger = LoggerFactory.getLogger(SubtoolMessageConsumer.class);

    @JmsListener(destination = SubscriberServiceImpl.SRCQ_NAME_UNIT, containerFactory = "jmsContainerFactory")
    public void receiveMessage(Message message) {
    	
        Map<String, String> receivedMap = convertMessageToMap(message);
        String sessionid = String.valueOf(receivedMap.get("sessionid"));
        String src = String.valueOf(receivedMap.get("src"));
        String resultCode = String.valueOf(receivedMap.get("flag"));
        logger.debug("消息返回 resultCode:{},sessionid:{}",resultCode,sessionid);
        CheckSubtoolResult  sub = new CheckSubtoolResult();
        String flag = "0";//默认返回成功
        try{
        	MessageModel messageModel = cacheCheckName.get(sessionid);
        	String messages = null;
        	if(resultCode.equalsIgnoreCase(RESULT_CODE_SUCCESS)) {//0表示成功
        		if(src.indexOf("/")!=-1){
        			src = src.substring(src.indexOf("/")+1, src.length()); 
        		}
        		String filePath =  ProjectProperties.getCOMP_BASE_PATH()+src;
        		logger.debug("报文路径：>>>>>>>>>"+filePath);
        		String neName = messageModel.getNe();
        		Map<String, String>  resultMap = FileReader(filePath,neName);
        		if(StringUtils.isNotEmpty(resultMap.get("result_succ"))){
        			//说明命令执行成功,并检查返回内容是否有错误信息存在
        			messages = resultMap.get("result_succ");
        			flag = (messages.contains("COMMAND EXECUTION FAILED") || 
        					messages.contains("error") || 
        					messages.contains("ERROR")) ? "1" : "0";
        		}else{
        			//命令执行失败
        			messages = resultMap.get("result_error");
        			flag = "1";
        		}
        	}else{
        		messages = String.valueOf(receivedMap.get("msg"));
        		flag = "1";
        		
        	}
        	logger.debug("msg---------"+messages);
        	logger.debug("number_checkName =--------" + messageModel);
        	sub.setExeResults(flag);
    		sub.setErrorMessage(messages);
        	sub.setFilePath(src);
        	sub.setCheckName(messageModel.getNetFlag());
        	sub.setCreateTime(new Date());
        	sub.setCreateName(messageModel.getHostname());
        	sub.setUserNumber(messageModel.getApp());
        	subtoolRepository.save(sub);//持久化数据
        	//删除缓存
        	cacheCheckName.remove(sessionid);
        	
        	logger.debug("subtool add data success...........");
        }catch(Exception e){
        	logger.debug("error:"+e.toString());
        }
        
        
    }
    

    /**
     * 根据返回报文路径判断是否成功
     * @param filePath
     * @return
     */
    private Map<String, String> FileReader(String filePath,String neName){
		Map<String, String> contextMessage = new HashMap<>();
  		String result_error = "";
  		String result_succ = "";
//  		String [] result = new String[2];
  		BufferedReader buf = null;
  		
  		try{
  			File file = new File(filePath);
  			if(file.exists()){
  				buf = new BufferedReader(new FileReader(file));
  				String linstr = "";
  				while ((linstr = buf.readLine()) != null) {
					if(linstr.contains(neName)){
						result_succ+=linstr+"\r\n";
					}else{
				        if(StringUtils.isNotEmpty(result_succ)){
				        	result_succ+=linstr+"\r\n";
				        }else{
				        	result_error +=linstr+"\r\n";
				        }
					}
  				}
  			}else{
  				logger.debug("The file does not exist");
  			}
  			contextMessage.put("result_error",result_error);
  			contextMessage.put("result_succ",result_succ);
  			//file.delete();//删除该文件
  		}catch(Exception e){
  			logger.error(e.toString());
  			return contextMessage;
  		}
  		return contextMessage;
  	}
    
    public Map<String,String> convertMessageToMap(Message message){
        String msgBody = null;
        Integer msgCode = new Integer(0);
        try {
            msgCode = message.getIntProperty("msgCode");
            msgBody = message.getStringProperty("msgBody");
            logger.info("消息返回信息：msgCode:{},msgBody:{}", msgCode, msgBody);
        } catch (JMSException e) {
            e.printStackTrace();
        }
        @SuppressWarnings("unchecked")
		Map<String, String> json = (Map<String, String>) new JsonMapper().fromJson(msgBody, Map.class);
        return json;
    }

}
