package com.nokia.ices.app.dhss.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nokia.ices.app.dhss.config.ProjectProperties;
import com.nokia.ices.app.dhss.config.PropertiesConfig;
import com.nokia.ices.app.dhss.domain.maintain.MaintainOperation;
import com.nokia.ices.app.dhss.domain.maintain.MaintainResult;
import com.nokia.ices.app.dhss.domain.maintain.SecurityManageResult;
import com.nokia.ices.app.dhss.service.MaintainService;
import com.nokia.ices.app.dhss.service.SecurityService;
import com.nokia.ices.app.dhss.vo.OperationMap;

@RestController
public class MaintainController {
	
	@Autowired
	private MaintainService maintainService;
	@Autowired
	private SecurityService securityService;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddmmHH");
	
	@RequestMapping(value="/api/v1/security/result",method=RequestMethod.POST)
	public Iterable<SecurityManageResult> saveSecurityManageResult(@RequestBody List<SecurityManageResult> operationMap){
		return maintainService.saveSecurityManageResult(operationMap);
	}
	
	@RequestMapping(value="/api/v1/security/result/list",method=RequestMethod.GET)
	public Page<SecurityManageResult> getSecurityManageResult(@RequestParam(value="startTime",required=false)String startTime,
			@RequestParam(value="endTime",required=false)String endTime,
			Pageable page){
		SimpleDateFormat s = new SimpleDateFormat("yyyyy-MM-dd HH:mm:ss");

		Map<String, Object> paramMap = new HashMap<String,Object>();
		if(StringUtils.isNotBlank(startTime)) {
			try {
				paramMap.put("createDate_GT", s.parse(startTime));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if(StringUtils.isNotBlank(endTime)) {
			try {
				paramMap.put("createDate_LE", s.parseObject(endTime));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return maintainService.findSecurityManageResultPage(paramMap, page);
	}
	
	@RequestMapping(value="/api/v1/maintain/send-cmd",method=RequestMethod.POST,consumes={"application/json;charset=UTF-8"})
	public MaintainOperation sendCmd(@RequestBody OperationMap operationMap){
		return maintainService.saveMaintainResult(operationMap);
	}
	
	@RequestMapping(value="/api/v1/maintain/read-log")
	public Map<String,Object> readLog(@RequestParam(value="path") String path,@RequestParam(value="unitName") String unitName){
		Map<String,Object> map = new HashMap<String,Object>();
		String [] pathArray = path.split("@");
		StringBuilder result = new StringBuilder();
		try{
			for (String string : pathArray) {
				if(!StringUtils.isNotEmpty(string)){
					result.append(System.lineSeparator()+unitName);
				}else{
					File file = new File(PropertiesConfig.getBaseLogPath()+string);
					if(file.exists()){
				            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
				            String s = null;
				            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
				                result.append(System.lineSeparator()+new String(s.getBytes("iso8859-1"),"UTF-8"));
				            }
				            br.close();    
					}else{
						result.append(System.lineSeparator()+unitName);
					}
				}
			}
		}catch(Exception e){
            e.printStackTrace();
        }
		map.put("content", result.toString());
		return map;
		   
	}
	
	@RequestMapping(value = "api/v1/maintainOperateion/downloadLogByUuid/{uuid}/download")
    public void downloadLogByUUID(@PathVariable String uuid, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
		String compBasePath = PropertiesConfig.getCompBasePath();
        String operationLogName = sdf.format(new Date()) + new Date().getTime() + ".txt";
        String operationLogPath = compBasePath + File.separator + operationLogName;
        File operationDir = new File(compBasePath);
        if (!operationDir.exists() || !operationDir.isDirectory()) {
            operationDir.mkdirs();
        }
        File operationLogFile = new File(operationLogPath);
        if (operationLogFile.exists() && operationLogFile.isFile()) {
            // do nothing
        } else {
        	List<MaintainResult> resultList = maintainService.findMaintainResultListByUUId(uuid);
        	maintainService.resultData(operationLogFile, resultList);
        }
        maintainService.downloadFile(request, response, operationLogFile, operationLogName);
	}
	
	
	@RequestMapping(value = "api/v1/maintainOperateion/downloadLog/{operationId}/download")
    public void downloadLog(@PathVariable Long operationId, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        MaintainOperation maintainOperation = maintainService.findMaintainOperation(operationId);
        String compBasePath = PropertiesConfig.getCompBasePath();
        String operationLogName = sdf.format(maintainOperation.getRequestTime()) + "-"
                + maintainOperation.getCommandCategory() + ".txt";
        String operationLogPath = compBasePath + File.separator + operationLogName;
        File operationDir = new File(compBasePath);
        if (!operationDir.exists() || !operationDir.isDirectory()) {
            operationDir.mkdirs();
        }
        // 若汇总日志文件 已存在，则不再进行汇总
        File operationLogFile = new File(operationLogPath);
        if (operationLogFile.exists() && operationLogFile.isFile()) {
            // do nothing
        } else {
        	List<MaintainResult> resultList = maintainService.findMaintainResultListByOperationId(String.valueOf(operationId));
        	maintainService.resultData(operationLogFile, resultList);
        }
        maintainService.downloadFile(request, response, operationLogFile, operationLogName);
    }
	
	

	
	
	@RequestMapping(value = "/downloadDetailLog", method = RequestMethod.GET)
	public void downloadDetailLog(HttpServletRequest request,
			HttpServletResponse response) {

		Map<String, String[]> reqParams = request.getParameterMap();
		String filePath = reqParams.containsKey("filePath") ? reqParams .get("filePath")[0] : "";
		String sessionText = reqParams.containsKey("logText") ? reqParams .get("logText")[0] : "";
		String rootPath = ProjectProperties.getCOMP_BASE_PATH();
		String operationPath = rootPath + filePath ;
		String operationLogName =  sessionText + ".txt";
		
		try {
			download(request, response, operationPath, "application/octet-stream",operationLogName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
     * 下载日志
     * @param request
     * @param response
     * @param downLoadPath
     * @param contentType
     * @param realName
     * @throws Exception
     */
    public static void download(HttpServletRequest request,HttpServletResponse response, String downLoadPath,
			String contentType, String realName) throws Exception {
		request.setCharacterEncoding("UTF-8");
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		File file = new File(downLoadPath);
		response.setContentType(contentType);
		
		String operationLogName = new String( realName.getBytes("UTF-8"), "iso-8859-1");
		if (request.getHeader("User-Agent").indexOf("Trident") != -1) {
			operationLogName = java.net.URLEncoder.encode(realName, "UTF-8");
		} 
		response.setHeader("Content-disposition", "attachment; filename=" + operationLogName);
		
		response.setHeader("Content-Length", String.valueOf(file.length()));
		bis = new BufferedInputStream(new FileInputStream(file));
		bos = new BufferedOutputStream(response.getOutputStream());
		byte[] buff = new byte[2048];
		int bytesRead;
		while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
			bos.write(buff, 0, bytesRead);
		}
		bis.close();
		bos.close();
	}
	
	
	@RequestMapping("api/v1/maintain/operation")
	public Page<MaintainOperation> findMaintainOperation(@RequestHeader("Ices-Access-Token") String token,
			@RequestParam(value="code",required=false)String code,
			@RequestParam(value="startTime",required=false)String startTime,
			@RequestParam(value="endTime",required=false)String endTime,Pageable page) throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String,Object> paramMap = new HashMap<String,Object>();
		if(StringUtils.isNotEmpty(code)){
			paramMap.put("commandCategory_EQ", code);
		}
		if(StringUtils.isNotEmpty(startTime)){
			paramMap.put("requestTime_GE", format.parse(startTime));
		}
		if(StringUtils.isNotEmpty(endTime)){
			paramMap.put("requestTime_LT", format.parse(endTime));
		}
		paramMap.put("createBy_EQ", securityService.getSystemUser(token));
		
		return maintainService.findMaintainOperationPage(paramMap,page);
	}
	
	@RequestMapping("api/v1/maintain/result")
	public List<MaintainResult> findMaintainResult(@RequestParam(value="id",required=false)String id){
		return maintainService.findMaintainResultListByOperationId(String.valueOf(id));
	}

}
