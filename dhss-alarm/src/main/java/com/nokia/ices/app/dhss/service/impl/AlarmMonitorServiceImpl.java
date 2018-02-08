package com.nokia.ices.app.dhss.service.impl;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.Predicate.BooleanOperator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nokia.ices.app.dhss.core.utils.UploadFileUtil;
import com.nokia.ices.app.dhss.domain.alarm.AlarmMonitor;
import com.nokia.ices.app.dhss.domain.alarm.AlarmReceiveHistory;
import com.nokia.ices.app.dhss.domain.alarm.AlarmReceiveRecord;
import com.nokia.ices.app.dhss.domain.alarm.AlarmRule;
import com.nokia.ices.app.dhss.domain.alarm.NotImportantAlarm;
import com.nokia.ices.app.dhss.domain.alarm.UserAlarmMonitor;
import com.nokia.ices.app.dhss.domain.equipment.EquipmentUnit;
import com.nokia.ices.app.dhss.jpa.DynamicSpecifications;
import com.nokia.ices.app.dhss.jpa.SearchFilter;
import com.nokia.ices.app.dhss.repository.alarm.AlarmMonitorRepository;
import com.nokia.ices.app.dhss.repository.alarm.AlarmReceiveHistoryRepository;
import com.nokia.ices.app.dhss.repository.alarm.AlarmReceiveRecordRepository;
import com.nokia.ices.app.dhss.repository.alarm.AlarmRuleRepository;
import com.nokia.ices.app.dhss.repository.alarm.NotImportantAlarmRepository;
import com.nokia.ices.app.dhss.repository.alarm.UserAlarmMonitorRepository;
import com.nokia.ices.app.dhss.repository.equipment.EquipmentUnitRepository;
import com.nokia.ices.app.dhss.service.AlarmMonitorService;
import com.nokia.ices.app.dhss.service.SecurityService;

@Service
public class AlarmMonitorServiceImpl implements AlarmMonitorService {
	private static final SimpleDateFormat sdfFileName = new SimpleDateFormat("yyyyMMddHHmm");
	private static final String FIELD_TERMINATOR = "~~~";
	private static final String LINE_TERMINATOR = ";;;";
	@Autowired
	private NotImportantAlarmRepository notImportantAlarmRepository;

	@Autowired
	private EquipmentUnitRepository equipmentUnitRepository;

	@Autowired
	private AlarmReceiveRecordRepository alarmReceiveRecordRepository;

	@Autowired
	private UserAlarmMonitorRepository userAlarmMonitorRepository;

	@Autowired
	private AlarmReceiveHistoryRepository alarmReceiveHistoryRepository;

	@Autowired
	private AlarmMonitorRepository alarmMonitorRepository;

