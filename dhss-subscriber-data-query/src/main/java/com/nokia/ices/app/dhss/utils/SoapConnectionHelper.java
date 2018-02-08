
package com.nokia.ices.app.dhss.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;

import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nokia.ices.app.dhss.service.SubscriberQueryService;
import com.nokia.ices.app.dhss.service.impl.SubscriberQueryServiceImpl;

public class SoapConnectionHelper {
	private static final Logger logger = LoggerFactory.getLogger(SoapConnectionHelper.class);

	public static void main(String[] args) throws DocumentException, TransformerFactoryConfigurationError, TransformerException {
		try {
			Map<String, Object> m = getSubscriberDataRequestObject("msisdn", "8618730229070");
			String webServiceUrl = "http://10.223.181.214:8089/ProvisioningGateway/services/SPMLSubscriber10Service?wsdl";
			String responseInfo = getSearchResponseXml(m, webServiceUrl);
			logger.debug(SubscriberQueryServiceImpl.formatXml(responseInfo));
		} catch (UnsupportedOperationException | SOAPException | IOException e) {
			e.printStackTrace();
		}
	}

	public static Map<String, Object> getSubscriberDataRequestObject(String subscriberNumberType,
			String subscriberNumber) throws UnsupportedOperationException, SOAPException, IOException {
		SOAPConnectionFactory soapConnFactory = SOAPConnectionFactory.newInstance();
		SOAPConnection connection = soapConnFactory.createConnection();
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage message = messageFactory.createMessage();
		SOAPPart soapPart = message.getSOAPPart();
		SOAPEnvelope envelope = soapPart.getEnvelope();
		SOAPBody body = envelope.getBody();
		SOAPElement bodyElement = body
				.addChildElement(envelope.createName("searchRequest", "spml", "urn:siemens:names:prov:gw:SPML:2:0"));
		bodyElement.addChildElement("version").addTextNode("SUBSCRIBER_v10");
		SOAPElement baseElemnt = bodyElement.addChildElement(envelope.createName("base"));
		baseElemnt.setAttribute("xsi:type", "spml:SearchBase");
		baseElemnt.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		SOAPElement objectclass = baseElemnt.addChildElement("objectclass");
		objectclass.addTextNode("Subscriber");
		SOAPElement aliasElemnt = baseElemnt.addChildElement("alias");
		aliasElemnt.setAttribute("name", subscriberNumberType);
		aliasElemnt.setAttribute("value", subscriberNumber);
		message.saveChanges();
		message.writeTo(System.out);
		Map<String, Object> messageAndConn = new HashMap<>();
		messageAndConn.put("SOAPConnection", connection);// 连接对象
		messageAndConn.put("SOAPMessage", message);// 消息对象
		return messageAndConn;

	}

	public static String getSearchResponseXml(Map<String, Object> searchRequestXmlAndConnMap, String webServiceUrl){
		SOAPConnection connection = (SOAPConnection) searchRequestXmlAndConnMap.get("SOAPConnection");
		SOAPMessage message = (SOAPMessage) searchRequestXmlAndConnMap.get("SOAPMessage");
		try {
			SOAPMessage response = connection.call(message, webServiceUrl);
			if (response != null) {
				Source source = response.getSOAPPart().getContent();
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				ByteArrayOutputStream myOutStr = new ByteArrayOutputStream();
				StreamResult res = new StreamResult();
				res.setOutputStream(myOutStr);
				transformer.transform(source, res);
				String soapxml = myOutStr.toString("UTF-8");
				System.out.println(soapxml);
				return soapxml;
			} else {
				return "Error response : soap response is null.";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "Error response :"+e.getMessage();
		}

	}
}