package com.nokia.ices.app.dhss.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nokia.ices.app.dhss.config.ProjectProperties;
import com.nokia.ices.app.dhss.config.SubscriberConfig;
import com.nokia.ices.app.dhss.core.utils.Encodes;
import com.nokia.ices.app.dhss.core.utils.JsonMapper;
import com.nokia.ices.app.dhss.domain.equipment.EquipmentUnit;
import com.nokia.ices.app.dhss.domain.number.NeGroup;
import com.nokia.ices.app.dhss.domain.number.NumberGroup;
import com.nokia.ices.app.dhss.domain.number.NumberSection;
import com.nokia.ices.app.dhss.domain.subscriber.SubtoolPgwLdapIp;
import com.nokia.ices.app.dhss.jms.model.JumpContent;
import com.nokia.ices.app.dhss.jms.model.MessageModel;
import com.nokia.ices.app.dhss.repository.equipment.EquipmentUnitRepository;
import com.nokia.ices.app.dhss.repository.number.NeGroupRepository;
import com.nokia.ices.app.dhss.repository.number.NumberGroupRepository;
import com.nokia.ices.app.dhss.repository.number.NumberSectionRepository;
import com.nokia.ices.app.dhss.repository.subscriber.CheckSubtoolResultRepository;
import com.nokia.ices.app.dhss.repository.subscriber.SubtoolLadpIpRepository;
import com.nokia.ices.app.dhss.service.EditVrlOrSgsnService;
import com.nokia.ices.app.dhss.service.SecurityService;
import com.nokia.ices.app.dhss.service.SubscriberService;
@Service
public class SubscriberServiceImpl implements SubscriberService {
	
	private final static Logger logger = LoggerFactory.getLogger(SubscriberServiceImpl.class);

	public static final String SRCQ_NAME_UNIT = "DHLR-MML";
	
	@Autowired
	private EquipmentUnitRepository equipmentUnitRepository;
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Autowired
	private SubscriberConfig subscriberConfig;
	
	@Autowired
	private SecurityService securityService;
	
	@Autowired
	private NumberGroupRepository  numberGroupRepository;
	
	@Autowired
	private NumberSectionRepository numberSectionRepository;
	
	@Autowired
	private NeGroupRepository neGroupRepository;
	
	@Autowired
	private SubtoolLadpIpRepository subtoolLadpIpRepository;
	
	@Autowired
	private EditVrlOrSgsnService editVrlOrSgsnService;
	
	@Autowired
	private CheckSubtoolResultRepository checkSubtoolResultRepository;
	
	/**
	 * 
	 * 用于用户数据管理命令下发匹配对应ip做号码缓存
	 */
	public static Map<String, List<String>> NUMBER_CACHE = new HashMap<String, List<String>>();

	
	
	/**
	 * 发送subtool 命令
	 * execStatus:-1命令中未找到msisdn or imsi
	 * execStatus:0 成功
	 * execStatus:1 异常
	 * execStatus:2  未找到合适的IP
	 * 
	 * 
	 */
	@Override
	public int sendCommandSubtool(String command, String checkName,String userName,String token,String path) {

		int execStatus = -1;
		// 提取imsi或者msisdn号码
		String regStr = "[0-9]{13,}";
		String number = regNumber(regStr, command);

		logger.debug("command = " + command + "   number = " + number);
		if (null != number) {
			// 开始下发命令
			if (command.startsWith("subtool")) {
				execStatus = sendSubtoolCmd(command, checkName, number,userName,token,path);
			} else {
				execStatus = sendMmlCmd(command, checkName, number ,userName);
			}
		}

		return execStatus;
	}
	
	
	
