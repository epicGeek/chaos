package com.nokia.ices.app.dhss.service.impl;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.nokia.ices.app.dhss.config.KpiCustomSetting;
import com.nokia.ices.app.dhss.domain.kpi.KpiMonitorHistory;
import com.nokia.ices.app.dhss.repository.kpi.KpiMonitorHistoryRepository;
import com.nokia.ices.app.dhss.service.KpiMonitorHistoryService;
import com.nokia.ices.app.dhss.service.SecurityService;

@Service
public class KpiMonitorHistoryServiceImpl implements KpiMonitorHistoryService {
	
	@Autowired
	private KpiMonitorHistoryRepository kpiMonitorHistoryRepository;
	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	KpiCustomSetting kpiCustomSetting;
	@Autowired
	SecurityService securityService;
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
	private static SimpleDateFormat sdfStandard = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	public Page<KpiMonitorHistory> getKpiDataByCondition(Map<String,Specification<KpiMonitorHistory>> map ,Pageable page) {
		Specification<KpiMonitorHistory> speciFicationsAND = map.get("AND");
		Specification<KpiMonitorHistory> speciFicationsOR = map.get("OR");
		Page<KpiMonitorHistory> resultList = kpiMonitorHistoryRepository.findAll(Specifications.where(speciFicationsAND).and(speciFicationsOR), page);
		return resultList;
	}
	@Override
	public Page<KpiMonitorHistory> getExportData( Map<String,Specification<KpiMonitorHistory>> map ) {
		Integer limit = kpiCustomSetting.getExportRecordNumberLimit();
		Pageable pageable = new PageRequest(0,limit,new Sort(Direction.DESC,"periodStartTime"));
		Specification<KpiMonitorHistory> speciFicationsAND = map.get("AND");
		Specification<KpiMonitorHistory> speciFicationsOR = map.get("OR");
		Page<KpiMonitorHistory> resultList = kpiMonitorHistoryRepository.findAll(Specifications.where(speciFicationsAND).and(speciFicationsOR),pageable);
		return resultList;
	}
	@Override
	public Map<String, String> KpiNameAndCodeMap() {
		List<Map<String, Object>> resultList = new ArrayList<>();
		resultList = jdbcTemplate.queryForList("select kpi_name,kpi_code from kpi_config where 1=1 ");
		Map<String, String> kpiNameAndCodeMap = new HashMap<>();
		for (Map<String, Object> map : resultList) {
			String kpiName = map.get("kpi_name").toString();
			String kpiCode = map.get("kpi_code").toString();
			kpiNameAndCodeMap.put(kpiName, kpiCode);
		}
		return kpiNameAndCodeMap;
	}

	@Override
	public Map<String, Object> getKpiDropdownList() {
		String sql = "select kpi_category,kpi_name,kpi_code from kpi_config";
		List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql);
		List<Map<String, Object>> kpiList = new ArrayList<>();
		List<Map<String, Object>> kpiTypeList = new ArrayList<>();
		Set<String> kpiTypeSet = new HashSet<>();
		for (Map<String, Object> map : resultList) {
			Map<String, Object> tempMap = new HashMap<>();
			tempMap.put("label", map.get("kpi_name").toString());
			tempMap.put("value", map.get("kpi_code").toString());
			tempMap.put("kpiType", map.get("kpi_category").toString());
			kpiList.add(tempMap);
			if (kpiTypeSet.add(map.get("kpi_category").toString())) {
				tempMap = new HashMap<>();
				tempMap.put("label", map.get("kpi_category").toString());
				tempMap.put("value", map.get("kpi_category").toString());
				kpiTypeList.add(tempMap);
			}
		}
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("kpiList", kpiList);
		resultMap.put("kpiType", kpiTypeList);
		return resultMap;
	}



	@Override
	public Boolean exportData(Page<KpiMonitorHistory> data, HttpServletRequest request, HttpServletResponse response) {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			String[] headers = { "DHSS Name", "NE name", "Unit name", "Ne type", "Location", "KPI name", "KPI value","KPI unit"
					,"Period start time" };
			XSSFSheet sheet = workbook.createSheet("KPI data");
			XSSFRow row = sheet.createRow(0);
			XSSFCellStyle style = workbook.createCellStyle();
			style.setFillForegroundColor(new XSSFColor(new Color(0xffffff00)));
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style.setAlignment(HorizontalAlignment.CENTER);
			style.setVerticalAlignment(VerticalAlignment.CENTER);
			for (int i = 0; i < headers.length; i++) {
				XSSFCell cell = row.createCell(i);
				XSSFRichTextString text = new XSSFRichTextString(headers[i]);
				cell.setCellStyle(style);
				cell.setCellValue(text);
			}
			int index = 1;
			for (KpiMonitorHistory kpi : data) {
				row = sheet.createRow(index);
				row.createCell(0).setCellValue(new XSSFRichTextString(kpi.getDhssName()));
				row.createCell(1).setCellValue(new XSSFRichTextString(kpi.getNeName()));
				row.createCell(2).setCellValue(new XSSFRichTextString(kpi.getUnitName()));
				row.createCell(3).setCellValue(new XSSFRichTextString(kpi.getNeType()));
				row.createCell(4).setCellValue(new XSSFRichTextString(kpi.getNeSite()));
				row.createCell(5).setCellValue(new XSSFRichTextString(kpi.getKpiName()));
				row.createCell(6).setCellValue(new XSSFRichTextString(String.valueOf(kpi.getKpiOutputValue())));
				row.createCell(7).setCellValue(new XSSFRichTextString(kpi.getKpiUnit()));
				row.createCell(8).setCellValue(new XSSFRichTextString(sdfStandard.format(kpi.getPeriodStartTime())));
				index++;
			}
			for (int i = 0; i < headers.length; i++) {
				sheet.autoSizeColumn(i);
			}
			String fileName = "KPI-export-report-" + sdf.format(new Date()) + ".xls";
			File exportFile = new File(fileName);
			OutputStream out = new FileOutputStream(exportFile.getAbsolutePath());
			workbook.write(out);
			out.close();
			workbook.close();
			System.out.println("new kpi export file is at:");
			System.out.println(exportFile.getAbsolutePath());
			downloadFile(request, response ,exportFile,exportFile.getName());
			exportFile.delete();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public Boolean fakeDownload(HttpServletRequest request, HttpServletResponse response) throws Exception {
		File f = new File("E:/kpi_report/KPI-export-report-201707121621.xls");
		downloadFile(request, response ,f,f.getName());
		return true;
	}

	private void downloadFile(HttpServletRequest request,HttpServletResponse response,File operationLogFile,String operationLogName) throws  Exception {
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<String> getNeList(String token) {
		Map paramsMap = new HashMap<>();
		paramsMap.put("token", token);
		paramsMap.put("resourceFlag", "net");
		paramsMap.put("contentFlag", "1");
		paramsMap.put("assocResourceFlag", "");
		paramsMap.put("assocResourceAttr", "");
		paramsMap.put("assocResourceAttrValue", "");
		List<Map<String,String>> data = securityService.getResource(paramsMap,true);
		List<String> dataList = new ArrayList<>();
		for (Map<String,String> m : data) {
			dataList.add(m.get("ne_name"));
		}
		return dataList;
		
	}
}
