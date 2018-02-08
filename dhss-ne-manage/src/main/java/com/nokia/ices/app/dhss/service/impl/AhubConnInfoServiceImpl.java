package com.nokia.ices.app.dhss.service.impl;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nokia.ices.app.dhss.config.ProjectProperties;
import com.nokia.ices.app.dhss.core.utils.UploadFileUtil;
import com.nokia.ices.app.dhss.domain.topology.AhubConnInfo;
import com.nokia.ices.app.dhss.repository.topology.AhubConnInfoRepository;
import com.nokia.ices.app.dhss.service.AhubConnInfoService;
@Service
public class AhubConnInfoServiceImpl implements AhubConnInfoService {
	private static final SimpleDateFormat sdfFileName = new SimpleDateFormat("yyyyMMddHHmm");
	// private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
	private static final String[] HEADERS = new String[]{"AHUB_NAME","IP_ADDRESS","SELF_PORT","TARGET_EQUIPMENT","TARGET_LAN","TARGET_MODE","TARGET_PORT","UP_LINK_IP_ADDRESS","VLAN_ID"};
	
	private static final String AHUB_TEMPLATE_FILENAME = "Ahub-info-template.xlsx";
	@Autowired
	private AhubConnInfoRepository ahubConnInfoRepository;

	
	@Override
	public void downloadTemplate(HttpServletRequest request, HttpServletResponse response) {

		String baseLogTempPath = ProjectProperties.getLogBasePath();
		String templateAbsPath = baseLogTempPath + "/" + AHUB_TEMPLATE_FILENAME;
		downloadUtils(new File(templateAbsPath), request,  response);
	}
	

