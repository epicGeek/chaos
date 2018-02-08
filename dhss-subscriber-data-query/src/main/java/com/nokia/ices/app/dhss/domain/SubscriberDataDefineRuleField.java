package com.nokia.ices.app.dhss.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubscriberDataDefineRuleField implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String fieldName;
	private String value;
	private String path;
	private String colspan;
	private String rowspan;

	public String getColspan() {
		return colspan;
	}

	public void setColspan(String colspan) {
		this.colspan = colspan;
	}

	public String getRowspan() {
		return rowspan;
	}

	public void setRowspan(String rowspan) {
		this.rowspan = rowspan;
	}

	private List<SubscriberDataDefineRuleField> subField;

	private Map<String, String> valueMapping;

	private Boolean isArray;
	

	private List<Map<String, String>> embedded = new ArrayList<Map<String, String>>();
	
	private String filter;

	private Boolean convert;

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}
	
	public Boolean getIsArray() {
		return isArray;
	}

	public void setIsArray(Boolean isArray) {
		this.isArray = isArray;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<SubscriberDataDefineRuleField> getSubField() {
		return subField;
	}

	public void setSubField(List<SubscriberDataDefineRuleField> subField) {
		this.subField = subField;
	}

	public Boolean getConvert() {
		return convert;
	}

	public void setConvert(Boolean convert) {
		this.convert = convert;
	}


	public Map<String, String> getValueMapping() {
		return valueMapping;
	}

	public void setValueMapping(Map<String, String> valueMapping) {
		this.valueMapping = valueMapping;
	}

	public List<Map<String, String>> getEmbedded() {
		return embedded;
	}

	public void setEmbedded(List<Map<String, String>> embedded) {
		this.embedded = embedded;
	}

	@Override
	public String toString() {
		return "SubscriberDataDefineRuleField [hashCode = "+hashCode()+"][fieldName=" + fieldName 
				+ ", path=" + path + ", value=" + value + ", subField=" + subField + ", valueMapping=" + valueMapping
				+ ", convert=" + convert + "]";
	}

}
