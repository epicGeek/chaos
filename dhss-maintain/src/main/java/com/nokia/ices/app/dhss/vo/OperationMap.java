package com.nokia.ices.app.dhss.vo;

import java.util.List;
import java.util.Map;

import com.nokia.ices.app.dhss.domain.maintain.MaintainOperation;

public class OperationMap {
	
	private List<Map<String, Object>> list;
	
	private MaintainOperation operation;

	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}

	public MaintainOperation getOperation() {
		return operation;
	}

	public void setOperation(MaintainOperation operation) {
		this.operation = operation;
	}

}