	/**
	 * 针对移动客户群，发送subtool命令
	 * @param command
	 * @param checkName
	 * @param number
	 * @return
	 */
	private int sendSubtoolCmd(String command, String checkName, String number,String userName,String token,String path) {

		/**
		 * 命令中代码MSISDN，IMSI替换成小写，因subtool支持小写
		 */
		String type = null;
		if (number.length() > 13) {
			type = "2";
			if (command.contains("IMSI") || command.contains("imsi")) {
				command = command.replaceAll("IMSI", "imsi");
			}
		} else {
			type = "1";
			if (command.contains("MSISDN") || command.contains("msisdn")) {
				command = command.replaceAll("MSISDN", "msisdn");
			}
		}
		if (command.lastIndexOf(";") != -1) {
			command = command.substring(0, command.lastIndexOf(";"));
		}
		// 获取ip
//		List<String> pgwIp = getCommandParams(shiroUser, number, type);
		List<String> pgwIp = getIpList(token, number, type);
		
		
		if (null != pgwIp && pgwIp.size() > 0) {
			if (command.contains("ZMIMSGSN") || command.contains("ZMIMVLR")) {
				// 杭州针对vlr/sgsn 走soapGetw 下发命令
				editVrlOrSgsnService.editVlrOrSgsn(command, pgwIp, checkName,userName);
			} else {
				// 其他统一走subtool环境工具下发命令
				new Thread(new SendSubToolThread(command, checkName, checkSubtoolResultRepository, pgwIp, /*shiroUser.getUserName()*/userName,
						number,path)).start();
			}
		} else {
			logger.debug("The soap pgwIp is not Null or ListIp size = 0");
//			return "zero";
			return 2;
		}

		return 0;

	}
	
	
	private List<String> getIpList(String token, String handleNumber, String type){
		List<String> listIp = null;
		if (NUMBER_CACHE.containsKey(handleNumber)) {
			logger.debug("get cache number ......" + handleNumber);
			listIp = NUMBER_CACHE.get(handleNumber);
		} else {
			logger.debug("get dataBase number ......" + handleNumber);
			String isFlag = subscriberConfig.getIfDynamicGetIp();
			if ("true".equals(isFlag)) {
				List<EquipmentUnit> soapUrlList = findAllUnitTypeByNumberSection(handleNumber,type, "PGW", token);
				listIp = filterPgwIpAndLdapIp(soapUrlList);
			} else {
				/**
				 * 获取配置ip
				 */
				listIp = new ArrayList<String>();
				List<String> Ips = subscriberConfig.getSoapIpList();
				for (String ip : Ips) {
					if (!ip.equals("")) {
						listIp.add(ip);
					}
				}
			}
			// 加入缓存
//			if (null != listIp && listIp.size() > 0) {
//				NUMBER_CACHE.put(handleNumber, listIp);
//			}
		}
		return listIp;
	}
	
	/**
	 * 根据pgwIp匹配ldap ip
	 * 
	 * @param list
	 * @return
	 */
	private List<String> filterPgwIpAndLdapIp(List<EquipmentUnit> list) {

		logger.debug(" pgw ip size..." + list.size());
		List<String> listIp = new ArrayList<>();
		if (null != list && list.size() > 0) {
			for (EquipmentUnit unit : list) {
				List<SubtoolPgwLdapIp> pgwLdaps = subtoolLadpIpRepository.findSubtoolByPgwIpEquals(unit.getServerIp());
				if (null != pgwLdaps && pgwLdaps.size() > 0) {
					for (SubtoolPgwLdapIp sub : pgwLdaps) {
						String pgwLdap = sub.getPgwIp() + ":" + sub.getLdapIp();
						if (!listIp.contains(pgwLdap)) {
							listIp.add(pgwLdap);
							logger.debug("unitType pgw ip and ldap ip ..." + pgwLdap);
						}
					}
				} else {
					// 未匹配使用全部
					if (!listIp.contains(unit.getServerIp())) {
						listIp.add(unit.getServerIp());
					}
				}
			}
		} else {
			logger.debug("The soap object url is  0 or null");
		}
		return listIp;

	}
	
	
	
