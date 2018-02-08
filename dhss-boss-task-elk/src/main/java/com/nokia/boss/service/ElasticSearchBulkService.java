package com.nokia.boss.service;

import java.util.List;
import java.util.Map;

import org.elasticsearch.action.index.IndexRequest;

import com.nokia.boss.bean.MessageItem;

public interface ElasticSearchBulkService {
	
	public void loadDataToEs(List<IndexRequest> requestList);

	public void updateErr(Map<String, MessageItem> errMap);

	public void deleteESData();
}
