package com.nokia.ices.app.dhss.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultElement;

public class LocalTest {
	private static String userBatchAbsPath = "E:/user_batch.xml";
	public static void main(String[] args) throws IOException, DocumentException {
		
//		Map<String,String> xmlDataMap = new HashMap<>();
//		String xml_466891000473911 = FileUtils.readFileToString(new File("E:/single-query/single-userdata-201706261017-886971595293.xml"));
//		String xmlTest = FileUtils.readFileToString(new File("E:/single-query/baoht_20170620143215_soapxml.xml"));
//		xmlDataMap.put("466891000473911", xml_466891000473911);
//		xmlDataMap.put("466891000473912", null);
//		xmlDataMap.put("466891000473913", null);
//		xmlDataMap.put("466891000473910", null);
//		xmlDataMap.put("466891000473909", null);
//		xmlDataMap.put("zzz", null);
//		xmlDataMap.put("460077280329824", xmlTest);
//		String[] numbers= new String[xmlDataMap.size()];
//		Set<String> numberSet = xmlDataMap.keySet();
//		Iterator<String> ii = numberSet.iterator();
//		
//			while(ii.hasNext()){
//				for (int i = 0; i < numberSet.size(); i++) {
//				numbers[i]=ii.next();
//				}
//			}
//		addBacthUserData(numbers,xmlDataMap);
		String oddXml = FileUtils.readFileToString(new File("E:/od.xml"));
		System.out.println(oddXml);
	}
	public static String formatXml(String unformatedXml) throws DocumentException, IOException {
		SAXReader reader = new SAXReader();
		StringReader in = new StringReader(unformatedXml);
		Document doc = reader.read(in);
		OutputFormat formater = OutputFormat.createPrettyPrint();
		formater.setEncoding("UTF-8");
		StringWriter out = new StringWriter();
		XMLWriter writer = new XMLWriter(out, formater);
		writer.write(doc);
		writer.close();
		String formatedXml = out.toString();
		return formatedXml;
	}
	public static Long addBacthUserData(String[] numbers,Map<String,String> xmlDataMap) {
		Map<String, Object> xmlMap = null;
		OutputStream out = null;
		List<String[]> vaList = new ArrayList<String[]>();
		List<String> titleNames = new ArrayList<String>();
		String phoneNumber = "";
		try {
			if ((null != numbers) && (numbers.length > 0)) {
				xmlMap = resolveUserBatchXml();
				int size = numbers.length;
				for (int i = 0; i < size; i++) {
					String[] values = new String[xmlMap.size()];
					String value = numbers[i];
					phoneNumber = phoneNumber + value + ",";

					if (StringUtils.isNotBlank(value)) {
						String formatXml = xmlDataMap.get(numbers[i]);
							Iterator<Entry<String, Object>> it = xmlMap.entrySet().iterator();
							int index = 0;
							while (it.hasNext()) {
								Entry<String, Object> entry = it.next();
								Object val_obj = entry.getValue();
								String titleName = (String) entry.getKey();
								if (i == 0) {
									titleNames.add(titleName);
								}
								if(formatXml!=null&&!formatXml.contains("error")){
									String title_val = resolveSoapXml(formatXml, val_obj, titleName);
									values[index] = title_val;
									index++;
								}else{
									values[0] = "'"+ value +"' can not be found.";
									index++;
								}

							}
							vaList.add(values);
					}
				}

				phoneNumber = phoneNumber.substring(0, phoneNumber.length() - 1);
				String userName = "multi-subscriber";

				String rootPath ="E:/batch-user/";

				File operationDir = new File(rootPath);
				if ((!operationDir.exists()) && (!operationDir.isDirectory())) {
					operationDir.mkdir();
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String fileName = userName + "_bacthUser_" + sdf.format(new Date()) + ".xlsx";
				out = new FileOutputStream(rootPath + fileName);
				ExportExcel<String[]> excel = new ExportExcel<String[]>();
				String[] headers = (String[]) titleNames.toArray(new String[titleNames.size()]);
				String[] headersConvert = new String[headers.length];
				Map<String,String> convertMap = titleNameConvertMap();
				for (int i =0;i<headers.length;i++) {
					headersConvert[i] = convertMap.get(headers[i]);
					if(headersConvert[i]==null){
						headersConvert[i] = headers[i];
					}
				}
				excel.exportExcel(userName + "_number", headersConvert, vaList, out, null);
				

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != out)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			out = null;
		}

		return Long.valueOf(0L);
	}
	@SuppressWarnings("unchecked")
	private static Map<String,String> titleNameConvertMap() throws FileNotFoundException, DocumentException{
		String filePath = userBatchAbsPath;
		FileInputStream fileIn = new FileInputStream(filePath);
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(fileIn);
		Element node = document.getRootElement();
		List<Element> elementAll = node.elements();
		Map<String,String> convertMap = new HashMap<>();
		for (Element element : elementAll) {
			List<Element> nodes = element.selectNodes("*");
			if(nodes.size()==0){ 
				//无子节点
				String standardName = element.attributeValue("zhName");
				if(StringUtils.isNotBlank(standardName)){
				    convertMap.put(element.getName(), standardName);
				}else{
					convertMap.put(element.getName(), element.getName());
				}
			}else{
				for (Element childEle : nodes) {
					if(!childEle.getPath().contains("include")){
						convertMap.put(element.getName()+"-"+childEle.getStringValue(), childEle.attributeValue("zhName"));
					}
				}
			}
		}
		System.out.println("convert map:"+convertMap);
		return convertMap;
	}
	@SuppressWarnings("unchecked")
	private static Map<String, Object> resolveUserBatchXml()
			throws FileNotFoundException, IOException, DocumentException {
		Map<String, Object> data_map = new LinkedHashMap<String, Object>();

		String filePath = userBatchAbsPath;
		FileInputStream fileIn = new FileInputStream(filePath);
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(fileIn);
		Element node = document.getRootElement();
		List<Element> elementAll = node.elements();
		for (Element e : elementAll) {
			String name = e.getName();
			String path = e.attributeValue("path");
			String[] atts;
			if (e.hasContent()) {
				atts = new String[4];
				List<Element> listElement = e.elements();
				for (Element element : listElement) {
					String property = element.attributeValue("property");
					String valueDes = element.attributeValue("valueDes");
					atts[0] = path;
					if (StringUtils.isNotBlank(valueDes)) {
						atts[3] = valueDes;
					}
					if (StringUtils.isNotBlank(property)) {
						atts[1] = property;
						atts[2] = element.getTextTrim();
					} else {
						name = e.getName() + "-" + element.getTextTrim();
						data_map.put(name, atts);
					}
				}
			} else {
				data_map.put(name, path);
			}
		}

		return data_map;
	}
	@SuppressWarnings("unchecked")
	private static String resolveSoapXml(String localPath, Object val_obj, String titleName)
			throws FileNotFoundException, IOException, DocumentException {
		//READ USER BATCH XML
		String userBatchAbsFile = userBatchAbsPath;
		FileInputStream userBatchAbsFileIn = new FileInputStream(userBatchAbsFile);
		SAXReader userBatchSaxReader = new SAXReader();
		Document userBatchDoc = userBatchSaxReader.read(userBatchAbsFileIn);
		//READ USER DATA XML
		InputStream fileIn = new ByteArrayInputStream(localPath.getBytes());
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(fileIn);
		String result = "";
		DefaultElement oj;
		if ((val_obj instanceof String)) {
			String _path = val_obj.toString();
			List<Element> allBatchNodes = userBatchDoc.getRootElement().elements();
			for (Element element : allBatchNodes) {
				String pathAttrInTemplate = element.attributeValue("path");
				if(pathAttrInTemplate.equals( _path)){
					String valueDes = element.attributeValue("valueDes");
					if(valueDes!=null&&valueDes.contains(":")){
						List<Element> listAll = document.selectNodes("/" + _path);
						for (Element e : listAll){
							result = e.getTextTrim();
							if(!valueDes.contains(",")){
								String key = valueDes.split(":")[0];
								String value = valueDes.split(":")[1];
								if(result.equals(key)){
									result = value;
								}
							}else{
								String[] rules = valueDes.split(",");
								for (String rule : rules) {
									String key = rule.split(":")[0];
									String value = rule.split(":")[1];
									if(result.equals(key)){
										result = value;
										break;
									}
								}
							}
						}
					}else{
						System.out.println(element.getName()+" need no convert");
						List<Element> listAll = document.selectNodes("/" + _path);
						for (Element e : listAll){
							result += e.getTextTrim()+",";
							
						}
					}
				}
			}



		} else {
			String[] arrData = (String[]) val_obj;
			String _path = arrData[0];
			String property = arrData[1];
			String include_val = arrData[2];
			String valuedes = arrData[3];
			List<Element> listAll = document.selectNodes("/" + _path);
			for (Element e : listAll) {
				String parentName = e.getName();
				if ((StringUtils.isNotBlank(include_val)) && (StringUtils.isNotBlank(property))) {
					List<DefaultElement> listDef = e.elements(property);
					if ((null != listDef) && (listDef.size() > 0)) {
						oj = listDef.get(0);
						String basiVal = oj.getTextTrim();
						if (!include_val.endsWith(basiVal))
							{continue;}else{
								if(titleName.contains("-isdnNumber")){
									result = e.elementText("isdnNumber");
									break;
								}else if(titleName.contains("-status")){
									if(valuedes!=null&&valuedes.contains(":")){
										result = e.elementText("status");
										if(valuedes.contains(",")){//multi-convert
											String[] convertRules = valuedes.split(",");
											for (String convertRule : convertRules) {
												String Key = convertRule.split(":")[0];
												String value = convertRule.split(":")[1];
												if(Key.equals(result)){
													result = value;
													break;
												}
											}
										}else{//single-convert
											String[] convertPieceArray = valuedes.split(":");
											if(result.equals(convertPieceArray[0])){
												result = convertPieceArray[1];
												break;
											}
											
										}
										
									}
								}

							}
					}
				} else {
					List<Element> listAlls = e.elements();
					for (Element ee : listAlls) {
						String name = ee.getName();
						if (titleName.equalsIgnoreCase(parentName + "-" + name)) {
							String val = ee.getTextTrim();
							if (StringUtils.isNotBlank(valuedes)) {
								String[] desVal = valuedes.split(",");
								for (String _val : desVal) {
									String key = _val.split(":")[0];
									String value = _val.split(":")[1];
									if (val.equals(key)) {
										val = value;
									}
								}
							}

							result = result + val + ",";
							break;
						}
					}
				}
			}
		}
		
		if (StringUtils.isNotBlank(result)&&result.contains(",")) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}
}
