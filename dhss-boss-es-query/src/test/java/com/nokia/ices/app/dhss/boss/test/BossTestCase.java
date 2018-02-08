package com.nokia.ices.app.dhss.boss.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import com.nokia.ices.app.dhss.service.BossQueryService;
import com.nokia.ices.app.dhss.service.impl.BossQueryServiceImpl;
@RunWith(SpringRunner.class)
@SpringBootTest
public class BossTestCase {
	@Autowired
	BossQueryService bossQueryService;
	//@Test
	public void getHlrsnList(){
		List<String> hlrsnList = bossQueryService.getHlrsnList();
		Assert.notEmpty(hlrsnList,"Hlrsn list is empty!");
	}
	//@Test
	public void getCommandList(){
		List<String> cmdList = bossQueryService.getCommandList();
		Assert.notEmpty(cmdList,"commond list is empty!");
	}
	//@Test
	public void getTaskIdResponse() throws IOException{
		RestClient lowLevelRestClient = RestClient.builder(new HttpHost("172.16.73.50", 9200, "http")).build();
		RestHighLevelClient client = new RestHighLevelClient(lowLevelRestClient);
		SearchRequest searchRequest = new SearchRequest("boss-data-4");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		TermQueryBuilder tqb = new TermQueryBuilder("task_id.keyword", "a-06adb23a-8118-480d-acbb-a5a1c456bffd#1510219712071");
		searchSourceBuilder.query(tqb);
		searchRequest.source(searchSourceBuilder);
		SearchResponse sr = client.search(searchRequest);
		SearchHit[] searchHits = sr.getHits().getHits();
		for (SearchHit searchHit : searchHits) {
			System.out.println(searchHit.getSourceAsMap().get("task_id"));
		}
	}
	//@Test
	public void testIndicesGetter(){
		DateTime endDate = new DateTime();
		DateTime startDate = endDate.minusDays(-1);
		BossQueryServiceImpl.getIndices(startDate, endDate);
	}
	
	//@Test
	public void testTimeScope(){
		DateTime startDate = new DateTime();
		DateTime endDate = new DateTime().plusMinutes(35);
		BossQueryServiceImpl.getTimeScopes(startDate, endDate, "15");
	}
	//@Test
    public void test2(){
        RestClient lowLevelRestClient = RestClient.builder(
                new HttpHost("172.16.73.50", 9200, "http")).build();
        RestHighLevelClient client =
                new RestHighLevelClient(lowLevelRestClient);
        SearchRequest searchRequest = new SearchRequest("bank");
        searchRequest.types("account");
        TermsAggregationBuilder aggregation = AggregationBuilders.terms("group_by_state")
                .field("state.keyword");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.aggregation(aggregation);
        searchSourceBuilder.size(0);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = client.search(searchRequest);
            Aggregations aggs = searchResponse.getAggregations();
            Terms byStateAggs = aggs.get("group_by_state");
            Terms.Bucket b = byStateAggs.getBucketByKey("ID"); //只取key是ID的bucket
            System.out.println(b.getKeyAsString()+","+b.getDocCount());
            System.out.println("!!!");
            List<? extends Bucket> aggList = byStateAggs.getBuckets();//获取bucket数组里所有数据
            for (Bucket bucket : aggList) {
                System.out.println("key:"+bucket.getKeyAsString()+",docCount:"+bucket.getDocCount());;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
 //   @Test
    public void tryToDeleteIndex() throws IOException, InterruptedException{
    	// execute shell script
    	//curl -XDELETE 'localhost:9200/mock-index-2018-01-11'
//    	RestClient lowLevelRestClient = RestClient.builder(
//                new HttpHost("172.16.73.52", 9200, "http")).build();
//        RestHighLevelClient client =
//                new RestHighLevelClient(lowLevelRestClient);
        File deleteScript = new File("/home/fileShare/delete.sh");
        FileWriter fw = new FileWriter(deleteScript);
        String deleteCmd = "curl -XDELETE 'localhost:9200/mock-index-2018-01-13'";
        fw.write(deleteCmd);
        fw.close();
        Process rsyncProcess = Runtime.getRuntime().exec("sh"+" "+deleteScript.getAbsolutePath());
		rsyncProcess.waitFor();
		InputStreamReader in = new InputStreamReader(rsyncProcess.getErrorStream());
		BufferedReader br = new BufferedReader(in);
		String callbackLine = null;
        while ((callbackLine = br.readLine()) != null) {
				System.out.println(callbackLine);
		}
    }
}
