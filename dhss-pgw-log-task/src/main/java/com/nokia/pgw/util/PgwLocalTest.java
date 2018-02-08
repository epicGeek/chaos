package com.nokia.pgw.util;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

@SuppressWarnings("unused")
public class PgwLocalTest {
	public static void main(String[] args) throws IOException{
		
//		//TODO 解析addResponse不会得到用户号。用户号在addRequest里
//		String spml_addResponse = FileUtils.readFileToString(new File("E:/pgwdata/tw/解析规则/spml-addResponse.txt"));
//		//TODO changeId请求 用newId作为用户号码
		String spml_changeIdResponse = FileUtils.readFileToString(new File("E:/pgwdata/tw/解析规则/spml-changeIdResponse.txt"));
		String spml_changeIdResponse_MSISDN = FileUtils.readFileToString(new File("E:/pgwdata/tw/解析规则/spml-changeIdResponse-msisdn.txt"));
//		//TODO delete操作为deleteRequest
//		String spml_deleteResponse = FileUtils.readFileToString(new File("E:/pgwdata/tw/解析规则/spml-deleteResponse.txt"));
//		String spml_modifyResponse = FileUtils.readFileToString(new File("E:/pgwdata/tw/解析规则/spml-modifyResponse.txt"));
//		//TODO spml:searchResponse 没有<identifier>.可能是/auc/imsi 也可能是 /base/alias 的 value属性值.
//		//
//		String spml_searchResponse = FileUtils.readFileToString(new File("E:/pgwdata/tw/解析规则/spml-searchResponse.txt"));
//		String spml_searchResponse_imsi = FileUtils.readFileToString(new File("E:/pgwdata/tw/解析规则/spml-searchResponse_imsi.txt"));
//		//TODO urn:searchResponse 没有<identifier>.可能是/auc/imsi 也可能是 /base/alias 的 value属性值. 
//		String urn_searchResponse = FileUtils.readFileToString(new File("E:/pgwdata/tw/解析规则/urn-searchResponse.txt"));
//		// 所有的非modify操作，都要补齐
//		String spml_extendedRequest = FileUtils.readFileToString(new File("E:/pgwdata/tw/解析规则/spml-extendedRequest.txt"));
//		String spml_extendedResponse = FileUtils.readFileToString(new File("E:/pgwdata/tw/解析规则/spml-extendedResponse.txt"));
//		//System.out.println(analysisPgwLogLineXml(spml_extendedResponse));
//		Map<String,String> m = new HashMap<>();
//		m.put("1", "one");
//		String s = m.get("2");
//		//System.out.println(s);
//		String xml = " <spml:modifyResponse executionTime=\"45\" requestID=\"5cc418b8:15cca1f13d2:-5e3\" result=\"success\" xmlns:spml=\"urn:siemens:names:prov:gw:SPML:2:0\" xmlns:subscriber=\"urn:siemens:names:prov:gw:SUBSCRIBER:1:0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><version>SUBSCRIBER_v10</version><objectclass>Subscriber</objectclass><identifier alias=\"imsi\">466891002324303</identifier><modification name=\"hlr/ts11\" operation=\"addorset\" scope=\"uniqueTypeMapping\"><valueObject xmlns:ns2=\"urn:siemens:names:prov:gw:SUBSCRIBER:1:0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:TS11\"/></modification></spml:modifyResponse>";
//		String imsi = getImsi(xml);
//		System.out.println(imsi);
		System.out.println("id:"+getIdentifier(spml_changeIdResponse_MSISDN));
		System.out.println("imsi:"+getImsi(spml_changeIdResponse_MSISDN));
		System.out.println("msisdn:"+getMsisdn(spml_changeIdResponse_MSISDN));
		
	}
	private static String getImsi(String xml) {
		//IMSI解析方法：
		//1.<imsi>标签
		//2.<identifier>标签,值为imsi
		//3.changeId请求 newID标签
		
		String imsi = "0";
		try { //获取IMSI标签
			imsi = xml.substring(xml.indexOf("<imsi>"), xml.indexOf("</imsi>"));
			imsi = imsi.replace("<imsi>", "");
			if(imsi.length()>1){
				return imsi;
			}else{
				imsi = "0";
			}
		} catch (Exception e) {
			imsi = "0";
		}
		imsi = getIdentifier(xml);
		if(!imsi.equals("0")&&imsi.startsWith("466")){
			return imsi;
		}else{
			imsi = "0";
		}
		if(xml.contains("changeId")&&xml.contains("alias=\"imsi\"")&&imsi.equals("0")){
			try {//changeId时，获取用户号码
				imsi = xml.substring(xml.indexOf("<newId"),xml.indexOf("</newId>"));
				imsi = imsi.substring(imsi.indexOf("\">")).replace("\">", "");
				if(imsi.length()>1){
					return imsi;
				}
			} catch (Exception e) {
				imsi = "0";
			}	
		}
		return imsi;
	}
	
	private static String getIdentifier(String xml){
		//identifier 查询方法：
		//1.<identifier></identifier>标签
		//2.<identifier alias="imsi"></identifier>标签
		//3.<identifier alias="imsi" xsi:type="subscriber:SubscriberIdentifier">标签
		String identifier = "0";
		try {
			identifier = xml.substring(xml.indexOf("<identifier"),xml.indexOf("</identifier>"));
			identifier = identifier.substring(identifier.indexOf(">")).replace(">", "");
			if(identifier.length()>1){
				return identifier;
			}else{
				return "0";
			}
			
		} catch (Exception e) {
			return "0";
		}
		
	}
	
	private static String getMsisdn(String xml) {
		// msisdn解析方法：
		// 1.<alias name="msisdn" value="886986999999"/>
		// 2.<msisdn></msisdn>
		String msisdn = "0";
		try {
			msisdn = xml.substring(xml.indexOf("<msisdn>"), xml.indexOf("</msisdn>"));
			msisdn = msisdn.replace("<msisdn>", "");
		} catch (Exception e) {
			msisdn = "0";
		}
		if (msisdn.length() > 1) {
			return msisdn;
		}
		try {
			msisdn = xml.substring(xml.indexOf("<alias name=\"msisdn\" value=\""), xml.indexOf("\"/>"))
					.replace("<alias name=\"msisdn\" value=\"", "");
		} catch (Exception e) {
			msisdn = "0";
		}
		if (msisdn.length() > 1) {
			return msisdn;
		}
		// changeId操作时，alias = "msisdn"
		if (xml.contains("changeId") && xml.contains("alias=\"msisdn\"") && msisdn.equals("0")) {
			try {// changeId时，获取用户号码
				msisdn = xml.substring(xml.indexOf("<newId"), xml.indexOf("</newId>"));
				msisdn = msisdn.substring(msisdn.indexOf("\">")).replace("\">", "");
				if (msisdn.length() > 1) {
					return msisdn;
				}
			} catch (Exception e) {
				msisdn = "0";
			}

		}
		if (msisdn.length() > 1) {
			return msisdn;
		} else {
			return "0";
		}

	}
}
