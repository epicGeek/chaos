
package com.nokia.ices.app.dhss.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.ListOrderedMap;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.ClassPathResource;

import com.nokia.ices.app.dhss.domain.SubscriberDataDefineRuleField;
import com.nokia.ices.app.dhss.domain.SubscriberDataDefineRuleTab;
@SuppressWarnings("unused")
public class SoapMessageHelper {
	// private static final Logger logger =
	// LoggerFactory.getLogger(SoapMessageHelper.class);
	// private static List<String[]> cachedFieldList = Collections
	// .synchronizedList(new ArrayList<String[]>());

	// private static List<SubscriberDataDefineRuleTab> template = Collections
	// .synchronizedList(new ArrayList<SubscriberDataDefineRuleTab>());
	// static {
	// template = initTemplateFromConfig();
	// }

	public static Map<String, Object> convertXMLToJsonFromClassPath(String filePath, String rootElementName) {

		ClassPathResource classPathResource = new ClassPathResource(filePath);
		try {
			String absPath = classPathResource.getFile().getAbsolutePath();
			return convertXMLToJson(absPath, rootElementName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new HashMap<String, Object>();

	}

	public static Map<String, Object> convertXMLToJson(String filePath, String rootElementName) {
		ListOrderedMap<String, Object> resultMap = new ListOrderedMap<String, Object>();
		//
		// resultMap.put("Auc", new ListOrderedMap<String, Object>());
		// resultMap.put("General", new ListOrderedMap<String, Object>());
		// resultMap.put("Operator Determined Barring", new
		// ListOrderedMap<String, Object>());
		// resultMap.put("Restrictions", new ListOrderedMap<String, Object>());
		// resultMap.put("Gprs", new ListOrderedMap<String, Object>());
		// resultMap.put("Eps", new ListOrderedMap<String, Object>());
		// resultMap.put("Supplementary Services", new ListOrderedMap<String,
		// Object>());
		// resultMap.put("Camel Service", new ListOrderedMap<String, Object>());
		// resultMap.put("Mobile Data", new ListOrderedMap<String, Object>());

		Document doc = readDocumentFromFile(filePath);

		Element rootElement = (Element) doc.selectSingleNode("//" + rootElementName);
		resultMap.put(rootElement.getName(), iterateElement(rootElement));
		return resultMap;
	}

	private static Document readDocumentFromFile(String filePath) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Can not Find file:" + filePath);
		}
		SAXReader saxReader = new SAXReader();
		Document doc = null;
		try {
			doc = saxReader.read(fis);
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new RuntimeException("Document file error:" + filePath);
		}
		return doc;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> iterateElement(Element element) {
		List<Element> jiedian = element.selectNodes("*");
		Element et = null;
		Map<String, Object> obj = new HashMap<String, Object>();
		List<Object> list = null;
		for (int i = 0; i < jiedian.size(); i++) {
			list = new LinkedList<Object>();
			et = (Element) jiedian.get(i);
			if (et.getTextTrim().equals("")) {
				if (et.selectNodes("*").size() == 0)
					continue;
				if (obj.containsKey(et.getName())) {
					list = (List<Object>) obj.get(et.getName());
				}
				list.add(iterateElement(et));
				obj.put(et.getName(), list);
			} else {
				if (obj.containsKey(et.getName())) {
					list = (List<Object>) obj.get(et.getName());
				}
				String path = et.getPath();
				path = path.substring(path.indexOf("/objects/"));
				// if (cachedFieldList.containsKey(path)) {
				// }

				list.add(et.getTextTrim());
				obj.put(et.getName(), list);
				// logger.info(path + " => " + et.getTextTrim());
			}
		}
		return obj;
	}

	@SuppressWarnings("unchecked")
	public static List<SubscriberDataDefineRuleTab> initTemplateFromConfig(String templateAbsPath) {

		Document doc = readDocumentFromFile(templateAbsPath);
		List<Node> tabNodeList = doc.selectNodes("/dhss:subscriber-data-parse-rule/dhss:tab[@tabName]");

		List<SubscriberDataDefineRuleTab> tabList = new ArrayList<SubscriberDataDefineRuleTab>();

		tabNodeList.forEach(tabNode -> { // TAB 页
			SubscriberDataDefineRuleTab newTab = new SubscriberDataDefineRuleTab();
			newTab.setTabName(tabNode.selectSingleNode("@tabName").getStringValue());
			newTab.setCol(tabNode.selectSingleNode("@col").getStringValue());

			List<SubscriberDataDefineRuleField> newTabChild = new ArrayList<SubscriberDataDefineRuleField>();
			List<Node> fieldNodeList = tabNode.selectNodes("dhss:field");
			fieldNodeList.forEach(fieldNode -> { // TAB 页下面的FIELD

				SubscriberDataDefineRuleField newField = new SubscriberDataDefineRuleField();
				newField.setFieldName(fieldNode.selectSingleNode("@zhName").getStringValue());
				handleValueConvertTemplate(fieldNode, newField);
				if (newField.getIsArray()) {
					// TODO HANDLE ARRAY DATA HERE
					newField.setPath(fieldNode.selectSingleNode("@parentName").getStringValue());
					// 如果有Path属性 表示这地方需要输出
					if (null != fieldNode.selectSingleNode("@path")) {
						newField.setPath(fieldNode.selectSingleNode("@path").getStringValue());
						// 数值转换
						handleValueConvertTemplate(fieldNode, newField);

					} else {
						// 有sublabel的情况 此时 无path convert
						List<Node> subLabelFieldNodeList = fieldNode.selectNodes("dhss:sub-field");
						List<SubscriberDataDefineRuleField> newSubFieldList = new ArrayList<SubscriberDataDefineRuleField>();

						subLabelFieldNodeList.forEach(subLabelNode -> {// TAB页下FIELD的SUBFIELD
							SubscriberDataDefineRuleField newSubField = new SubscriberDataDefineRuleField();
							if (null != subLabelNode.selectSingleNode("@path")) {
								newSubField.setFieldName(subLabelNode.selectSingleNode("@zhName").getStringValue());
								newSubField.setPath(subLabelNode.selectSingleNode("@path").getStringValue());
								handleValueConvertTemplate(subLabelNode, newSubField);
								newSubFieldList.add(newSubField);
							} else {
								List<Node> subSubLabelFieldNodeList = subLabelNode.selectNodes("dhss:sub-sub-field");
								subSubLabelFieldNodeList.forEach(subSubLabelFieldNode -> {
									SubscriberDataDefineRuleField newSubSubField = new SubscriberDataDefineRuleField();
									newSubSubField.setFieldName(subSubLabelFieldNode.selectSingleNode("@zhName").getStringValue());
									newSubSubField.setPath(subSubLabelFieldNode.selectSingleNode("@path").getStringValue());
									handleValueConvertTemplate(subSubLabelFieldNode, newSubSubField);
									newSubFieldList.add(newSubSubField);
								});
							}
						});

						newField.setSubField(newSubFieldList);

					}

				} else {
					// 如果有Path属性 表示这地方需要输出
					if (null != fieldNode.selectSingleNode("@path")) {
						newField.setPath(fieldNode.selectSingleNode("@path").getStringValue());
						// 数值转换
						handleValueConvertTemplate(fieldNode, newField);

					} else {
						// 有sublabel的情况 此时 无path convert
						List<Node> subLabelFieldNodeList = fieldNode.selectNodes("dhss:sub-field");
						List<SubscriberDataDefineRuleField> newSubFieldList = new ArrayList<SubscriberDataDefineRuleField>();

						subLabelFieldNodeList.forEach(subLabelNode -> {// TAB页下FIELD的SUBFIELD
							SubscriberDataDefineRuleField newSubField = new SubscriberDataDefineRuleField();
							if (null != subLabelNode.selectSingleNode("@path")) {
								newSubField.setFieldName(subLabelNode.selectSingleNode("@zhName").getStringValue());
								newSubField.setPath(subLabelNode.selectSingleNode("@path").getStringValue());
								handleValueConvertTemplate(subLabelNode, newSubField);
								newSubFieldList.add(newSubField);

							} else {
								List<Node> subSubLabelFieldNodeList = subLabelNode.selectNodes("dhss:sub-sub-field");
								// List<SubscriberDataDefineRuleField>
								// newSubSubFieldList = new
								// ArrayList<SubscriberDataDefineRuleField>();
								subSubLabelFieldNodeList.forEach(subSubLabelFieldNode -> {
									SubscriberDataDefineRuleField newSubSubField = new SubscriberDataDefineRuleField();
									newSubSubField.setFieldName(subSubLabelFieldNode.selectSingleNode("@zhName").getStringValue());
									newSubSubField.setPath(subSubLabelFieldNode.selectSingleNode("@path").getStringValue());
									handleValueConvertTemplate(subSubLabelFieldNode, newSubSubField);
									newSubFieldList.add(newSubSubField);
								});
							}
						});

						newField.setSubField(newSubFieldList);

					}
				}

				newTabChild.add(newField);
			});
			newTab.setChildren(newTabChild);
			tabList.add(newTab);
		});
		tabList.forEach(tab -> {
			// logger.info(tab.toString());
			List<SubscriberDataDefineRuleField> fieldSet = tab.getChildren();
			if (fieldSet != null)
				fieldSet.forEach(field -> {
					// logger.info(" " + field.toString());
					List<SubscriberDataDefineRuleField> subFieldList = (field.getSubField() == null
							? new ArrayList<SubscriberDataDefineRuleField>() : field.getSubField());
					subFieldList.forEach(subfield -> {
						// logger.info(" " + subfield.toString());
					});
				});
		});
		return tabList;

	}

	public static void main(String[] args) {
		String fileAbsPath = "/Users/quyidong/Documents/dhss-source/dhss-subscriber-data-query/src/main/resources/sample/"
				+ "tstar/single-userdata-201706221046-466891000001604.xml";
		String templatePath = "/Users/quyidong/Documents/dhss-source/dhss-subscriber-data-query/src/main/resources/"
				+ "sample/tstar/subscriber_data_value_mapping.xml";

		querySubscriberDataFromXMLPath(templatePath, fileAbsPath);

	}
	@SuppressWarnings("unchecked")
	public static List<SubscriberDataDefineRuleTab> querySubscriberDataFromXMLPath(String templateAbsPath,
			String fileAbsPath) {
		List<SubscriberDataDefineRuleTab> template = initTemplateFromConfig(templateAbsPath);

		Document doc = readDocumentFromFile(fileAbsPath);
		Node objectNode = doc.selectSingleNode("//objects");
		// Node objectNode2 = objectNode.selectSingleNode("*");
		template.forEach(tab -> {
			// logger.info(tab.getTabName());
			List<SubscriberDataDefineRuleField> fieldList = tab.getChildren();
			fieldList.forEach(fieldEntity -> {
				if (!fieldEntity.getIsArray()) {
					if (null != fieldEntity.getPath() && "" != fieldEntity.getPath()) {
						handleValueConvert(objectNode, fieldEntity);
					} else {
						if (fieldEntity.getSubField() != null && fieldEntity.getSubField().size() > 0) {
							List<SubscriberDataDefineRuleField> subFieldList = fieldEntity.getSubField();
							subFieldList.forEach(subField -> {
								handleValueConvert(objectNode, subField);
							});
						}
					}
				} else {
					// TODO 处理 isArray
					System.out.println(fieldEntity.getFieldName() + " is ARRAY" + fieldEntity.getPath());
					// Map<String,String> nsMap = new HashMap<>();
					// //如果XML含有命名空间（xmlns）,那么必须获得命名空间，否则dom4j不识别xpath
					// String defaultNamespace =
					// doc.getRootElement().getNamespaceURI();
					// nsMap.put("ns", defaultNamespace);
					// XPath xPath = doc.createXPath("/"+fieldEntity.getPath());
					// xPath.setNamespaceURIs(nsMap);
					String queryString = "";

					if (StringUtils.isNotBlank(fieldEntity.getFilter())) {
						queryString = "[" + fieldEntity.getFilter() + "]";
					}
					List<Node> nodes = doc.selectNodes("/" + fieldEntity.getPath() + queryString);
					// System.out.println(xPath.getText()+" has
					// array,size:"+nodes.size());
					List<Map<String,String>> result = new ArrayList<Map<String,String>>();
					for (Node node : nodes) {
						Map<String,String> rowData = new HashMap<String,String>();
                         List<Node> colNodeList = node.selectNodes("*");
                         System.out.println(node.asXML());
                         colNodeList.forEach(colNode -> {
                        	    rowData.put(colNode.getName(), colNode.getText());
                         });
//						try {
//							JAXBContext context = JAXBContext.newInstance(HashMap.class);
//							Unmarshaller unmarshaller = context.createUnmarshaller();
//                           arrayItem = (HashMap) unmarshaller.unmarshal(new StringReader(profix + outPutString));
//						} catch (JAXBException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
						result.add(rowData);
						System.out.println(rowData.toString());

					}
					fieldEntity.setEmbedded(result);
				}

			});
		});
//		template.forEach(tab -> {
//			List<SubscriberDataDefineRuleField> field = tab.getChildren();
//			field.forEach(a -> {
//				if (a.getIsArray())
//					System.out.println(a.getEmbedded().toString());
//			});
//		});
		return template;
	}

	private static void handleArrayConvert(Node objectNode, SubscriberDataDefineRuleField fieldNode) {
		String dataPath = fieldNode.getPath();
		Node node = objectNode.selectSingleNode("/" + dataPath);
		String value = "";
		if (node == null) {
			value = "";
		} else {
			value = node.getText();
			if (fieldNode.getConvert()) {
				value = fieldNode.getValueMapping().get(value);
			}
		}
		fieldNode.setValue(value);
	}

	private static void handleValueConvert(Node objectNode, SubscriberDataDefineRuleField fieldNode) {
		String dataPath = fieldNode.getPath();
		Node node = objectNode.selectSingleNode("/" + dataPath);
		String value = "";
		if (node == null) {
			value = "";
		} else {
			value = node.getText();
			if (fieldNode.getConvert()) {
				value = fieldNode.getValueMapping().get(value);
			}
		}
		fieldNode.setValue(value);
	}

	private static void handleValueConvertTemplate(Node fieldNode, SubscriberDataDefineRuleField newField) {
		Node nodeConvert = fieldNode.selectSingleNode("@convert");
		Node isArray = fieldNode.selectSingleNode("@isArray");
		Node filter = fieldNode.selectSingleNode("@filter");
		Node rowspanNode = fieldNode.selectSingleNode("@rowspan");
		Node colspanNode = fieldNode.selectSingleNode("@colspan");

		Map<String, String> valueMap = new HashMap<String, String>();

		@SuppressWarnings("unchecked")
		List<Node> valueMappingList = fieldNode.selectNodes("dhss:value-mapping");
		valueMappingList.forEach(valueMappingNode -> {
			String value = valueMappingNode.selectSingleNode("@value").getText();
			String displaytext = valueMappingNode.getText();
			valueMap.put(value, displaytext);
		});
		newField.setIsArray(isArray != null);
		newField.setConvert(nodeConvert != null);
		newField.setFilter(filter != null ? filter.getText() : "");
		newField.setFilter(filter != null ? filter.getText() : "");
		newField.setRowspan(rowspanNode !=null ? rowspanNode.getText():"1");
		newField.setColspan(colspanNode !=null ? colspanNode.getText():"1");
		newField.setValueMapping(valueMap);

	}
}