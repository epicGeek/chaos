package com.nokia.boss.service.impl;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.threadpool.ThreadPool;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.nokia.boss.bean.MessageItem;
import com.nokia.boss.service.ElasticSearchBulkService;
import com.nokia.boss.settings.CustomSettings;
import com.nokia.boss.util.FileProcUtils;

@Service
public class ElasticeSearchBulkServiceImpl implements ElasticSearchBulkService {
	private RestHighLevelClient client;
	private Logger logger = LogManager.getLogger(ElasticeSearchBulkServiceImpl.class);
	@Autowired
	private CustomSettings customSettings;
	private final RestTemplate restTemplate;

	public ElasticeSearchBulkServiceImpl(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	@Override
	public void loadDataToEs(List<IndexRequest> requestList) {
		BulkProcessor bulkProcessor = null;
		try {
			client = initESclient();
			logger.info("Use bulk request to load data:");
			bulkProcessor = initES(client);
			Long startBulk = System.currentTimeMillis();
			for (IndexRequest indexRequest : requestList) {
				bulkProcessor.add(indexRequest);
			}
			Long endBulk = System.currentTimeMillis();
			logger.info("Bulk soap data success,use{} ms", endBulk - startBulk);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bulkProcessor != null) {
				bulkProcessor.close();
			}
		}

	}

	private RestHighLevelClient initESclient() {
		if (client == null) {
			String elasticSearchHost = customSettings.getElSearchConfig().getElasticSearchHost();
			int elasticSearchPort = customSettings.getElSearchConfig().getElasticSearchPort();
			RestClient lowLevelRestClient = RestClient
					.builder(new HttpHost(elasticSearchHost, elasticSearchPort, "http")).build();
			client = new RestHighLevelClient(lowLevelRestClient);

		}
		return client;
	}

	private BulkProcessor initES(RestHighLevelClient client) {

		Settings settings = Settings.EMPTY;
		ThreadPool threadPool = new ThreadPool(settings); // 构建新的线程池
		BulkProcessor.Listener listener = new BulkProcessor.Listener() {
			// 构建bulk listener

			@Override
			public void beforeBulk(long executionId, BulkRequest request) {
				// 重写beforeBulk,在每次bulk request发出前执行,在这个方法里面可以知道在本次批量操作中有多少操作数
				int numberOfActions = request.numberOfActions();
				logger.debug("Executing bulk [{}] with {} requests", executionId, numberOfActions);
			}

			@Override
			public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
				// 重写afterBulk方法，每次批量请求结束后执行，可以在这里知道是否有错误发生。
				if (response.hasFailures()) {
					logger.warn("Bulk [{}] executed with failures", executionId);
				} else {
					logger.debug("Bulk [{}] completed in {} milliseconds", executionId, response.getTook().getMillis());
				}
			}

			@Override
			public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
				// 重写方法，如果发生错误就会调用。
				logger.error("Failed to execute bulk", failure);
			}

		};

		BulkProcessor.Builder builder = new BulkProcessor.Builder(client::bulkAsync, listener, threadPool);// 使用builder做批量操作的控制
		BulkProcessor bulkProcessor = builder.build();
		// 在这里调用build()方法构造bulkProcessor,在底层实际上是用了bulk的异步操作
		builder.setBulkActions(2000); // 执行多少次动作后刷新bulk.默认1000，-1禁用
		builder.setBulkSize(new ByteSizeValue(5L, ByteSizeUnit.MB));// 执行的动作大小超过多少时，刷新bulk。默认5M，-1禁用
		builder.setConcurrentRequests(10000);// 最多允许多少请求同时执行。默认是1，0是只允许一个。
		builder.setFlushInterval(TimeValue.timeValueSeconds(5L));// 设置刷新bulk的时间间隔。默认是不刷新的。
		builder.setBackoffPolicy(BackoffPolicy.constantBackoff(TimeValue.timeValueSeconds(1L), 3)); // 设置补偿机制参数。由于资源限制（比如线程池满），批量操作可能会失败，在这定义批量操作的重试次数。
		logger.info("bulk processor build complete!");
		return bulkProcessor;
	}

	@Override
	public void updateErr(Map<String, MessageItem> errMap) {
		client = initESclient();
		BulkProcessor bulkProcessor = initES(client);
		String type = customSettings.getElSearchConfig().getSoapType();
		// 先根据taskId查询，然后update
		String index = "";
		for (Map.Entry<String, MessageItem> entry : errMap.entrySet()) {
			try {
				String key = entry.getKey();
				MessageItem mi = errMap.get(key);
				String day = mi.getResponse_time().split(" ")[0];
				// 在今天和昨天的数据中查询(如果是第一次运行这里会报找不到昨天的索引，商定暂时先不查询昨天的数据)
				index = customSettings.getElSearchConfig().getMockIndex() + "-" + day;
				// String otherIndex =
				// customSetting.getMockIndex()+"-"+DateUtils.getOtherDay(day,1);
				SearchRequest searchRequest = new SearchRequest();
				searchRequest.indices(index);
				searchRequest.types(type);
				SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
				TermQueryBuilder tqb = new TermQueryBuilder("task_id.keyword", key);
				searchSourceBuilder.query(tqb);
				searchRequest.source(searchSourceBuilder);
				SearchResponse sr = client.search(searchRequest);
				if (sr.getHits() != null) {
					SearchHit[] searchHits = sr.getHits().getHits();
					for (SearchHit searchHit : searchHits) {
						String id = searchHit.getId();// 得到当前数据的ID，更新
						UpdateRequest uRequest = new UpdateRequest();
						uRequest.index(index);
						uRequest.type(type);
						uRequest.id(id);
						uRequest.doc(jsonBuilder().startObject().field("error_code", mi.getError_code())
								.field("error_message", mi.getError_message()).field("error_log", mi.getError_log())
								.field("response_status", mi.getResponse_status()).endObject());
						bulkProcessor.add(uRequest);
					}
				}

			} catch (Exception e) {
				logger.error("update elasticeSearch data,index:{}, error:{}", index, e.getMessage());
				continue;
			}

		}
		bulkProcessor.close();

	}

	@Override
	public void deleteESData() {
		Integer saveDay = customSettings.getDefaultConfig().getSaveDays();
		String elasticSearchHost = customSettings.getElSearchConfig().getElasticSearchHost();
		int elasticSearchPort = customSettings.getElSearchConfig().getElasticSearchPort();
		String index = customSettings.getElSearchConfig().getMockIndex();
		DateTime deadLineTime = new DateTime().minusDays(saveDay);
		String delIndex = index + "-" + deadLineTime.toString("yyyy-MM-dd");
		logger.info("Delete index :{}", delIndex);
		File deleteScript;
		try {
			deleteScript = FileProcUtils
					.createShell("curl -XDELETE '" + elasticSearchHost + ":" + elasticSearchPort + "/" + index + "'");
			Process rsyncProcess = Runtime.getRuntime().exec("sh" + " " + deleteScript.getAbsolutePath());
			rsyncProcess.waitFor();
			InputStreamReader in = new InputStreamReader(rsyncProcess.getErrorStream());
			BufferedReader br = new BufferedReader(in);
			String callbackLine = null;
			while ((callbackLine = br.readLine()) != null) {
				logger.error(callbackLine);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
