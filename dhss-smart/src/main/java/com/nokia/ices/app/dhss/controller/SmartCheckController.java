package com.nokia.ices.app.dhss.controller;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nokia.ices.app.dhss.config.DhssProjectProperties;
import com.nokia.ices.app.dhss.domain.smart.SmartCheckJob;
import com.nokia.ices.app.dhss.domain.smart.SmartCheckResult;
import com.nokia.ices.app.dhss.event.SmartCheckJobEventHandler;
import com.nokia.ices.app.dhss.service.SmartCheckService;

@RestController
@CrossOrigin
public class SmartCheckController {
	
	private Logger logger = LoggerFactory.getLogger(SmartCheckController.class);
	 
	
	@Autowired
	private SmartCheckService smartCheckService;
	
	
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
	SimpleDateFormat formats = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
	
	
	@RequestMapping("api/v1/startJobCheck/{id}")
	public boolean startJobCheck(@PathVariable Long id){
		return smartCheckService.checkJob(id);
	}
	
	@RequestMapping("api/v1/findResultPage")
	public Page<SmartCheckResult> getfindResultPage(@RequestParam(value="scheduleId",required=false)String scheduleId,
													@RequestParam(value="checkItemId",required=false)String checkItemId,
													@RequestParam(value="neId",required=false)String neId,
													@RequestParam(value="resultCode",required=false)String resultCode,
													@RequestParam(value="unitType",required=false)String unitType,
													@RequestParam(value="unitName",required=false)String unitName,
													@RequestParam(value="startTime",required=false)String startTime,
													@RequestParam(value="endTime",required=false)String endTime,
													@RequestParam(value="checkItemName",required=false)String checkItemName,Pageable page) throws ParseException{
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String,Object> paramMap = new HashMap<String,Object>();
		if(StringUtils.isNotEmpty(scheduleId)){
			paramMap.put("scheduleId_EQ", scheduleId);
		}
		if(StringUtils.isNotEmpty(checkItemId)){
			paramMap.put("checkItemId_EQ", checkItemId);
		}
		if(StringUtils.isNotEmpty(neId)){
			paramMap.put("neName_EQ", neId);
		}
		if(StringUtils.isNotEmpty(resultCode)){
			paramMap.put("resultCode_EQ", resultCode.equals("1"));
		}
		if(StringUtils.isNotEmpty(unitType)){
			paramMap.put("unitTypeName_EQ", unitType);
		}
		if(StringUtils.isNotEmpty(unitName)){
			paramMap.put("unitName_EQ", unitName);
		}
		if(StringUtils.isNotEmpty(startTime)){
			paramMap.put("startTime_GE", format1.parse(startTime));
		}
		if(StringUtils.isNotEmpty(endTime)){
			paramMap.put("startTime_LT", format1.parse(endTime));
		}
		if(StringUtils.isNotEmpty(checkItemName)){
			paramMap.put("checkItemName_LIKE", checkItemName);
		}
		logger.info("{}",paramMap);
		return smartCheckService.getfindResultPage(paramMap, page);
 		
	}
	
	
	@RequestMapping(value = "api/v1/donwload/smartCheckDetailPageList/download", method = RequestMethod.GET)
	public void donwloadDmartCheckDetailPageList(HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		
		
		String checkItemName = request.getParameter("checkItemName");
		
		
		String unitName = request.getParameter("unitName");
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		
		String scheduleId = request.getParameter("scheduleId");
		String checkItemId = request.getParameter("checkItemId");
		String neId = request.getParameter("neId");
		String resultCode = request.getParameter("resultCode");
		String unitType = request.getParameter("unitType");
		
		
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String,Object> paramMap = new HashMap<String,Object>();
		if(StringUtils.isNotEmpty(scheduleId)){
			paramMap.put("scheduleId_EQ", scheduleId);
		}
		if(StringUtils.isNotEmpty(checkItemId)){
			paramMap.put("checkItemId_EQ", checkItemId);
		}
		if(StringUtils.isNotEmpty(neId)){
			paramMap.put("neId_EQ", neId);
		}
		if(StringUtils.isNotEmpty(resultCode)){
			paramMap.put("resultCode_EQ", resultCode.equals("1"));
		}
		if(StringUtils.isNotEmpty(unitType)){
			paramMap.put("unitTypeName_EQ", unitType);
		}
		if(StringUtils.isNotEmpty(unitName)){
			paramMap.put("unitName_EQ", unitName);
		}
		if(StringUtils.isNotEmpty(startTime)){
			paramMap.put("startTime_GE", format1.parse(startTime));
		}
		if(StringUtils.isNotEmpty(endTime)){
			paramMap.put("startTime_LT", format1.parse(endTime));
		}
		if(StringUtils.isNotEmpty(checkItemName)){
			paramMap.put("checkItemName_LIKE", checkItemName);
		}
		
		List<SmartCheckResult> list = smartCheckService.getfindResult(paramMap);
		logger.info("SmartCheckResult xls : {}",paramMap);
		String [] headers = new String[]{"Ne Type","Unit Type","Ne Name","Unit Name","Command Name","Status","Execute Time","Abnormal Information"};
		
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet();
		XSSFRow row = sheet.createRow(0);
		XSSFCellStyle style = workbook.createCellStyle();
		style.setFillForegroundColor(new XSSFColor(new Color(0xffffff00)));

//		style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//		style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        style.setAlignment(HorizontalAlignment.CENTER);
//		style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		
		for (int i = 0; i < headers.length; i++) {

			XSSFCell cell = row.createCell(i);
			XSSFRichTextString text = new XSSFRichTextString(headers[i]);
			cell.setCellStyle(style);
			
			cell.setCellValue(text);

		}
		
		
		int index = 1;
		for (SmartCheckResult result : list) {
			row = sheet.createRow(index);
			
			row.createCell(0).setCellValue(new XSSFRichTextString(result.getNeTypeName()));
			
			row.createCell(1).setCellValue(new XSSFRichTextString(result.getUnitTypeName()));
			
			row.createCell(2).setCellValue(new XSSFRichTextString(result.getNeName()));
			
			row.createCell(3).setCellValue(new XSSFRichTextString(result.getUnitName()));
			
			row.createCell(4).setCellValue(new XSSFRichTextString(result.getCheckItemName()));
			
			row.createCell(5).setCellValue(new XSSFRichTextString(result.isResultCode() ? "SUCCESS" : " EXCEPTION"));
			
			row.createCell(6).setCellValue(new XSSFRichTextString(String.valueOf(result.getStartTime())));
			
			row.createCell(7).setCellValue(new XSSFRichTextString(result.getErrorMessage()));
			
			index++;
		}
		SimpleDateFormat formats = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		String fileName = "RESULT_"+formats.format(new Date())+".xlsx";
		
		
		String rootPath = request.getSession().getServletContext().getRealPath("") + File.separator + "do.download";
		File f = new File(rootPath);
		if(!f.exists()){
			f.mkdir();
		}
		String operationLogPath = rootPath + File.separator + fileName;
		
		OutputStream out = new FileOutputStream(operationLogPath);
		workbook.write(out);
		out.close();
		workbook.close();
		download(request, response, operationLogPath, "application/octet-stream", fileName);
		
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
	
	
	
	
	
	@RequestMapping("api/v1/findSmartCheckResultList")
	public List<Map<String,Object>> findSmartCheckResultList(@RequestParam(value="scheduleId")String scheduleId,
			@RequestParam(value="type")String type){ 
		return smartCheckService.findSmartCheckResultList(scheduleId,type);
	}
	
	@RequestMapping("api/v1/findSmartCheckResultArray")
	public List<Map<String,Object>> findSmartCheckResultArray(@RequestParam(value="scheduleId")String scheduleId,
			@RequestParam(value="type")String type){
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		result = smartCheckService.findSmartCheckResultList(scheduleId,type);
		 for (Map<String, Object> map : result) {
			 map.put("highlight", "0".equalsIgnoreCase(map.get("errorCount").toString())?"":"red");
		}
		 return result;
	}
	
	
	
	@RequestMapping("api/v1/smart-check-job/getPageSmartManage")
	public Page<SmartCheckJob> getPageSmartManage(@RequestParam(value="params",required=false)String params,Pageable pageable){
		Map<String, Object> paramMap = new HashMap<String,Object>();
		if(StringUtils.isNotEmpty(params)){
			paramMap.put("jobName_LIKE", params);
			paramMap.put("jobDesc_LIKE", params);
		}
		return smartCheckService.findSmartCheckJob(paramMap, pageable);
	}
	
	@Autowired
	private SmartCheckJobEventHandler smartCheckJobEventHandler;
	
	@RequestMapping("api/v1/smart-check-job/stopAll")
	public boolean stopAll(){
		Iterable<SmartCheckJob> jobs = smartCheckService.findSmartCheckJobAll();
		for (SmartCheckJob smartCheckJob : jobs) {
			smartCheckJob.setExecFlag(2);
			smartCheckJobEventHandler.handleBeforeSave(smartCheckJob);
		}
		smartCheckService.saveSmartCheckJob(jobs);
		return true;
	}
	
	
	
	
	@RequestMapping(value = "api/v1/downloadLog/download", method = RequestMethod.GET)
	public void downloadLog(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		Map<String, String[]> reqParams = request.getParameterMap(); 
		String neId = reqParams.containsKey("ne") ? reqParams.get("ne")[0] : ""; 
		String id = reqParams.containsKey("id") ? reqParams.get("id")[0] : ""; 
		String checkItemId = reqParams.containsKey("job") ? reqParams.get("job")[0] : ""; 
		String scheduleId = reqParams.containsKey("scheduleId") ? reqParams.get("scheduleId")[0] : "";
		String fileName = reqParams.containsKey("fileName") ? reqParams.get("fileName")[0] : "";
		Map<String, Object> map = new HashMap<String, Object>();
		if(StringUtils.isNotEmpty(checkItemId)){
			map.put("checkItemId_EQ", checkItemId);
		}
		if(StringUtils.isNotEmpty(neId)){
			map.put("neId_EQ", neId);
		} 
		if(StringUtils.isNotEmpty(scheduleId)){
			map.put("scheduleId_EQ", scheduleId);
		}
		if(StringUtils.isNotEmpty(id)){
			map.put("id_EQ", id);
		}
		String rootPath = request.getSession().getServletContext().getRealPath("");
		String operationPath = rootPath + File.separator + "do.download";
		String operationLogName =  fileName + ".txt";
		String operationLogPath = operationPath + File.separator + operationLogName;
		File operationDir = new File(operationPath);
		if (!operationDir.exists() || !operationDir.isDirectory()) {
			operationDir.mkdirs();
		}
		File operationLogFile = new File(operationLogPath);
		// 若汇总日志文件 已存在，则不再进行汇总

		if (operationLogFile.exists() && operationLogFile.isFile()) {
			// do nothing
			operationLogFile.delete();
		}   
		{
//			operationLogFile.createNewFile();
			OutputStream writer = null;
			List<SmartCheckResult> resultList = this.smartCheckService.getfindResult(map);

			try {
				writer = new FileOutputStream(operationLogFile);

				for (SmartCheckResult listMap : resultList) {
					if (id == null || "".equals(id)) {
						StringBuilder string = new StringBuilder();
						string.append("ne：");
						string.append(listMap.getNeName());
						string.append("  ");					 
						string.append("unit：");
						string.append(listMap.getUnitName());
						string.append("  ");
						string.append("checkItem：");
						string.append(listMap.getCheckItemName());
						string.append("  ");
						string.append("dateTime："); 
						string.append(listMap.getStartTime());

						writer.write(string.toString().getBytes());
						writer.write("\r\n".getBytes());
						writer.write("\r\n".getBytes());
					}

					// 若检查结果成功，则读取检查日志
					// 若检查结果失败，则直接读取错误信息
					String resultLog = DhssProjectProperties.getBaseLogPath() + (String) listMap.getFilePath();
					// 0:false 1:true

					InputStream reader = null;
					try {
						reader = new FileInputStream(resultLog);

						byte[] b = new byte[1024];
						int len = 0;
						while ((len = reader.read(b)) != -1) {
							writer.write(b, 0, len);
						}
						if (id == null || "".equals(id)) {
							writer.write("\r\n".getBytes());
							writer.write("----------------------------------------------------------------------------------"
									.getBytes());
							writer.write("\r\n".getBytes());
						}
					} catch (Exception e) {
						writer.write(e.getMessage().getBytes());
						if (id == null || "".equals(id)) {
							writer.write("\r\n".getBytes());
							writer.write("----------------------------------------------------------------------------------"
									.getBytes());
							writer.write("\r\n".getBytes());
						}

					} finally {
						if (null != reader) {
							reader.close();
						}
					}

					writer.flush();
				}
			} catch (IOException e) {

				e.printStackTrace();
			} finally {
				if (null != writer) {
					try {
						writer.close();
					} catch (IOException e) {

						e.printStackTrace();
					}
				}
			}
		}

		// 下载日志
		InputStream is = null;
		OutputStream os = null;

		try {
			request.setCharacterEncoding("UTF-8");
			long fileLength = operationLogFile.length();

			response.setContentType("application/octet-stream");

			// 如果客户端为IE
			// System.out.println(request.getHeader("User-Agent"));
			if (request.getHeader("User-Agent").indexOf("Trident") != -1) {
				operationLogName = java.net.URLEncoder.encode(operationLogName,
						"UTF-8");
			} else {
				operationLogName = new String(
						operationLogName.getBytes("UTF-8"), "iso-8859-1");
			}

			response.setHeader("Content-disposition", "attachment; filename="
					+ operationLogName);
			response.setHeader("Content-Length", String.valueOf(fileLength));

			is = new FileInputStream(operationLogFile);
			os = response.getOutputStream();

			byte[] b = new byte[1024];
			int len = 0;
			while ((len = is.read(b)) != -1) {
				os.write(b, 0, len);
			}
//			os.flush(); 
		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		}

	}
	
	
	
	
	@CrossOrigin
	@RequestMapping(value = "api/v1/downloadAllLog/download", method = RequestMethod.GET)
	public void downloadAllLog(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
		Map<String, String[]> reqParams = request.getParameterMap();
		String sessionText = "SMART_RESULT_LOG_" + formats.format(new Date());
		String scheduleId = reqParams.containsKey("scheduleId") ? reqParams.get("scheduleId")[0] : "";
		sessionText = sessionText.replaceAll(":", "-").replaceAll(" ", "_").replaceAll("-", "_");
		String rootPath = request.getSession().getServletContext().getRealPath("");
		String operationPath = rootPath + File.separator + "do.download";
		String operationLogName =  sessionText+ ".zip";
		
		Map<String, List<Map<String, Object>>> dhssMap = new HashMap<String, List<Map<String, Object>>>();
		List<Map<String, Object>> resultList = this.smartCheckService.getSmartCheckDetailResultPageList(scheduleId);
		for (Map<String, Object> map : resultList) {
			List<Map<String, Object>> list = dhssMap.get(map.get("dhss_name")) == null ? 
												new ArrayList<Map<String, Object>>() : dhssMap.get(map.get("dhss_name"));
			list.add(map);
			dhssMap.put(String.valueOf(map.get("dhss_name")), list);
		}
		File f = new File(operationPath);
		if(!f.exists()){
			f.mkdirs();
		}
		List<File> files = new ArrayList<File>();
		for (String key : dhssMap.keySet()) {
			File operationLogFile = new File(operationPath + File.separator + key + ".txt");

			if (operationLogFile.exists() && operationLogFile.isFile()) {
				operationLogFile.delete();
			}
			OutputStream writer =  new FileOutputStream(operationLogFile);
			for (Map<String, Object> listMap : dhssMap.get(key)) {
				
					StringBuilder string = new StringBuilder();
					string.append("ne：");
					string.append(listMap.get("NE_NAME"));
					string.append("  ");					 
					string.append("unit：");
					string.append(listMap.get("UNIT_NAME"));
					string.append("  ");
					string.append("checkItem：");
					string.append(listMap.get("CHECK_ITEM_NAME"));
					string.append("  ");
					string.append("dataTime：");
					String startTime = "";
					if(!org.springframework.util.StringUtils.isEmpty(listMap.get("START_TIME"))){
						try {
							startTime= format.format(format1.parse((String)listMap.get("START_TIME")));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				 
					string.append(startTime);

					writer.write(string.toString().getBytes());
					writer.write("\r\n".getBytes());
					writer.write("\r\n".getBytes());
				// 若检查结果成功，则读取检查日志
				// 若检查结果失败，则直接读取错误信息
				String resultLog = DhssProjectProperties.getBaseLogPath() + (String) listMap.get("FILE_PATH");
				// 0:false 1:true

				InputStream reader = null;
				try {
					reader = new FileInputStream(resultLog);

					byte[] b = new byte[1024];
					int len = 0;
					while ((len = reader.read(b)) != -1) {
						writer.write(b, 0, len);
					}
						writer.write("\r\n".getBytes());
						writer.write("----------------------------------------------------------------------------------"
								.getBytes());
						writer.write("\r\n".getBytes());
				} catch (Exception e) {
					writer.write(e.getMessage().getBytes());
						writer.write("\r\n".getBytes());
						writer.write("----------------------------------------------------------------------------------"
								.getBytes());
						writer.write("\r\n".getBytes());

				} finally {
					if (null != reader) {
						reader.close();
					}
				}

				writer.flush();
			}
			writer.close();
			files.add(operationLogFile);
		}
		
		
		File file = new File(operationPath + File.separator + operationLogName);
        if (!file.exists()){   
            file.createNewFile();   
        }
        response.reset();
        //response.getWriter()
        //创建文件输出流
        FileOutputStream fous = new FileOutputStream(file);   
        /**打包的方法我们会用到ZipOutputStream这样一个输出流,
         * 所以这里我们把输出流转换一下*/
        ZipOutputStream zipOut = new ZipOutputStream(fous);
        /**这个方法接受的就是一个所要打包文件的集合，
         * 还有一个ZipOutputStream*/
        smartCheckService.zipFile(files, zipOut);
        zipOut.close();
        fous.close();
        smartCheckService.downloadZip(file,response,request);
    }
	
	
	@RequestMapping("api/v1/execJob")
	public boolean execJob(@RequestParam(value="id",required=false)Long id){
		return smartCheckService.execJob(id);
	}
}

