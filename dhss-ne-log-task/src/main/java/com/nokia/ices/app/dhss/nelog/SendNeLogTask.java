package com.nokia.ices.app.dhss.nelog;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.nokia.ices.app.dhss.nelog.jms.MessageModel;
import com.nokia.ices.app.dhss.nelog.jms.SendMessageJms;
import com.nokia.ices.app.dhss.nelog.log.LogConfig;

@Component
public class SendNeLogTask {

	private final static Logger logger = LoggerFactory.getLogger(SendNeLogTask.class);
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final String scriptName = "SSH_DHLR_NE_LOG";

	/**
	 * 根据当前服务器内核数*10扩展创建线程池个数
	 */
	// private static ExecutorService EXECUTOR_SERVICE =
	// Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() *
	// 10);

	@Value("${dhss.desQName}")
	private String desQName;

	@Value("${dhss.srcQName}")
	private String srcQName;

	@Value("${dhss.sendUnitTtype}")
	private String unitTypes;

	@Autowired
	SendMessageJms sendMessageJms;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@PostConstruct
	public void sendNeLogTasks() {
		
		logger.info(LogConfig.PROJECT_NAME+"|"+LogConfig.MODULE_NAME+"|"+LogConfig.TASK_NAME+"|START"); 
		 

		String unitSql = "SELECT a.unit_name,a.unit_type,b.ne_name,b.ne_type,a.login_name,a.login_password,"
				+ " a.root_password,a.server_ip,a.server_protocol,a.server_port FROM equipment_unit a JOIN equipment_ne b"
				+ " ON a.ne_id = b.id AND a.unit_type";
		List<String> args = new ArrayList<>();
		String[] in_datas = unitTypes.split(",");
		String inString = "";
		for (int i = 0; i < in_datas.length; i++) {
			args.add(in_datas[i]);
			if (i > 0) {
				inString += ", ";
			}
			inString += "?";
		}
		List<Map<String, Object>> resultList = jdbcTemplate.queryForList(unitSql + " in(" + inString + ")",
				args.toArray());

        List<String []> paramsAdd = new ArrayList<>();
		for (Map<String, Object> params : resultList) {

			String [] adds  = new String[5];
			String unitType = params.get("unit_type").toString();
			String neType = params.get("ne_type").toString();
			String unit_name = params.get("unit_name").toString();
			String ne_name = params.get("ne_name").toString();
			adds[0] = dateFormat.format(new Date());
			adds[1] = ne_name;
			adds[2] = neType;
			adds[3] = unit_name;
			adds[4] = unitType;
			paramsAdd.add(adds);

			String sessionId = UUID.randomUUID().toString().replaceAll("[-]", "");
			MessageModel message = new MessageModel();
			message.setApp("DHSS");
			message.setSessionid(sessionId);
			String procotol = params.get("server_protocol").toString().replace("2", "");
			message.setProcotol(procotol);
			message.setType(neType);
			message.setNe(unit_name);
			message.setNeConnType("DHSS_" + procotol);
			String password = params.get("login_password").toString();
			message.setPassword(Hex.encodeHexString(password.getBytes()));
			message.setUsername(params.get("login_name").toString());
			message.setPort(Integer.parseInt(params.get("server_port").toString()));
			message.setIp(params.get("server_ip").toString());
			Map<String, String> content = new HashMap<String, String>();
			content.put("ct", "2");
			content.put("rt", "1");
			String root_password = params.get("root_password").toString();
			content.put("cmd", scriptName + "|root@" + Hex.encodeHexString(root_password.getBytes()));
			message.setContent(content);
			message.setTaskNum(71000);
			message.setMsgCode(71000);
			message.setUnitType(unitType);
			message.setSrcQ(srcQName);
			message.setDestQ(desQName);
			// 发送AMQ
			sendMessageJms.sendMessage(message);
		}

		/**
		 * 添加记录到DB
		 */
		String exesql = "INSERT INTO equipment_ne_operation_log(give_time,ne_name,ne_type,unit_name,unit_type)VALUES(?,?,?,?,?)";
		jdbcTemplate.batchUpdate(exesql, new BatchPreparedStatementSetter(){
			public void setValues(PreparedStatement ps,int i){
				try {
					ps.setString(1, paramsAdd.get(i)[0]);
					ps.setString(2, paramsAdd.get(i)[1]);
					ps.setString(3, paramsAdd.get(i)[2]);
					ps.setString(4, paramsAdd.get(i)[3]);
					ps.setString(5, paramsAdd.get(i)[4]);
				} catch (SQLException e) {
					logger.info(LogConfig.PROJECT_NAME+"|"+LogConfig.MODULE_NAME+"|"+LogConfig.TASK_NAME
							+"|SQLException msg, reason:"+e.toString());
				}
			}
			public int getBatchSize(){
				return paramsAdd.size();
			}
		});

		logger.info(LogConfig.PROJECT_NAME+"|"+LogConfig.MODULE_NAME+"|"+LogConfig.TASK_NAME+"|END");
	}
	

}
