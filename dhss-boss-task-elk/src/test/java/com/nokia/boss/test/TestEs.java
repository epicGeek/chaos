package com.nokia.boss.test;



import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.nokia.boss.service.ElasticSearchBulkService;
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestEs {
	@Autowired
	private ElasticSearchBulkService esSearchBulkService;

	@Test
	public void test() {
		//fail("Not yet implemented");
		//esSearchBulkService.deleteESData();
	}

}