	private void downloadUtils(File downloadFile, HttpServletRequest request, HttpServletResponse response) {
		InputStream inStream = null;
		try {
			inStream = new FileInputStream(downloadFile.getAbsolutePath());
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		response.reset();
		response.setContentType("bin");
		response.addHeader("Content-Disposition", "attachment; filename=\"" + downloadFile.getName() + "\"");
		byte[] b = new byte[100];
		int len;
		try {
			while ((len = inStream.read(b)) > 0)
				response.getOutputStream().write(b, 0, len);
			inStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void exportAhubData(HttpServletRequest request, HttpServletResponse response) {
		List<AhubConnInfo> allAhubInfo = ahubConnInfoRepository.findAll();
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Ahub-info");
		XSSFRow row = sheet.createRow(0);
		XSSFCellStyle style = workbook.createCellStyle();
		style.setFillForegroundColor(new XSSFColor(new Color(0xffffff00)));
		// style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
		// style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		// style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);

		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		for (int i = 0; i < HEADERS.length; i++) {//写title
			XSSFCell cell = row.createCell(i);
			XSSFRichTextString text = new XSSFRichTextString(HEADERS[i]);
			cell.setCellStyle(style);
			cell.setCellValue(text);
		}
		int index = 1;
		for (AhubConnInfo ahubConnInfo : allAhubInfo) {
			row = sheet.createRow(index);
			row.createCell(0).setCellValue(new XSSFRichTextString(ahubConnInfo.getAhubName()));
			row.createCell(1).setCellValue(new XSSFRichTextString(ahubConnInfo.getIpAddress()));
			row.createCell(2).setCellValue(new XSSFRichTextString(ahubConnInfo.getSelfPort()));
			row.createCell(3).setCellValue(new XSSFRichTextString(ahubConnInfo.getTargetEquipment()));
			row.createCell(4).setCellValue(new XSSFRichTextString(ahubConnInfo.getTargetLan()));
			row.createCell(5).setCellValue(new XSSFRichTextString(ahubConnInfo.getTargetMode()));
			row.createCell(6).setCellValue(new XSSFRichTextString(ahubConnInfo.getTargetPort()));
			row.createCell(7).setCellValue(new XSSFRichTextString(ahubConnInfo.getUpLinkIpAddress()));
			row.createCell(8).setCellValue(new XSSFRichTextString(ahubConnInfo.getVlanId()));
			index++;
		}
//		for (int i = 0; i < HEADERS.length; i++) {
//			sheet.autoSizeColumn(i);
//		}
		String fileName = "Ahub-info-" + sdfFileName.format(new Date()) + ".xlsx";
		File exportFile = new File(fileName);
		OutputStream out;
		try {
			out = new FileOutputStream(exportFile.getAbsolutePath());
			workbook.write(out);
			out.close();
			workbook.close();
			System.out.println("Ahub file exported as:");
			System.out.println(exportFile.getAbsolutePath());
			downloadUtils(exportFile,request, response );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			exportFile.delete();
		}

	}


	@Override
	public Map<String,String> importAhubInfoData(MultipartFile multiQueryTemplate) {
		Map<String,String> m = new HashMap<>();
		try {
			
			File uploadFile = UploadFileUtil.saveUploadFileToDest(multiQueryTemplate, "");
			XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(uploadFile));
			//上传数据的字段必须与要求对应
			boolean isUploadFileReasonable = isUploadFileHeaderReasonable(workbook);
			if(!isUploadFileReasonable){
				m.put("severity", "error");
				m.put("summary", "Wrong headers");
				m.put("detail", "The headers in the upload excel is not proper.It should be the same as the template or the exported data excel.");
				return m;
			}else{
				try {
					List<AhubConnInfo> importedAhubInfoList = tryToAnalysisAhubData(workbook);
					ahubConnInfoRepository.deleteAll();
					ahubConnInfoRepository.save(importedAhubInfoList);
					m.put("severity", "success");
					m.put("summary", "Import success");
					m.put("detail", "Upload data success");
					return m;
				} catch (Exception e) {
					e.printStackTrace();
					m.put("severity", "error");
					m.put("summary", e.getMessage());
					m.put("detail", "Upload data analysis error.Please contact DHSS team for support");
					return m;
				}
			}
		}catch(Exception e1 ){
			e1.printStackTrace();
			m.put("severity", "error");
			m.put("summary", "File type error.");
			m.put("detail", "Upload file is not proper.Please contact DHSS team personnel for more information");
			return m;
		}
		
	}


	private List<AhubConnInfo> tryToAnalysisAhubData(XSSFWorkbook workbook) {
		// 认为导入模板的HEADER字段值一致。
		XSSFSheet sheet = workbook.getSheetAt(0);
		Map<String,Row> rowMap = new HashMap<>();
		Row headerRow = sheet.getRow(0);
		int i = 0;
		for (Cell cell : headerRow) {
			String header = cell.getStringCellValue();
			Row row = sheet.getRow(i);
			i++;
			rowMap.put(header, row);
		}
		Map<String,Integer> rowSerieMap = new HashMap<>();
		int j = 0;
		for (Cell cell : headerRow) {
			String header = cell.getStringCellValue();
			rowSerieMap.put(header, j);
			j++;
		}
		List<AhubConnInfo> ahubConnInfoList = new ArrayList<>();
		Integer lastLine = sheet.getLastRowNum();
		for (int line = 1; line <= lastLine; line++) {
			Map<String,String> rowData = new LinkedHashMap<>();
			for (String header : HEADERS) {
				String value = sheet.getRow(line).getCell(rowSerieMap.get(header)).getStringCellValue();
				rowData.put(header, value);
			}
			AhubConnInfo thisLineAhubConnInfo = setAhubValue(rowData);
			ahubConnInfoList.add(thisLineAhubConnInfo);
		}
		
		return ahubConnInfoList;
	}


	private AhubConnInfo setAhubValue(Map<String, String> rowData) {
		//header:
		//{"AHUB_NAME","IP_ADDRESS","SELF_PORT","TARGET_EQUIPMENT","TARGET_LAN","TARGET_MODE","TARGET_PORT","UP_LINK_IP_ADDRESS","VLAN_ID"};
		AhubConnInfo a = new AhubConnInfo();
		a.setAhubName(rowData.get("AHUB_NAME")==null?"":rowData.get("AHUB_NAME"));
		a.setIpAddress(rowData.get("IP_ADDRESS")==null?"":rowData.get("IP_ADDRESS"));
		a.setSelfPort(rowData.get("SELF_PORT")==null?"":rowData.get("SELF_PORT"));
		a.setTargetEquipment(rowData.get("TARGET_EQUIPMENT")==null?"":rowData.get("TARGET_EQUIPMENT"));
		a.setTargetLan(rowData.get("TARGET_LAN")==null?"":rowData.get("TARGET_LAN"));
		a.setTargetMode(rowData.get("TARGET_MODE")==null?"":rowData.get("TARGET_MODE"));
		a.setTargetPort(rowData.get("TARGET_PORT")==null?"":rowData.get("TARGET_PORT"));
		a.setUpLinkIpAddress(rowData.get("UP_LINK_IP_ADDRESS")==null?"":rowData.get("UP_LINK_IP_ADDRESS"));
		a.setVlanId(rowData.get("VLAN_ID")==null?"":rowData.get("VLAN_ID"));
		return a;
	}


	private boolean isUploadFileHeaderReasonable(XSSFWorkbook workbook) {
		XSSFSheet sheet = workbook.getSheetAt(0);
		Row titleRow = sheet.getRow(0);
		Set<String> uploadHeaderSet = new LinkedHashSet<>();
		for(int i=0;i<HEADERS.length;i++){
			try {
				Cell headerCell = titleRow.getCell(i);
				String header = headerCell.getStringCellValue();
				if(StringUtils.isNotBlank(header)){
					uploadHeaderSet.add(header);
				}else{
					break;
				}
			} catch (Exception e) {
				return false;
			}

		}

		if(HEADERS.length!=uploadHeaderSet.size()){
			return false;
		}
		for (String exceptedHeader : HEADERS) {
			if(!uploadHeaderSet.contains(exceptedHeader)){
				return false;
			}
		}
		return true;
	}
	
}
