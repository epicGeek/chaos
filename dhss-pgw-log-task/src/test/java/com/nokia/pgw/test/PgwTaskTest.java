package com.nokia.pgw.test;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.nokia.pgw.service.PgwAnalysisService;
import com.nokia.pgw.settings.CustomSetting;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PgwTaskTest {
	private Logger LOGGER = LogManager.getLogger(PgwTaskTest.class);

	@Autowired
	private CustomSetting customSetting;
	@Autowired
	private PgwAnalysisService pgwAnalysisService;
	@Test
	public void test () {
		List<String> commandList = pgwAnalysisService.getAllRsyncCommandForMainland();
		for (String cmd : commandList) {
			LOGGER.info(cmd);
		}
	}
}