	/**
	 * 根据用户号码，单元类型，操作类型获取匹配的单元IP
	 */
	@SuppressWarnings("unchecked")
	public List<EquipmentUnit> findAllUnitTypeByNumberSection(String handleNumber,
			String type, String unitTypeName, String token) {
		//获取号码段分组
		List<Map<String, String>> resultNumberGroup = getResource(token, "number_group", true);

		if(resultNumberGroup.size()==0){
			logger.debug("Not area assign permissions......");
			return new ArrayList<>();
		}
		
		//查询具有该地区权限的number
		List<NumberSection> numberList = findNumberSectionList(resultNumberGroup);
		
		List<Long> matchIdList = new ArrayList<Long>();
		for (NumberSection numberModel : numberList) {
			String str = "";
			if ("1".equals(type) && StringUtils.isNotEmpty(numberModel.getMsisdn())) {
				str = numberModel.getMsisdn().trim();
			} else {
				if(StringUtils.isNotEmpty(numberModel.getImsi())){
					str = numberModel.getImsi().trim();
				}
			}
			if (StringUtils.isNotEmpty(str)) {
				if (handleNumber.equals(str)) {
					matchIdList.add(numberModel.getId());
				} else if (handleNumber.contains(str)) {
					matchIdList.add(numberModel.getId());
				}else if(str.contains("X")){
					//460077021X 特殊号码处理
					String [] x_imsi = {"0","1","2","3","4","5","6","7","8","9"};
					for(String imsi :x_imsi){
						String newImsi = str.replaceAll("X", imsi);
						if (handleNumber.contains(newImsi)) {
							matchIdList.add(numberModel.getId());
						}
					}
				}else if(str.contains("-")){
					//861582150-3、5-9 号码匹配处理
					 Map<String,Object> mapNumbers = getNumberStr(str);
					 String perfixNumber = mapNumbers.get("perfix").toString();
					 List<Integer> numbers =(List<Integer>) mapNumbers.get("suffix");
					 for(Integer number :numbers){
						if (handleNumber.contains(perfixNumber+number)) {
							matchIdList.add(numberModel.getId());
						}
					 }
					
				}
			}/* else {
				logger.debug("IMSI/NUMBER 未获取到，请检查配置........   ID="+ nse.getId());
			}*/
		}
		/**
		 * 查找与号码段匹配的PGW单元
		 */ 
		return findEquipmentUnitList(matchIdList, unitTypeName);
//		return equipmentUnitRepository.findListByNeNumberSectionIdInAndUnitTypeEquals(matchIdList,EquipmentUnitType.valueOf(EquipmentUnitType.class, unitTypeName));
	}
	
	
	private List<EquipmentUnit> findEquipmentUnitList(List<Long> ids,String unitType){
		
		List<NeGroup> groupList = neGroupRepository.findNeGroupByNumberIdIn(ids);
		
		ids.clear();
		
		for (NeGroup neGroup : groupList) {
			ids.add(neGroup.getNeId());
		}
		
		return equipmentUnitRepository.findEquipmentUnitByNeIdInAndUnitTypeEquals(ids, unitType);
	}
	
	
		
	
	/**
	 * 获取匹配好的imsi或者msisdn
	 * @param imsiOrMsisdn
	 * @return
	 */
	public static Map<String,Object> getNumberStr(String imsiOrMsisdn){
	  
	  Map<String,Object> mapNumber = new HashMap<String,Object>();
	  List<Integer> numbers = new ArrayList<>();
	  String  prefixNumber = null;
	  String [] aa = imsiOrMsisdn.split("、");
	  
	  for(int i=0;i<aa.length;i++){
		  if(i>0){
			  matenumber(aa[i],numbers);
		  }else{
			  if(aa[0].contains("-")){
				  //循环匹配号段值
				  int startLength = aa[0].indexOf("-")-1;
				  prefixNumber = aa[0].substring(0, startLength);
				  String newImsi = aa[0].substring(startLength, aa[0].length());
				  matenumber(newImsi,numbers);
			  }else{
				  //直接使用该值
				  prefixNumber = aa[0];
			  }
		  }
		  
	  }
	  mapNumber.put("perfix", prefixNumber);
	  mapNumber.put("suffix", numbers);
	  return mapNumber;
	}
	
	
	/**
	 * 根据861582150-3、5-9 号段进行循环匹配
	 * @param 号段 suffix 后缀 
	 * @param numbers 后缀变化值
	 */
	private static void matenumber(String  suffix,List<Integer> numbers){
		 Integer startims = Integer.parseInt(suffix.split("-")[0]);
		  Integer endims = Integer.parseInt(suffix.split("-")[1]);
		  numbers.add(startims);
		  while(startims<endims){
			  startims++;
			  numbers.add(startims);
		  }
	}
	
	
	private List<NumberSection> findNumberSectionList(List<Map<String, String>> resultNumberGroup){
		List<Long> Ids = new ArrayList<>();
		for (Map<String, String> map : resultNumberGroup) {
			Object objId = map.get("id");
			Ids.add(Long.parseLong(objId.toString()));
		}
		List<NumberGroup> numberGroupList = numberGroupRepository.findListByGroupIdIn(Ids);
		
		Ids.clear();
		
		for (NumberGroup numberGroup : numberGroupList) {
			Ids.add(numberGroup.getNumberId());
		}
		return numberSectionRepository.findNumberByIdIn(Ids);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Map<String, String>> getResource(String token,String type,boolean flag){
		Map paramsMap = new HashMap<>();
		paramsMap.put("token", token);
		paramsMap.put("resourceFlag", type);
		paramsMap.put("contentFlag", "1");
		paramsMap.put("assocResourceFlag", "");
		paramsMap.put("assocResourceAttr", "");
		paramsMap.put("assocResourceAttrValue", "");
		return securityService.getResource(paramsMap,flag);
	}
	
	
	
	/**
	 * 发送mml命令，针对联通客户群
	 * 
	 * @param command
	 * @param checkName
	 * @param number
	 * @return
	 */
	private int sendMmlCmd(String command, String checkName, String number,String userName) {

		command = command.replaceAll("\n", "");
		String uuid = UUID.randomUUID().toString();
		EquipmentUnit unit = equipmentUnitRepository.findEquipmentUnitByServerIpEquals(subscriberConfig.getMmlIp());
		if (null != unit) {
			
			String password = Encodes.encodeHex(subscriberConfig.getMmlUserPwd() != null ? subscriberConfig.getMmlUserPwd().getBytes() : "".getBytes());
			Map<String,String> content = new HashMap<String,String>();
			content.put("cmd", "DHLR_USERMANAGER_COMMAND|" + command);
			content.put("ct", "2");
			content.put("rt", "1");
			String unitType = unit.getUnitType();
			
			JumpContent jump = new JumpContent(unit.getJumperIp(), unit.getJumperPort(), unit.getJumperUserName(), 
					unit.getJumperPassword(), unit.getJumpProtocol());
			
			MessageModel model = new MessageModel(8,"dhss", unit.getNeType(), 
					SRCQ_NAME_UNIT,ProjectProperties.getDesQName(),uuid, unit.getUnitName(), 
					"DHSS_"+subscriberConfig.getMmlProtocol(), password, Integer.valueOf(subscriberConfig.getMmlPort()),5, 
					subscriberConfig.getMmlProtocol(),subscriberConfig.getMmlUserName(), unit.getServerIp(), content, 
					unit.getHostname(),"","", "", "",unitType, 71000,jump,unit.getIsDirect() ? 1 : 0);

			jmsTemplate.setDefaultDestinationName(ProjectProperties.getDesQName());
			jmsTemplate.send(new MessageCreator() {
				public Message createMessage(Session session) throws JMSException {
					TextMessage txtMessage = session.createTextMessage("");
					txtMessage.setStringProperty("msgBody", new JsonMapper().toJson(model));
					txtMessage.setIntProperty("msgCode",model.getMsgCode());
					txtMessage.setJMSPriority(5);
					model.setHostname(userName);
					model.setNetFlag(checkName);
					model.setApp(number);
					SubtoolMessageConsumer.cacheCheckName.put(uuid,model);
					logger.debug("message.getSrcQ() = {},message.getDestQ() = {}", model.getMsgCode(),
							model.getDestQ());
					logger.debug(txtMessage.toString());
					return txtMessage;
				}
			});
			return 0;
		} else {
			logger.debug("The SOAP_GW is IP NULL.........");
			return 1;
		}
	}
	
	

	@SuppressWarnings({ "resource", "deprecation" })
	@Override
	public void exportTemplate(String title, String name, String defaultValue, HttpServletResponse response) {
		try {
			response.setHeader("content-disposition", "attachment;filename="  
			        + URLEncoder.encode(name + ".xls", "UTF-8"));
			
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("sub_template");
			int index = 0;
			HSSFRow headerRow = sheet.createRow(index);
			String[] headers = title.split(",");
			int columnIndex = 0;
			for (int i = 0; i < headers.length; i++) {
				String headerName = headers[i];
				sheet.setColumnWidth(i, 5000);
				CellStyle style = workbook.createCellStyle();  
		        style.setFillForegroundColor(IndexedColors.AQUA.getIndex());  
		        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); 
		        
		        style.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
		        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
		        style.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
		        style.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
		        
		        HSSFFont font = workbook.createFont();
		        font.setFontName("仿宋_GB2312");
		        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体显示
		        style.setFont(font);//选择需要用到的字体格式
		        
		        
				HSSFCell cell = headerRow.createCell(columnIndex);
				cell.setCellStyle(style);
				cell.setCellValue(new HSSFRichTextString(headerName));
				columnIndex ++;
			}
			index ++;
			HSSFRow valueRow = sheet.createRow(index);
			String []defaults = defaultValue.split(",");
			columnIndex = 0;
			for (int i = 0; i < defaults.length; i++) {
				String value = defaults[i];
		        
				HSSFCell cell = valueRow.createCell(columnIndex);
				cell.setCellValue(new HSSFRichTextString(value));
				columnIndex ++;
			}
			
			workbook.write(response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@SuppressWarnings({ "resource", "unused", "deprecation" })
	@Override
	public List<Map<String,String>> importTemplate(MultipartFile file, HttpServletRequest request) {
		List<Map<String,String>> rowList = new ArrayList<Map<String,String>>();
		String path = request.getSession().getServletContext().getRealPath("subscriber_template");
        String fileName = file.getOriginalFilename();  
        File targetFile = new File(path);  
        if(!targetFile.exists()){  
            targetFile.mkdirs();  
        }
        File newFile = new File(targetFile.getPath()+"/"+fileName);
        if(newFile.exists()){
        	newFile.delete();
        }
        //保存  
        try {  
        	newFile.createNewFile();
            file.transferTo(newFile);
            HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(newFile));  
            HSSFSheet sheet = wb.getSheetAt(0);
            HSSFRow headers = sheet.getRow(sheet.getFirstRowNum());
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            	HSSFRow valueRow = sheet.getRow(i);
            	Map<String,String> valueMap = new HashMap<>();
				for (int j = 0; j < valueRow.getLastCellNum(); j++) {
					switch(valueRow.getCell(j).getCellType()){
						case HSSFCell.CELL_TYPE_NUMERIC:
							valueMap.put((j+1)+"", new DecimalFormat("#.######").format(valueRow.getCell(j).getNumericCellValue()));
							break;
						default:
							valueMap.put((j+1)+"",valueRow.getCell(j).toString());
							break;	
					}
						
				}
				rowList.add(valueMap);
			}
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
		return rowList;
	}

	
	private static String regNumber(String regStr, String command) {

		Pattern pattern = Pattern.compile(regStr);
		Matcher matcher = pattern.matcher(command);
		if (matcher.find()) {
			return matcher.group();
		} else {
			logger.debug("Number is not standard........");
			return null;
		}
	}
}
