package com.nokia.pgw.task;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.nokia.pgw.Entry;
import com.nokia.pgw.service.PgwAnalysisService;
import com.nokia.pgw.settings.CustomSetting;
import com.nokia.pgw.util.PgwAnalyseUtil;
@Component
public class PrepareAction implements CommandLineRunner{
	private static final Logger LOGGER = LogManager.getLogger(PrepareAction.class);
	private static String uncompressedFileDir = "";
	private static String rsyncDataFileDir = "";
	private static String loaderFileDir = "";
	private static String loaderDetailFileDir = "";
	private static String loaderXMLFileDir = "";
	private static String rsyncShellDir = "";

	private static String detailDataLoadSQL ;
	private static String XMLDataLoadSQL ;

	@Autowired
	private CustomSetting customSetting;
	@Autowired
	private PgwAnalysisService pgwAnalysisService;
	@Override
	public void run(String... args) throws Exception {
		LOGGER.info(Entry.getLOGGER_HEAD() +"Establish file directory...");
		String deployDir = customSetting.getPgwLogDeployDir();
		if(!deployDir.endsWith("/")){
			deployDir+="/";
			LOGGER.info(Entry.getLOGGER_HEAD() +"Program deploy location:"+deployDir);
		}
		String uncompressedDir = deployDir + "uncompressed/";
		String rsyncDataDir = deployDir + "rsync-data/";
		String loaderDir = deployDir + "loader/";
		String rsyncShellDir = deployDir + "rsync-shell/";
		String loadDetailDataDir = loaderDir+"detail/";
		String loadXmlDataDir = loaderDir + "xml/";
		setUncompressedFileDir(uncompressedDir);
		setRsyncDataFileDir(rsyncDataDir);
		setLoaderFileDir(loaderDir);
		setLoaderDetailFileDir(loadDetailDataDir);
		setLoaderXMLFileDir(loadXmlDataDir);
		setRsyncShellDir(rsyncShellDir);
		File uncompressedDirectory = new File(uncompressedFileDir);
		if(!uncompressedDirectory.exists()){
			uncompressedDirectory.mkdirs();
		}
		File rsyncDataDirectory = new File(rsyncDataFileDir);
		if(!rsyncDataDirectory.exists()){
			rsyncDataDirectory.mkdirs();
		}
		File rsyncShellDirectory = new File(rsyncShellDir);
		if(!rsyncShellDirectory.exists()){
			rsyncShellDirectory.mkdirs();
		}
		File loaderDirectory = new File(loaderFileDir);
		if(!loaderDirectory.exists()){
			loaderDirectory.mkdirs();
		}
		File loadDetailDataDirectory = new File(loadDetailDataDir);
		if(!loadDetailDataDirectory.exists()){
			loadDetailDataDirectory.mkdirs();
		}
		File loadXMLDataDirectory = new File(loadXmlDataDir);
		if(!loadXMLDataDirectory.exists()){
			loadXMLDataDirectory.mkdirs();
		}
		String fieldTerminator = PgwAnalyseUtil.getFieldTerminator();
		String lineTerminator = PgwAnalyseUtil.getLineTerminator();
		String detailSql = " LOAD DATA LOCAL INFILE '#fileAbsPath#' INTO TABLE pgw_detail_data FIELDS TERMINATED BY '#fieldTerminator#' LINES TERMINATED BY '#lineTerminator#'  "
				+ "(\n" +
				"	response_time,\n" +
				"	request_id,\n" +
				"	pgw_name,\n" +
				"	instance_name,\n" +
				"	user_name,\n" +
				"	execution_time,\n" +
				"	execution_content,\n" +
				"	result_type,\n" +
				"	operation,\n" +
				"	user_number,\n" +
				"	imsi,\n" +
				"	msisdn,\n" +
				"	error_code,\n" +
				"	error_message\n" +
				")";
		String xmlSql = 
				" LOAD DATA LOCAL INFILE '#fileAbsPath#' INTO TABLE pgw_xml_log FIELDS TERMINATED BY '#fieldTerminator#' LINES TERMINATED BY '#lineTerminator#'  "
				+ "(\n" +
				"	response_time,\n" +
				"	request_id,\n" +
				"	response_log\n" +
				")";
		detailSql = detailSql.replace("#fieldTerminator#", fieldTerminator).replace("#lineTerminator#", lineTerminator);
		xmlSql = xmlSql.replace("#fieldTerminator#", fieldTerminator).replace("#lineTerminator#", lineTerminator);
		setDetailDataLoadSQL(detailSql);
		setXMLDataLoadSQL(xmlSql);
		if (customSetting.isManualMode() == true) {
			LOGGER.info("Get to manual mode.....");
			pgwAnalysisService.manualMode();
			pgwAnalysisService.clearTempFile();
			LOGGER.info("Manual mode ends.Exit.");
			return;
		}
	}
	public static String getRsyncShellDir() {
		return rsyncShellDir;
	}
	public static void setRsyncShellDir(String rsyncShellDir) {
		PrepareAction.rsyncShellDir = rsyncShellDir;
	}
	public static String getUncompressedFileDir() {
		return uncompressedFileDir;
	}
	public static void setUncompressedFileDir(String uncompressedFileDir) {
		PrepareAction.uncompressedFileDir = uncompressedFileDir;
	}
	public static String getRsyncDataFileDir() {
		return rsyncDataFileDir;
	}
	public static void setRsyncDataFileDir(String rsyncDataFileDir) {
		PrepareAction.rsyncDataFileDir = rsyncDataFileDir;
	}
	public static String getLoaderFileDir() {
		return loaderFileDir;
	}
	public static void setLoaderFileDir(String loaderFileDir) {
		PrepareAction.loaderFileDir = loaderFileDir;
	}
	public static String getLoaderDetailFileDir() {
		return loaderDetailFileDir;
	}
	public static void setLoaderDetailFileDir(String loaderDetailFileDir) {
		PrepareAction.loaderDetailFileDir = loaderDetailFileDir;
	}
	public static String getLoaderXMLFileDir() {
		return loaderXMLFileDir;
	}
	public static void setLoaderXMLFileDir(String loaderXMLFileDir) {
		PrepareAction.loaderXMLFileDir = loaderXMLFileDir;
	}
	public static String getDetailDataLoadSQL() {
		return detailDataLoadSQL;
	}
	public static void setDetailDataLoadSQL(String detailDataLoadSQL) {
		PrepareAction.detailDataLoadSQL = detailDataLoadSQL;
	}
	public static String getXMLDataLoadSQL() {
		return XMLDataLoadSQL;
	}
	public static void setXMLDataLoadSQL(String xMLDataLoadSQL) {
		XMLDataLoadSQL = xMLDataLoadSQL;
	}

}