	@Autowired
	private AlarmRuleRepository alarmRuleRepository;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	/**
	 * 取消收藏
	 */
	@Override
	public boolean cancelCollection(UserAlarmMonitor userAlarm, String token) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userName_EQ", this.securityService.getSystemUser(token));
		paramMap.put("cnum_EQ", userAlarm.getCnum());
		List<UserAlarmMonitor> userAlarmList = this.findUserAlarmMonitor(paramMap);
		userAlarmMonitorRepository.delete(userAlarmList);
		return true;
	}

	/**
	 * 加入收藏
	 */
	@Override
	public UserAlarmMonitor joinCollection(UserAlarmMonitor userAlarm, String token) {
		userAlarm.setCreateTime(new Date());
		userAlarm.setUserName(securityService.getSystemUser(token));
		return userAlarmMonitorRepository.save(userAlarm);
	}

	/**
	 * 查询网元
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, String>> findEquipmentNe(String token) {
		Map<String, Object> paramsMap = new HashMap<>();
		paramsMap.put("token", token);
		paramsMap.put("resourceFlag", "net");
		paramsMap.put("contentFlag", "1");
		paramsMap.put("assocResourceFlag", "");
		paramsMap.put("assocResourceAttr", "");
		paramsMap.put("assocResourceAttrValue", "");
		List<Map<String, String>> data = securityService.getResource(paramsMap, true);
		return data;
	}

	/**
	 * 查询单元
	 */
	@Override
	public List<EquipmentUnit> findEquipmentUnit(Map<String, Object> paramMap) {
		Map<String, SearchFilter> filter = SearchFilter.parse(paramMap);
		Specification<EquipmentUnit> spec = DynamicSpecifications.bySearchFilter(filter.values(), BooleanOperator.AND,
				EquipmentUnit.class);
		return equipmentUnitRepository.findAll(spec);
	}

	/**
	 * 查询活动告警
	 */
	@Override
	public List<AlarmReceiveRecord> findAlarmReceiveRecord(Map<String, Object> paramMap) {
		Map<String, SearchFilter> filter = SearchFilter.parse(paramMap);
		Specification<AlarmReceiveRecord> spec = DynamicSpecifications.bySearchFilter(filter.values(),
				BooleanOperator.AND, AlarmReceiveRecord.class);
		return alarmReceiveRecordRepository.findAll(spec);
	}

	/**
	 * 查询用户收藏的告警属性
	 */
	@Override
	public List<UserAlarmMonitor> findUserAlarmMonitor(Map<String, Object> paramMap) {
		Map<String, SearchFilter> filter = SearchFilter.parse(paramMap);
		Specification<UserAlarmMonitor> spec = DynamicSpecifications.bySearchFilter(filter.values(),
				BooleanOperator.AND, UserAlarmMonitor.class);
		return userAlarmMonitorRepository.findAll(spec, new Sort(Direction.DESC, "createTime"));
	}

	/**
	 * 查询dhss的treeNode数据
	 */
	@Override
	public void returnDhssList(List<Map<String, Object>> root, String token) {
		List<Map<String, String>> neList = this.findEquipmentNe(token);
		Set<String> set = new HashSet<String>();
		for (Map<String, String> ne : neList) {
			set.add(ne.get("dhss_name"));
		}

		// 添加未知的节点

		set = sortByValue(set);
		for (String key : set) {
			root.add(fullNode(key, key, false, false, token, key, false));
		}
		root.add(fullNode("unknown", "unknown", false, true, token, "unknown", false));
	}

	/**
	 * 查询网元的treeNode数据
	 */
	@Override
	public void returnNeList(List<Map<String, Object>> root, String dhssName, String token) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("dhssName_EQ", dhssName);
		List<Map<String, String>> neList = this.findEquipmentNe(token);
		for (Map<String, String> neModel : neList) {
			if (StringUtils.isNotBlank(dhssName) && dhssName.equals(neModel.get("dhss_name"))) {
				root.add(fullNode(neModel.get("ne_name"), neModel.get("ne_name"), true, false, token,
						neModel.get("ne_name") + "_ne", true));
			}

		}
	}

	/**
	 * 查询单元的treeNode数据
	 */
	@Override
	public void returnUnitList(List<Map<String, Object>> root, String neName, String token) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("neName_EQ", neName);
		List<EquipmentUnit> unitList = this.findEquipmentUnit(paramMap);
		for (EquipmentUnit unit : unitList) {
			root.add(fullNode(unit.getUnitName(), StringUtils.isNoneEmpty(unit.getUnitName()) ? unit.getUnitName() : "",
					true, true, token, unit.getUnitName(), false));
		}
	}

	/**
	 * 填充每个节点
	 * 
	 * @param label
	 * @param param
	 * @param cnumOrDhss
	 * @param leaf
	 * @param token
	 * @return
	 */
	public Map<String, Object> fullNode(String label, String param, boolean cnumOrDhss, boolean leaf, String token,
			String cnum, boolean isNe) {
		Map<String, Object> node = new HashMap<String, Object>();
		node.put("label", label);
		Integer[] countSizes = { 0, 0 };
		try {
			countSizes = this.countNum(param, cnumOrDhss, isNe);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		node.put("leaf", leaf);
		node.put("totalCount", countSizes[0]);
		node.put("isCollection", isCollection(token, param));
		node.put("dayCount", countSizes[1]);
		node.put("data", StringUtils.isNotBlank(cnum) ? cnum : "***********");
		return node;
	}

	/**
	 * 查看node是否加入收藏
	 * 
	 * @param token
	 * @return
	 */
	private String isCollection(String token, String cnum) {
		String userName = securityService.getSystemUser(token);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userName_EQ", userName);
		List<UserAlarmMonitor> userAlarm = this.findUserAlarmMonitor(paramMap);
		for (UserAlarmMonitor userAlarmMonitor : userAlarm) {
			if (StringUtils.isNoneEmpty(userAlarmMonitor.getCnum()) && userAlarmMonitor.getCnum().equals(cnum)) {
				return "fa fa-star";
			}
		}
		return "fa fa-star-o";
	}

	/**
	 * 统计每个节点的全部活动告警数和当天活动告警数
	 * 
	 * @param param
	 * @param cnumOrDhss
	 * @return
	 * @throws ParseException
	 */
	private Integer[] countNum(String param, boolean cnumOrDhss, boolean isNe) throws ParseException {
		List<AlarmReceiveRecord> record = this.findAlarmReceiveRecord(new HashMap<>());
		List<String> notAlarmNo = findNotAlarmNo();
		Integer[] countSizes = { 0, 0 };

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Long dayDate = df.parse(df.format(new Date())).getTime();
		List<String> unitNameList = new ArrayList<>();
		if (isNe) {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("neName_EQ", param);
			List<EquipmentUnit> unitList = this.findEquipmentUnit(paramMap);
			for (EquipmentUnit equipmentUnit : unitList) {
				unitNameList.add(equipmentUnit.getUnitName());
			}
		}
		for (AlarmReceiveRecord alarm : record) {
			// if(!notAlarmNo.contains(alarm.getAlarmNo())){
			Long time = df.parse(df.format(alarm.getReceiveStartTime())).getTime();
			int size = time.equals(dayDate) ? 1 : 0;
			if (param.equals("unknown")) {
				if (!StringUtils.isNotBlank(alarm.getDhssName())) {

					countSizes[0] += 1;
					if (!notAlarmNo.contains(alarm.getAlarmNo()))
						countSizes[1] += size;
				}
			} else {
				if (isNe) {
					if (unitNameList.contains(StringUtils.isNotEmpty(alarm.getNeName()) ? alarm.getNeName() : "$$")) {
						countSizes[0] += 1;
						if (!notAlarmNo.contains(alarm.getAlarmNo()))
							countSizes[1] += size;
					}
				} else {
					if (cnumOrDhss) {
						if (((alarm.getAlarmCell().indexOf(StringUtils.isNotEmpty(param) ? param : "$$") != -1)
								|| (StringUtils.isNotBlank(alarm.getNeName()) && alarm.getNeName().equals(param)))) {
							countSizes[0] += 1;
							if (!notAlarmNo.contains(alarm.getAlarmNo()))
								countSizes[1] += size;
						}
					} else {
						if (StringUtils.isNoneEmpty(alarm.getDhssName()) && alarm.getDhssName().equals(param)) {
							countSizes[0] += 1;
							if (!notAlarmNo.contains(alarm.getAlarmNo()))
								countSizes[1] += size;
						}
					}
				}
			}
			// }
		}
		return countSizes;
	}

	private List<String> findNotAlarmNo() {
		List<String> notAlarmNo = new ArrayList<String>();
		List<NotImportantAlarm> alarm = notImportantAlarmRepository.findAll();
		if (alarm.size() != 0) {
			// NotImportantAlarm notImportantAlarm = alarm.get(0);
			// return notImportantAlarm.getAlarmNoList();
			for (NotImportantAlarm notImportantAlarm : alarm) {
				notAlarmNo.add(notImportantAlarm.getAlarmNum());
			}
		}
		return notAlarmNo;
	}

	/**
	 * 排序
	 * 
	 * @param set
	 * @return
	 */
	public static Set<String> sortByValue(Set<String> set) {
		List<String> setList = new ArrayList<String>(set);
		Collections.sort(setList, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				// TODO Auto-generated method stub
				return o1.toString().compareTo(o2.toString());
			}

		});
		set = new LinkedHashSet<String>(setList);// 这里注意使用LinkedHashSet
		return set;
	}

	@Override
	public Page<AlarmMonitor> findAlarmMonitorCustom(Map<String,Specification<AlarmMonitor>> map , Pageable pageable) {
//		Map<String, SearchFilter> filter = SearchFilter.parse(paramMap);
//		Specification<AlarmMonitor> spec = DynamicSpecifications.bySearchFilter(filter.values(), BooleanOperator.AND,
//				AlarmMonitor.class);
//		return alarmMonitorRepository.findAll(spec, pageable);
		Specification<AlarmMonitor> speciFicationsAND = map.get("AND");
		Specification<AlarmMonitor> speciFicationsOR = map.get("OR");
		return alarmMonitorRepository.findAll(Specifications.where(speciFicationsAND).and(speciFicationsOR), pageable);
	}

	@Override
	public Page<AlarmReceiveHistory> findAlarmMonitorHistory(Map<String, Object> paramMap, Pageable pageable) {
		Map<String, SearchFilter> filter = SearchFilter.parse(paramMap);
		Specification<AlarmReceiveHistory> spec = DynamicSpecifications.bySearchFilter(filter.values(),
				BooleanOperator.AND, AlarmReceiveHistory.class);

		return alarmReceiveHistoryRepository.findAll(spec, pageable);
	}

	@Override
	public List<AlarmReceiveHistory> findAlarmMonitorHistory(Map<String, Object> paramMap) {
		Map<String, SearchFilter> filter = SearchFilter.parse(paramMap);
		Specification<AlarmReceiveHistory> spec = DynamicSpecifications.bySearchFilter(filter.values(),
				BooleanOperator.AND, AlarmReceiveHistory.class);

		return alarmReceiveHistoryRepository.findAll(spec);
	}

	@SuppressWarnings({ "resource", "deprecation" })
	@Override
	public void exportData(List<AlarmReceiveHistory> data, HttpServletResponse response) throws ClassNotFoundException {
		try {
			response.setHeader("content-disposition",
					"attachment;filename=" + URLEncoder.encode(
							"alarm_history_" + new SimpleDateFormat("yyyy_MM_dd_HH_mm").format(new Date()) + ".xls",
							"UTF-8"));
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("alarm");

			int index = 0;
			HSSFRow headerRow = sheet.createRow(index);
			Field[] headers = AlarmReceiveHistory.class.getDeclaredFields();
			for (int i = 0; i < headers.length; i++) {
				sheet.setColumnWidth(i, 5000);
				CellStyle style = workbook.createCellStyle();
				style.setFillForegroundColor(IndexedColors.AQUA.getIndex());
				style.setFillPattern(CellStyle.SOLID_FOREGROUND);
				style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

				style.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 下边框
				style.setBorderLeft(HSSFCellStyle.BORDER_THIN);// 左边框
				style.setBorderTop(HSSFCellStyle.BORDER_THIN);// 上边框
				style.setBorderRight(HSSFCellStyle.BORDER_THIN);// 右边框

				HSSFFont font = workbook.createFont();
				font.setFontName("仿宋_GB2312");
				font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 粗体显示
				style.setFont(font);// 选择需要用到的字体格式

				HSSFCell cell = headerRow.createCell(i);
				cell.setCellStyle(style);
				cell.setCellValue(new HSSFRichTextString(headers[i].getName()));
			}
			for (AlarmReceiveHistory alarm : data) {
				index++;
				HSSFRow row = sheet.createRow(index);
				for (int i = 0; i < headers.length; i++) {
					String fieldName = headers[i].getName();
					String methodName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
					Method method = alarm.getClass().getMethod("get" + methodName);
					Object value = method.invoke(alarm);
					row.createCell(i).setCellValue(String.valueOf(value));
				}
			}
			workbook.write(response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public Page<AlarmRule> findAlarmRule(Map<String, Object> paramMap, Pageable pageable) {
		Map<String, SearchFilter> filter = SearchFilter.parse(paramMap);
		Specification<AlarmRule> spec = DynamicSpecifications.bySearchFilter(filter.values(), BooleanOperator.AND,
				AlarmRule.class);
		return alarmRuleRepository.findAll(spec, pageable);
	}

	@Override
	public List<AlarmRule> findExportAlarmRule(Map<String, Object> paramMap) {
		Map<String, SearchFilter> filter = SearchFilter.parse(paramMap);
		Specification<AlarmRule> spec = DynamicSpecifications.bySearchFilter(filter.values(), BooleanOperator.AND,
				AlarmRule.class);
		return alarmRuleRepository.findAll(spec);
	}

	@Override
	public void exportAlarmRule(List<AlarmRule> exportData, HttpServletRequest request, HttpServletResponse response) {

		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			String[] headers = { "Alarm description", "Alarm meaning", "Alarm number", "Alarm text", "Alarm type",
					"Probable cause", "Release version" };
			XSSFSheet sheet = workbook.createSheet("Alarm rule");
			XSSFRow row = sheet.createRow(0);
			XSSFCellStyle style = workbook.createCellStyle();
			style.setFillForegroundColor(new XSSFColor(new Color(0xffffff00)));
//			style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
//			style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
//			style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);

			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style.setAlignment(HorizontalAlignment.CENTER);
			style.setVerticalAlignment(VerticalAlignment.CENTER);

			for (int i = 0; i < headers.length; i++) {// 写title
				XSSFCell cell = row.createCell(i);
				XSSFRichTextString text = new XSSFRichTextString(headers[i]);
				cell.setCellStyle(style);
				cell.setCellValue(text);
			}

			int index = 1;
			for (AlarmRule alarmRule : exportData) {
				row = sheet.createRow(index);
				row.createCell(0).setCellValue(alarmRule.getAlarmDesc());
				row.createCell(1).setCellValue(alarmRule.getAlarmMeaning());
				row.createCell(2).setCellValue(alarmRule.getAlarmNo());
				row.createCell(3).setCellValue(alarmRule.getAlarmText());
				row.createCell(4).setCellValue(alarmRule.getAlarmType());
				row.createCell(5).setCellValue(alarmRule.getProbableCause());
				row.createCell(6).setCellValue(alarmRule.getReleaseVersion());
				index++;
			}
			for (int i = 0; i < headers.length; i++) {
				sheet.autoSizeColumn(i);
			}
			String fileName = "Alarm-rule-" + sdfFileName.format(new Date()) + ".xlsx";
			File exportFile = new File(fileName);
			OutputStream out = new FileOutputStream(exportFile.getAbsolutePath());
			workbook.write(out);
			out.close();
			workbook.close();
			downloadFile(request, response, exportFile, exportFile.getName());
			exportFile.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void downloadFile(HttpServletRequest request, HttpServletResponse response, File operationLogFile,
			String operationLogName) throws Exception {
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
	public Map<String, String> handleWithUploadFile(MultipartFile multiQueryTemplate) {
		Map<String, String> m = new HashMap<>();
		if (multiQueryTemplate.getOriginalFilename().contains("hssfe")
				|| multiQueryTemplate.getOriginalFilename().contains("HSSFE")) {
			return importHSSFE(multiQueryTemplate);
		}
		if (multiQueryTemplate.getOriginalFilename().contains("NT_HLR_FE")) {
			return importNTHLR(multiQueryTemplate);
		}
		if (multiQueryTemplate.getOriginalFilename().contains("mapping")) {
			return importOneNDS(multiQueryTemplate);
		}
		m.put("severity", "error");
		m.put("summary", "File type confussion");
		m.put("detail",
				"Upload file type is ambiguous.The name of the file should contains 'hssfe'(for HSSFE) or 'NT_HLR_FE'(for NTHLR) or 'mapping'(for ONE-NDS)");
		return m;
	}

	@Override
	public Map<String, String> importHSSFE(MultipartFile multiQueryTemplate) {
		/**
		 * DB column -> Excel column 
		 * alarm_desc -> Instructions 
		 * alarm_meaning ->Meaning alarm_no -> 
		 * Alarm number alarm_text -> Alarm text 
		 * alarm_type -> Alarm type 
		 * probable_cause -> Probable cause 
		 * release_version ->Release
		 * alarm_level -> Severity information
		 * ne_type,from_file,from_row,is_active
		 */
		Map<String, String> m = new HashMap<>();
		
		try {
			File uploadFile = UploadFileUtil.saveUploadFileToDest(multiQueryTemplate, "");
			String fromFile = uploadFile.getAbsolutePath().replaceAll("\\\\", "/");
			String neType = "HSSFE";
			String unitType = "HSS-FE";
			// 需求给出的hssfe excel 为 xls
			HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(uploadFile));
			// 只有一页
			HSSFSheet sheet = workbook.getSheetAt(0);
			// 根据title名，确定列位置
			Row firstRow = sheet.getRow(0);
			Map<String, Integer> columnIndexMap = new HashMap<>();
			for (Cell cell : firstRow) {
				if (cell.getStringCellValue().equals("Instructions")) {
					columnIndexMap.put("Instructions", cell.getColumnIndex());
					continue;
				}
				if (cell.getStringCellValue().equals("Meaning")) {
					columnIndexMap.put("Meaning", cell.getColumnIndex());
					continue;
				}
				if (cell.getStringCellValue().equals("Alarm number")) {
					columnIndexMap.put("Alarm number", cell.getColumnIndex());
					continue;
				}
				if (cell.getStringCellValue().equals("Alarm text")) {
					columnIndexMap.put("Alarm text", cell.getColumnIndex());
					continue;
				}
				if (cell.getStringCellValue().equals("Alarm type")) {
					columnIndexMap.put("Alarm type", cell.getColumnIndex());
					continue;
				}
				if (cell.getStringCellValue().equals("Probable cause")) {
					columnIndexMap.put("Probable cause", cell.getColumnIndex());
					continue;
				}
				if (cell.getStringCellValue().equals("Release")) {
					columnIndexMap.put("Release", cell.getColumnIndex());
					continue;
				}
				if (cell.getStringCellValue().equals("Severity information")) {
					columnIndexMap.put("Severity information", cell.getColumnIndex());
					continue;
				}
				
			}
			// 开始遍历数据
			
			Long start = System.currentTimeMillis();
			StringBuilder hssfeSb = new StringBuilder();
			for (int row = 1; row <= sheet.getLastRowNum(); row++) {
				Row thisRow = sheet.getRow(row);
				// 根据告警号去update已有数据
				Cell alarmNoCell = thisRow.getCell(columnIndexMap.get("Alarm number"));
				alarmNoCell.setCellType(CellType.STRING);
				String alarmNo = alarmNoCell.getStringCellValue();
				hssfeSb.append(alarmNo+FIELD_TERMINATOR);
				if(!StringUtils.isNotBlank(alarmNo)){
					continue;
				}
				String instruction = thisRow.getCell(columnIndexMap.get("Instructions")).getStringCellValue();
				hssfeSb.append(instruction+FIELD_TERMINATOR);
				String meaning = thisRow.getCell(columnIndexMap.get("Meaning")).getStringCellValue();
				hssfeSb.append(meaning+FIELD_TERMINATOR);
				String alarmText = thisRow.getCell(columnIndexMap.get("Alarm text")).getStringCellValue();
				hssfeSb.append(alarmText+FIELD_TERMINATOR);
				String alarmType = thisRow.getCell(columnIndexMap.get("Alarm type")).getStringCellValue();
				hssfeSb.append(alarmType+FIELD_TERMINATOR);
				//String releaseVersion = thisRow.getCell(columnIndexMap.get("Release")).getStringCellValue();
				hssfeSb.append("15.5C"+FIELD_TERMINATOR);
				Cell probableCauseCell = thisRow.getCell(columnIndexMap.get("Probable cause"));
				probableCauseCell.setCellType(CellType.STRING);
				String probableCause = probableCauseCell.getStringCellValue();
				hssfeSb.append(probableCause+FIELD_TERMINATOR);
				String severityInformation = thisRow.getCell(columnIndexMap.get("Severity information")).getStringCellValue();
				hssfeSb.append(severityInformation+FIELD_TERMINATOR);
				hssfeSb.append(unitType+FIELD_TERMINATOR);
				hssfeSb.append("1"+FIELD_TERMINATOR);
				hssfeSb.append(neType+FIELD_TERMINATOR);
				hssfeSb.append(fromFile+FIELD_TERMINATOR);
				hssfeSb.append("From:" + row+LINE_TERMINATOR);
			}
			File loadFile = new File("hssfe.load");
			FileUtils.writeStringToFile(loadFile, hssfeSb.toString());
			hssfeSb = null;
			String deleteSQL = "delete from alarm_rule where ne_type = ?";
			jdbcTemplate.update(deleteSQL,neType);
			String absPath = loadFile.getAbsolutePath().replaceAll("\\\\","/");
			String loadSQL = 
					"LOAD DATA LOCAL INFILE '"+absPath+"' INTO TABLE alarm_rule FIELDS TERMINATED BY '"+FIELD_TERMINATOR+"' LINES TERMINATED BY '"+LINE_TERMINATOR+"'  (\n" +
							"	alarm_no,\n" +
							"	alarm_desc,\n" +
							"	alarm_meaning,\n" +
							"	alarm_text,\n" +
							"	alarm_type,\n" +
							"	release_version,\n" +
							"	probable_cause,\n" +
							"	alarm_level,\n" +
							"	unit_type,\n" +
							"	is_active,\n" +
							"	ne_type,\n" +
							"	from_file,\n" +
							"	from_row\n" +
							")";
			jdbcTemplate.execute(loadSQL);
			Long end = System.currentTimeMillis();
			Double use = (end - start) / 1000.0;
			System.out.println("use: " + use + "s");
			m.put("severity", "success");
			m.put("summary", "Import HSSFE alarm rule success");
			m.put("detail", "File has been imported successfully!");
			workbook.close();
			return m;
		} catch (Exception e) {
			e.printStackTrace();
			m.put("severity", "error");
			m.put("summary", "File error");
			m.put("detail", e.getMessage() + " Please contact DHSS team personnel for more support.");
			return m;
		}

	}

	@Override
	public Map<String, String> importNTHLR(MultipartFile multiQueryTemplate) {
		/**
		 * DB column -> Excel column 
		 * alarm_desc -> Instructions 
		 * alarm_meaning ->
		 * Meaning alarm_no -> Alarm number 
		 * alarm_text -> Alarm text 
		 * alarm_type -> Alarm type 
		 * probable_cause -> Probable cause 
		 * release_version -> Release 
		 * ne_type,from_file,from_row,is_active
		 */
		Map<String, String> m = new HashMap<>();
		try {
			File uploadFile = UploadFileUtil.saveUploadFileToDest(multiQueryTemplate, "");
			String fromFile = uploadFile.getAbsolutePath().replaceAll("\\\\", "/");
			String neType = "NTHLR";
			String unitType = "HLRFE";
			// 需求给出的hssfe excel 为 xls
			HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(uploadFile));
			// 只有一页
			HSSFSheet sheet = workbook.getSheet("Alarm List");
			// 根据title名，确定列位置
			Row firstRow = sheet.getRow(0);
			Map<String, Integer> columnIndexMap = new HashMap<>();
			for (Cell cell : firstRow) {
				if (cell.getStringCellValue().equals("Instructions")) {
					columnIndexMap.put("Instructions", cell.getColumnIndex());
					continue;
				}
				if (cell.getStringCellValue().equals("Meaning")) {
					columnIndexMap.put("Meaning", cell.getColumnIndex());
					continue;
				}
				if (cell.getStringCellValue().equals("Alarm number")) {
					columnIndexMap.put("Alarm number", cell.getColumnIndex());
					continue;
				}
				if (cell.getStringCellValue().equals("Alarm text")) {
					columnIndexMap.put("Alarm text", cell.getColumnIndex());
					continue;
				}
				if (cell.getStringCellValue().equals("Alarm type")) {
					columnIndexMap.put("Alarm type", cell.getColumnIndex());
					continue;
				}
				if (cell.getStringCellValue().equals("Probable cause")) {
					columnIndexMap.put("Probable cause", cell.getColumnIndex());
					continue;
				}
				if (cell.getStringCellValue().equals("Release")) {
					columnIndexMap.put("Release", cell.getColumnIndex());
					continue;
				}
				if (cell.getStringCellValue().equals("Severity information")) {
					columnIndexMap.put("Severity information", cell.getColumnIndex());
					continue;
				}
			}
			// 开始遍历数据
			StringBuilder nthlrSb = new StringBuilder();
			Long start = System.currentTimeMillis();
			for (int row = 1; row <= sheet.getLastRowNum(); row++) {
				Row thisRow = sheet.getRow(row);
				Cell alarmNoCell = thisRow.getCell(columnIndexMap.get("Alarm number"));
				alarmNoCell.setCellType(CellType.STRING);
				String alarmNo = alarmNoCell.getStringCellValue();
				
				if(!StringUtils.isNotBlank(alarmNo)){
					continue;
				}
				nthlrSb.append(alarmNo+FIELD_TERMINATOR);
				String instruction = thisRow.getCell(columnIndexMap.get("Instructions")).getStringCellValue();
				nthlrSb.append(instruction+FIELD_TERMINATOR);
				String meaning = thisRow.getCell(columnIndexMap.get("Meaning")).getStringCellValue();
				nthlrSb.append(meaning+FIELD_TERMINATOR);
				String alarmText = thisRow.getCell(columnIndexMap.get("Alarm text")).getStringCellValue();
				nthlrSb.append(alarmText+FIELD_TERMINATOR);
				String alarmType = thisRow.getCell(columnIndexMap.get("Alarm type")).getStringCellValue();
				nthlrSb.append(alarmType+FIELD_TERMINATOR);
				//String releaseVersion = thisRow.getCell(columnIndexMap.get("Release")).getStringCellValue();
				nthlrSb.append("15.5C"+FIELD_TERMINATOR);
				Cell probableCauseCell = thisRow.getCell(columnIndexMap.get("Probable cause"));
				probableCauseCell.setCellType(CellType.STRING);
				String probableCause = probableCauseCell.getStringCellValue();
				nthlrSb.append(probableCause+FIELD_TERMINATOR);
				String severityInformation = thisRow.getCell(columnIndexMap.get("Severity information")).getStringCellValue();
				nthlrSb.append(severityInformation+FIELD_TERMINATOR);
				nthlrSb.append(unitType+FIELD_TERMINATOR);
				nthlrSb.append("1"+FIELD_TERMINATOR);
				nthlrSb.append(neType+FIELD_TERMINATOR);
				nthlrSb.append(fromFile+FIELD_TERMINATOR);
				nthlrSb.append("From:" + row+LINE_TERMINATOR);
				
			}
			File loadFile = new File("nthlr.load");
			FileUtils.writeStringToFile(loadFile, nthlrSb.toString());
			nthlrSb = null;
			String absPath = loadFile.getAbsolutePath().replaceAll("\\\\", "/");
			//TODO DELETE&LOAD
			String loadSQL = "LOAD DATA LOCAL INFILE '"+absPath+"' INTO TABLE alarm_rule FIELDS TERMINATED BY '"+FIELD_TERMINATOR+"' LINES TERMINATED BY '"+LINE_TERMINATOR+"'  (\n" +
					"	alarm_no,\n" +
					"	alarm_desc,\n" +
					"	alarm_meaning,\n" +
					"	alarm_text,\n" +
					"	alarm_type,\n" +
					"	release_version,\n" +
					"	probable_cause,\n" +
					"	alarm_level,\n" +
					"	unit_type,\n" +
					"	is_active,\n" +
					"	ne_type,\n" +
					"	from_file,\n" +
					"	from_row\n" +
					")";
			String deleteSQL = "delete from alarm_rule where ne_type = ?";
			jdbcTemplate.update(deleteSQL,neType);
			jdbcTemplate.execute(loadSQL);
			Long end = System.currentTimeMillis();
			Double use = (end - start) / 1000.0;
			System.out.println("use: " + use + "s");
			m.put("severity", "success");
			m.put("summary", "Import NTHLR alarm rule success");
			m.put("detail", "File has been imported successfully!");
			workbook.close();
			return m;
		} catch (Exception e) {
			e.printStackTrace();
			m.put("severity", "error");
			m.put("summary", "File error");
			m.put("detail", e.getMessage() + " Please contact DHSS team personnel for more support.");
			return m;
		}
	}

	@Override
	public Map<String, String> importOneNDS(MultipartFile multiQueryTemplate) {
		/**
		 * DB column -> Excel column 
		 * alarm_desc -> Repair action 
		 * alarm_meaning -> Description - SS Long Text - GA 
		 * alarm_no -> Generic Agent Alarm
		 * number alarm_text -> Short Text - 
		 * GA alarm_type -> Alarm Type
		 * -(probable_cause -> Probable cause) -(release_version -> Release) +
		 * Status Service alarm number + Status Service Unique Identifier
		 * 
		 * + unit_type -> sheet name : ADM -> ADM BE-DSA -> BEDS INS -> ? PGW ->
		 * PGW PGW-DSA -> PGDS R-DSA -> RDS
		 * 
		 * ne_type,from_file,from_row,is_active
		 */
		Map<String, String> sheetNameMapper = new HashMap<>();
		sheetNameMapper.put("ADM", "ADM");
		sheetNameMapper.put("BEDS", "BE-DSA");
		sheetNameMapper.put("PGW", "PGW");
		sheetNameMapper.put("PGDS", "PGW-DSA");
		sheetNameMapper.put("RDS", "R-DSA");
		Map<String, String> m = new HashMap<>();
		try {
			File uploadFile = UploadFileUtil.saveUploadFileToDest(multiQueryTemplate, "");
			String fromFile = uploadFile.getAbsolutePath().replaceAll("\\\\","/");

			// 需求给出的ONE-NDS excel 为 xlsx
			XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(uploadFile));
			handleOneNdsSheets(sheetNameMapper, fromFile, workbook);
			m.put("severity", "success");
			m.put("summary", "Import ONE-NDS alarm rule success");
			m.put("detail", "File has been imported successfully!");
			workbook.close();
			return m;
		} catch (Exception e) {
			m.put("severity", "error");
			m.put("summary", "Import ONE-NDS alarm rule failed!");
			m.put("detail", "File imported failed!Please contact DHSS team for more support");
			e.printStackTrace();
			return m;
		}
	}

	private void handleOneNdsSheets(Map<String, String> sheetNameMapper, String fromFile, XSSFWorkbook workbook) throws IOException {
		XSSFSheet rdsSheet = workbook.getSheet("RDS");
		XSSFSheet pgdsSheet = workbook.getSheet("PGDS");
		XSSFSheet pgwSheet = workbook.getSheet("PGW");
		XSSFSheet bedsSheet = workbook.getSheet("BEDS");
		XSSFSheet admSheet = workbook.getSheet("ADM");
		if (rdsSheet != null) {
			oneNdsSheetHandler(fromFile, rdsSheet, sheetNameMapper);

		}
		if (pgdsSheet != null) {
			oneNdsSheetHandler(fromFile, pgdsSheet, sheetNameMapper);

		}
		if (pgwSheet != null) {
			oneNdsSheetHandler(fromFile, pgwSheet, sheetNameMapper);

		}
		if (bedsSheet != null) {
			oneNdsSheetHandler(fromFile, bedsSheet, sheetNameMapper);

		}
		if (admSheet != null) {

			oneNdsSheetHandler(fromFile, admSheet, sheetNameMapper);
		}
	}

	private void oneNdsSheetHandler(String fromFile, XSSFSheet rdsSheet, Map<String, String> sheetNameMapper) throws IOException {
		String neType = "ONE-NDS";
		String unitType = sheetNameMapper.get(rdsSheet.getSheetName());
		Row firstRow = rdsSheet.getRow(0);
		Map<String, Integer> columnIndexMap = new HashMap<>();
		for (Cell cell : firstRow) {
			if (cell.getStringCellValue().equals("Repair action")) {
				columnIndexMap.put("Repair action", cell.getColumnIndex());
				continue;
			}
			if (cell.getStringCellValue().equals("Alarm Name")) {
				columnIndexMap.put("Alarm Name", cell.getColumnIndex());
				continue;
			}
			if (cell.getStringCellValue().contains("Description - SS")) {
				columnIndexMap.put("Description - SS Long Text - GA", cell.getColumnIndex());
				continue;
			}
			if (cell.getStringCellValue().contains("Generic Agent Alarm number")) {
				columnIndexMap.put("Generic Agent Alarm number", cell.getColumnIndex());
				continue;
			}
			if (cell.getStringCellValue().contains("Short Text - GA")) {
				columnIndexMap.put("Short Text - GA", cell.getColumnIndex());
				continue;
			}
			if (cell.getStringCellValue().contains("Alarm Type")) {
				columnIndexMap.put("Alarm Type", cell.getColumnIndex());
				continue;
			}
			if (cell.getStringCellValue().contains("Release")) {
				columnIndexMap.put("Release", cell.getColumnIndex());
				continue;
			}
			if (cell.getStringCellValue().contains("Probable cause")) {
				columnIndexMap.put("Probable cause", cell.getColumnIndex());
				continue;
			}
			if (cell.getStringCellValue().contains("Status Service alarm number")) {
				columnIndexMap.put("Status Service alarm number", cell.getColumnIndex());
				continue;
			}
			if (cell.getStringCellValue().contains("Status Service Unique Identifier")) {
				columnIndexMap.put("Status Service Unique Identifier", cell.getColumnIndex());
				continue;
			}
			if (cell.getStringCellValue().contains("Predefined Severity")) {
				columnIndexMap.put("Predefined Severity", cell.getColumnIndex());
				continue;
			}
			
		}
		// 开始遍历数据
		StringBuilder oneNdsSb = new StringBuilder();
		for (int row = 1; row < rdsSheet.getLastRowNum(); row++) {
			Row thisRow = rdsSheet.getRow(row);
			// 根据告警号去update已有数据
			Cell alarmNoCell = thisRow.getCell(columnIndexMap.get("Generic Agent Alarm number"));
			alarmNoCell.setCellType(CellType.STRING);
			String alarmNo = alarmNoCell.getStringCellValue();
			if(!StringUtils.isNotBlank(alarmNo)){
				continue;
			}
			oneNdsSb.append(alarmNo+FIELD_TERMINATOR);
			String alarmName = thisRow.getCell(columnIndexMap.get("Alarm Name")).getStringCellValue();
			oneNdsSb.append(alarmName+FIELD_TERMINATOR);
			String instruction = thisRow.getCell(columnIndexMap.get("Repair action")).getStringCellValue();
			oneNdsSb.append(instruction+FIELD_TERMINATOR);
			String meaning = thisRow.getCell(columnIndexMap.get("Description - SS Long Text - GA"))
					.getStringCellValue();
			oneNdsSb.append(meaning+FIELD_TERMINATOR);
			String alarmText = thisRow.getCell(columnIndexMap.get("Short Text - GA")).getStringCellValue();
			oneNdsSb.append(alarmText+FIELD_TERMINATOR);
			String alarmType = thisRow.getCell(columnIndexMap.get("Alarm Type")).getStringCellValue();
			oneNdsSb.append(alarmType+FIELD_TERMINATOR);
			Cell statusServiceAlarmNumberCell = thisRow.getCell(columnIndexMap.get("Status Service alarm number"));
			statusServiceAlarmNumberCell.setCellType(CellType.STRING);
			String statusServiceAlarmNumber = statusServiceAlarmNumberCell.getStringCellValue();
			oneNdsSb.append(statusServiceAlarmNumber+FIELD_TERMINATOR);
			String statusServiceUniqueIdentifier = thisRow
					.getCell(columnIndexMap.get("Status Service Unique Identifier")).getStringCellValue();
			String predefinedSeverity = thisRow
					.getCell(columnIndexMap.get("Predefined Severity")).getStringCellValue();
			oneNdsSb.append(predefinedSeverity+FIELD_TERMINATOR);
			oneNdsSb.append(statusServiceUniqueIdentifier+FIELD_TERMINATOR);
			oneNdsSb.append(unitType+FIELD_TERMINATOR);
			oneNdsSb.append("1"+FIELD_TERMINATOR);
			oneNdsSb.append(neType+FIELD_TERMINATOR);
			oneNdsSb.append(fromFile+FIELD_TERMINATOR);
			oneNdsSb.append("From:" + row+FIELD_TERMINATOR);
			oneNdsSb.append("15.5C"+LINE_TERMINATOR);
		}
		String deleteSQL = "delete from alarm_rule where unit_type = ?";
		jdbcTemplate.update(deleteSQL,unitType);
		File loadFile = new File("onends.load");
		FileUtils.writeStringToFile(loadFile, oneNdsSb.toString());
		oneNdsSb = null;
		String absPath = loadFile.getAbsolutePath().replaceAll("\\\\", "/");
		//TODO DELETE&LOAD
		String loadSQL = "LOAD DATA LOCAL INFILE '"+absPath+"' INTO TABLE alarm_rule FIELDS TERMINATED BY '"+FIELD_TERMINATOR+"' LINES TERMINATED BY '"+LINE_TERMINATOR+"'  (\n" +
				"	alarm_no,\n" +
				"	alarm_name,\n" +
				"	alarm_desc,\n" +
				"	alarm_meaning,\n" +
				"	alarm_text,\n" +
				"	alarm_type,\n" +
				"	status_service_alarm_number,\n" +
				"	alarm_level,\n" +
				"	status_service_unique_identifier,\n" +
				"	unit_type,\n" +
				"	is_active,\n" +
				"	ne_type,\n" +
				"	from_file,\n" +
				"	from_row,\n" +
				"	release_version\n" +
				")";
		jdbcTemplate.execute(loadSQL);
	}

	@Override
	public AlarmReceiveHistory cancelAlarm(AlarmReceiveRecord record) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		AlarmReceiveHistory history = new AlarmReceiveHistory();
		history.setNotifyId(record.getNotifyId());
		history.setAlarmLevel(record.getAlarmLevel());
		history.setAlarmNo(record.getAlarmNo());
		history.setAlarmCell(record.getAlarmCell());
		history.setStartTime(record.getStartTime());
		history.setCancelTime(record.getCancelTime());
		history.setNeName(record.getNeName());
		history.setNeCode(record.getNeCode());
		history.setReceiveStartTime(record.getReceiveStartTime());
		try {
			history.setReceiveCancelTime(sdf.parse(record.getCancelTime()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		history.setUserInfo(record.getUserInfo());
		history.setSupplInfo(record.getSupplInfo());
		history.setDiagInfo(record.getDiagInfo());
		history.setObjInfo(record.getObjInfo());
		history.setAlarmType(record.getAlarmType());
		history.setDhssName(record.getDhssName());
		history.setAlarmDesc(record.getAlarmDesc());
		history.setAlarmString(record.getAlarmText());
		history.setUnitType(record.getUnitType());
		alarmReceiveRecordRepository.delete(record);
		history = alarmReceiveHistoryRepository.save(history);
		return history;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<String> getUnitList(String token) {
		Map paramsMap = new HashMap<>();
		paramsMap.put("token", token);
		paramsMap.put("resourceFlag", "net");
		paramsMap.put("contentFlag", "1");
		paramsMap.put("assocResourceFlag", "");
		paramsMap.put("assocResourceAttr", "");
		paramsMap.put("assocResourceAttrValue", "");
		List<String> neList = new ArrayList<>();
		List<Map<String,String>> data = securityService.getResource(paramsMap,true);
		for (Map<String,String> m : data) {
			neList.add(m.get("ne_name"));
		}
		Map<String,Object> m = new HashMap<>();
		m.put("m", neList);
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		String sql = "select unit_name from equipment_unit where ne_name in (:m)";
		List<Map<String,Object>> l = namedParameterJdbcTemplate.queryForList(sql,m);
		List<String> unitList = new ArrayList<>();
		for (Map<String,Object> resultMap : l) {
			unitList.add(resultMap.get("unit_name")!=null?resultMap.get("unit_name").toString():"");
		}
		return unitList;
		
	
	}

}