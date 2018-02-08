package com.nokia.ices.app.dhss.vo;

import java.util.List;

import com.nokia.ices.app.dhss.domain.number.NeGroup;
import com.nokia.ices.app.dhss.domain.number.NumberGroup;

public class NumberSectionMap {
	private List<NumberGroup> list;
	private Long selId;
	
	private List<NeGroup> neGroupResult;
	
	public List<NeGroup> getNeGroupResult() {
		return neGroupResult;
	}
	public void setNeGroupResult(List<NeGroup> neGroupResult) {
		this.neGroupResult = neGroupResult;
	}
	public List<NumberGroup> getList() {
		return list;
	}
	public void setList(List<NumberGroup> list) {
		this.list = list;
	}
	public Long getSelId() {
		return selId;
	}
	public void setSelId(Long selId) {
		this.selId = selId;
	}
	
	
}
