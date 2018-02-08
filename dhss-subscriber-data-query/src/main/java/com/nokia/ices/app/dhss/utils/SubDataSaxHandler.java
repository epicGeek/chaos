package com.nokia.ices.app.dhss.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.nokia.ices.app.dhss.bean.BatchTemplate;

public class SubDataSaxHandler extends DefaultHandler{
	
	private String xPath = ""; //whole path
	private Integer finished = 0;
	private Long start ;
	private List<String[]> valueList = new ArrayList<>();
	private List<String> titleList ;
	private List<Map<String,String>> orginList = new ArrayList<>();
	private Set<BatchTemplate> btSet;
	private Map<String,BatchTemplate> templatesMap;
	private String[] singleXmlData;
	
	public SubDataSaxHandler(Set<BatchTemplate> btSet){
		this.btSet = btSet;
		this.templatesMap = getTemplatesMap(this.btSet);
		this.titleList = getTitleList(this.btSet);
		
	}
	private List<String> getTitleList(Set<BatchTemplate> btSet) {
		List<String> titles = new ArrayList<>();
		for (BatchTemplate bt : btSet) {
			titles.add(bt.getZhName());
		}
		return titles;
	}
	private Map<String, BatchTemplate> getTemplatesMap(Set<BatchTemplate> btSet) {
		Map<String, BatchTemplate> map = new LinkedHashMap<>();
		for (BatchTemplate batchTemplate : btSet) {
			String path = batchTemplate.getxPath();
			map.put(path, batchTemplate);
		}
		return map;
	}
	@Override
	public void startDocument() throws SAXException { 
		System.out.println(" --- Start to analyze subscriber data. ---");
		start = System.currentTimeMillis();
	}
	//开始解析每个元素时都会调用该方法
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		xPath += "/"+qName;
		if(qName.equals("objects")){
			this.singleXmlData = new String[titleList.size()]; //objects节点 清空数据集
			this.orginList = new ArrayList<>();
		}
	}
	//解析到每个元素的内容时会调用此方法
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException{
		String value = String.valueOf(ch).substring(start, start+length).trim();
		String configPath = xPath.replace("/soapenv:Envelope/soapenv:Body/spml:searchResponse", "");
		if(templatesMap.containsKey(configPath)){
			BatchTemplate bt = templatesMap.get(configPath);
			if(bt.getHasValueDesc()){
				value = bt.getValueDecsList().get(value);
			}
			String zhName = bt.getZhName();
			Map<String,String> dataMap = new HashMap<>();
			dataMap.put(zhName, value);
			this.orginList.add(dataMap);
		}
	}
	//每个元素结束的时候都会调用该方法
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException {
		if(qName.equals("objects")){
			finished ++;
			String[] dataArray = arrangeData();
			for (int i = 0 ; i < dataArray.length ; i++) {
				if(dataArray[i].endsWith(",")){
					dataArray[i] = StringUtils.trimTrailingCharacter(dataArray[i],',');
				}
			}
			this.valueList.add(dataArray);
		}
		String currentQname = xPath.substring(xPath.lastIndexOf('/'));
		if(currentQname.equals("/"+qName)){
			xPath = xPath.substring(0,xPath.lastIndexOf('/'));
		}
	}
	private String[] arrangeData() {
		for (Map<String,String> dataMap : this.orginList) {
			String key;
			for (Map.Entry<String,String> dataEntry : dataMap.entrySet()) {
				key = dataEntry.getKey();
				String value = dataEntry.getValue();
				int indexOfConfigure = this.titleList.indexOf(key);
				if(this.singleXmlData[indexOfConfigure] == null){
					this.singleXmlData[indexOfConfigure] = value;
				}else{
					this.singleXmlData[indexOfConfigure] += value + ",";
				}
				continue;
			}
		}
		return this.singleXmlData;
	}
	//结束解析文档，即解析根元素结束标签时调用该方法
	@Override
	public void endDocument() throws SAXException {
		System.out.println(" --- Subscriber data analyze completed. ---");
		Long end = System.currentTimeMillis();
		System.out.println("analyse "+ finished + " docs costs:"+(end-this.start)/1000.0+"s");
	}
	public List<String[]> getValueList() {
		return valueList;
	}
	public void setValueList(List<String[]> valueList) {
		this.valueList = valueList;
	}
	public List<String> getTitleList() {
		return titleList;
	}
}
