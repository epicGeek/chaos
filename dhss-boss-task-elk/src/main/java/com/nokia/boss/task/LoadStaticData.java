package com.nokia.boss.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import com.nokia.boss.bean.DefaultConfig;
import com.nokia.boss.bean.LogBase;
import com.nokia.boss.bean.SoapGwLogin;
import com.nokia.boss.settings.CustomSettings;

@Component
/**
 * 
 * @author Pei Nan
 * 
 * 
 *         这个类继承了CommandLineRunner，在程序运行时优先运行一次。用来加载一些常量和创建一些必要的路径。 This class
 *         implements CommandLineRunner.Once the program runs ,this class runs
 *         first. It is used to load some static constants and create some
 *         necessary directory.
 *
 */
public class LoadStaticData implements CommandLineRunner {
	@Autowired
	private CustomSettings customSettings;
	public static List<LogBase> listLogBase;
	public static enum VERSION{
		UNICOM,CHINAMOBILE
	}
	private static String RULE_PATTERN_CM_ERR;// China mobile boss file name
												// pattern
	private static String RULE_PATTERN_CM_SOAP;// China mobile boss file name
												// pattern
	private static String RULE_PATTERN_CUC_ERR;// unicom boss file name pattern
	private static String RULE_PATTERN_CUC_SOAP;// unicom boss file name pattern
	private static String CURR_SOAP_PATTERN;
	private static String CURR_ERR_PATTERN;
	private static String CURR_VERSION;
	private static String RSYNC_DATA_DIR;
	private static Map<String, String> CACHE_DIR = new HashMap<>();

	// 解析JSON串所用到的的KEY
	// Key pattern for analysis JSON data.
	private static String HLRSN_PATTERN = "\"HLRSN\":\"";
	private static String HLRID_PATTERN = "\"HLRID\":\"";
	private static String MSISDN_PATTERN = "\"ISDN\":\"";
	private static String MSISDN_PATTERN_CUC = "\"MSISDN\":\"";
	private static String IMSI_PATTERN = "\"IMSI\":\"";
	private static String IMPU_PATTERN = "\"IMPU\":\"";
	private static String OPERATION_PATTERN = "\"OPERATION\":\"";
	private static String OPERATION_NAME_PATTERN = "\"operationName\":\"";
	private static String TASK_ID_CUC = "\"TASKID\":\"";
	private static String USER_CUC = "\"USER\":\"";
	private static String MML_CUC = "\"MML\":\"";

	@Override
	/**
	 * This method runs first. 这个方法是优先运行的
	 */
	public void run(String... args) throws Exception {
		makeDirs();
		loadYml();
	}

	/**
	 * 加载yml文件
	 */
	@SuppressWarnings("unchecked")
	private void loadYml() {
		try {
			Yaml yaml = new Yaml();
			String path = customSettings.getDefaultConfig().getDataPath();
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			if (file.exists()) {
				List<LogBase> loadAs = yaml.loadAs(new FileInputStream(file), ArrayList.class);
				if (loadAs != null) {
					listLogBase = loadAs;
				} else {
					listLogBase = new ArrayList<>();
				}

			}

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	// 创建必要的文件
	// Create necessary directory.
	private void makeDirs() {
		DefaultConfig defaultConfig = customSettings.getDefaultConfig();
		String rsyncDir = defaultConfig.getRsyncDataDir();
		String loaderDir = defaultConfig.getLoadFileDir();
		String cacheDir = defaultConfig.getCacheDataDir();
		String errRuleDir = defaultConfig.getRuleFileAbsErrPath();
		String soapRuleDir = defaultConfig.getRuleFileAbsSoapPath();

		RSYNC_DATA_DIR = rsyncDir;

		List<File> rsyncFileList = new ArrayList<>();
		List<File> loaderFileList = new ArrayList<>();
		for (SoapGwLogin sg : customSettings.getLoginInfoList()) {
			String sgwName = sg.getSoapGwName();
			String rsyncDirFileStr = rsyncDir + sgwName + "/";
			String rsyncBackupDirFileStr = rsyncDir + sgwName + "/" + "backup/";
			File rsyncDirFile = new File(rsyncDirFileStr);
			File rsyncBackupDirFile = new File(rsyncBackupDirFileStr);
			File cacheF = new File(cacheDir + sgwName + "/");
			rsyncFileList.add(rsyncDirFile);
			rsyncFileList.add(rsyncBackupDirFile);
			File loaderDirFile = new File(loaderDir + sgwName + "/");
			loaderFileList.add(loaderDirFile);
			if (!rsyncDirFile.exists()) {
				rsyncDirFile.mkdirs();
			}
			if (!rsyncBackupDirFile.exists()) {
				rsyncDirFile.mkdirs();
			}
			if (!loaderDirFile.exists()) {
				loaderDirFile.mkdirs();
			}
			if (!cacheF.exists()) {
				cacheF.mkdirs();
				CACHE_DIR.put(sgwName, cacheF.getAbsolutePath()+"/");
			}
			try {
				if (VERSION.CHINAMOBILE.name().equals(defaultConfig.getBossVersion().toUpperCase())) {
					CURR_SOAP_PATTERN = RULE_PATTERN_CM_SOAP;
					CURR_ERR_PATTERN = RULE_PATTERN_CM_ERR;
					CURR_VERSION = VERSION.CHINAMOBILE.name();
					File errRuleFile = new File(errRuleDir);
					if (!errRuleFile.exists()) {
						errRuleFile.createNewFile();
						FileWriter fw = new FileWriter(errRuleFile);
						fw.write(RULE_PATTERN_CM_ERR);
						fw.close();
					}
					File soapRuleFile = new File(soapRuleDir);
					if (!soapRuleFile.exists()) {
						soapRuleFile.createNewFile();
						FileWriter fw = new FileWriter(soapRuleDir);
						fw.write(RULE_PATTERN_CM_SOAP);
						fw.close();
					}

				} else {
					CURR_SOAP_PATTERN = RULE_PATTERN_CUC_SOAP;
					CURR_ERR_PATTERN = RULE_PATTERN_CUC_ERR;
					CURR_VERSION = VERSION.UNICOM.name();
					File errRuleFile = new File(errRuleDir);
					if (!errRuleFile.exists()) {
						errRuleFile.createNewFile();
						FileWriter fw = new FileWriter(errRuleFile);
						fw.write(RULE_PATTERN_CUC_ERR);
						fw.close();
					}
					File soapRuleFile = new File(soapRuleDir);
					if (!soapRuleFile.exists()) {
						soapRuleFile.createNewFile();
						FileWriter fw = new FileWriter(soapRuleDir);
						fw.write(RULE_PATTERN_CUC_SOAP);
						fw.close();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		BOSS_DIR.put("rsync", rsyncFileList);
		BOSS_DIR.put("loader", loaderFileList);
	}

	public static Map<String, String> getCACHE_DIR() {
		return CACHE_DIR;
	}

	public static String getRSYNC_DATA_DIR() {
		return RSYNC_DATA_DIR;
	}

	public static String getCURR_VERSION() {
		return CURR_VERSION;
	}

	/**
	 * 中国移动BOSS日志的名字精确匹配 ChinaMobile BOSS log file name match pattern.
	 */
	static {
		RULE_PATTERN_CM_ERR = "+ */\n" + "+ backup/BOSS_ERR_CASE.log.yyyy-MM-dd.*\n" + "+ BOSS_ERR_CASE.log\n"
				+ "+ BOSS_ERR_CASE.log.yyyy-MM-dd.*\n" + "- *";
		RULE_PATTERN_CM_SOAP = "+ */\n" + "+ backup/BOSS_SOAP_Agent_BOSSA_main_yyyy-MM-dd.*\n"
				+ "+ BOSS_SOAP_Agent_BOSSA_main_yyyy-MM-dd.*\n" + "- *";
		RULE_PATTERN_CUC_ERR = "+ */\n" + "+ backup/CUC_BOSS_ERR_CASE.log.yyyy-MM-dd.*\n" + "+ CUC_BOSS_ERR_CASE.log\n"
				+ "+ CUC_BOSS_ERR_CASE.log.yyyy-MM-dd.*\n" + "- *";
		RULE_PATTERN_CUC_SOAP = "+ */\n" + "+ backup/CUC_Telnet_Agent_CUCA_main_yyyy-MM-dd.*\n"
				+ "+ backup/CUC_Telnet_Agent_BOSSA_main_yyyy-MM-dd.*\n" + "+ CUC_Telnet_Agent_CUCA_main_yyyy-MM-dd.*\n"
				+ "+ CUC_Telnet_Agent_BOSSA_main_yyyy-MM-dd.*\n" + "- *";
	}
	/**
	 * 
	 * Key : operationName 操作 Value: business type 业务类型
	 */
	@SuppressWarnings("serial")
	private static Map<String, String> BUSINESS_TYPE_MAP = new HashMap<String, String>() {
		{
			put("ZVFS", "UNKNOWN");
			put("ZMIS", "UNKNOWN");
			put("ZMNE", "LTE");
			put("ZMIM", "CARD");
			put("ZMNB", "STOPRESET");
			put("ZMNP", "LTE");
			put("ZMNA", "LTE");
			put("ZMGC", "STOPRESET");
			put("ZMBC", "VOICE");
			put("ZMNM", "STOPRESET");
			put("ZMAE", "OVERHEAD");
			put("ZMIR", "OVERHEAD");
			put("ZMSD", "VOICE");
			put("ZMBD", "VOICE");
			put("ZMNC", "GPRS");
			put("ZMIO", "BQUERY");
			put("ZMSO", "BQUERY");
			put("ZMNO", "BQUERY");
			put("ZMQO", "BQUERY");
			put("ZMAO", "BQUERY");
			put("ZMNF", "BQUERY");
			put("ZMSC", "VOICE");
			put("ZMNI", "BQUERY");
			put("ZMSS", "VOICE");
			put("ZMGO", "BQUERY");
			put("ZMND", "GPRS");
			put("ZMQD", "NETWORK");
			put("ZMQE", "NETWORK");
			put("ZMID", "OVERHEAD");
			put("ZMAD", "OVERHEAD");
			put("ZMIO", "BQUERY");
			put("ZMBO", "BQUERY");
			put("ZMNR", "LTE");
			put("ZMND", "GPRS");
			put("ZVIR", "voLTE");
			put("ZVID", "voLTE");
			put("ADD_KI", "OVERHEAD");
			put("RMV_KI", "OVERHEAD");
			put("MOD_ARD", "GPRS_LTE");
			put("LST_ARD", "");
			put("MOD_BS", "VOICE");
			put("LST_BS", "");
			put("MOD_CFU", "VOICE");
			put("MOD_CFNRC", "VOICE");
			put("REG_CFNRC", "VOICE");
			put("ERA_CFNRC", "VOICE");
			put("MOD_CFD", "VOICE");
			put("LST_CFALL", "");
			put("REG_CFU", "VOICE");
			put("ERA_CFU", "VOICE");
			put("MOD_CFB", "VOICE");
			put("REG_CFB", "VOICE");
			put("ERA_CFB", "VOICE");
			put("MOD_CFNRY", "VOICE");
			put("REG_CFNRY", "VOICE");
			put("ERA_CFNRY", "VOICE");
			put("MOD_CB", "VOICE");
			put("ACT_BICROM", "VOICE");
			put("DEA_BICROM", "VOICE");
			put("LST_CBAR", "");
			put("MOD_BARPWD", "VOICE");
			put("MOD_CBCOU", "VOICE");
			put("ACT_BAOC", "VOICE");
			put("DEA_BAOC", "VOICE");
			put("ACT_BOIC", "VOICE");
			put("DEA_BOIC", "VOICE");
			put("ACT_BOICEXHC", "VOICE");
			put("DEA_BOICEXHC", "VOICE");
			put("ACT_BAIC", "VOICE");
			put("DEA_BAIC", "VOICE");
			put("MOD_CLIP", "IDENTIFICATION");
			put("LST_CLIP", "");
			put("MOD_CLIR", "IDENTIFICATION");
			put("LST_CLIR", "");
			put("MOD_COLP", "IDENTIFICATION");
			put("LST_COLP", "");
			put("MOD_COLR", "IDENTIFICATION");
			put("LST_COLR", "");
			put("MOD_PLMNSS", "CUSTOM");
			put("MOD_OSS", "VOICE");
			put("LST_OSS", "");
			put("LST_SS", "");
			put("MOD_LCS", "LOCATION");
			put("LST_LCS", "");
			put("MOD_CARP", "VOICE");
			put("LST_CARP", "");
			put("ADD_SUB", "OVERHEAD");
			put("MOD_IMSI", "OVERHEAD");
			put("MOD_ISDN", "OVERHEAD");
			put("MOD_CATEGORY", "USERTYPE");
			put("LST_CATEGORY", "");
			put("MOD_NAM", "GPRS_VOICE");
			put("LST_NAM", "");
			put("MOD_CCGLOBAL", "CHARGING");
			put("LST_CCGLOBAL", "");
			put("ADD_TPLSUB", "OVERHEAD");
			put("RMV_SUB", "OVERHEAD");
			put("ADD_CSPSSUB", "OVERHEAD");
			put("ADD_TPLCSPSSUB", "OVERHEAD");
			put("RMV_CSPSSUB", "OVERHEAD");
			put("ADD_EPSSUB", "OVERHEAD");
			put("RMV_EPSSUB", "OVERHEAD");
			put("LST_SUB", "");
			put("MOD_CAMEL", "NETWORK");
			put("LST_CAMEL", "");
			put("SND_CANCELC", "LOCATION");
			put("MOD_TS", "VOICE");
			put("LST_TS", "");
			put("MOD_TPLGPRS", "GPRS");
			put("MOD_GPRS_CONTEXT", "GPRS");
			put("LST_GPRS", "");
			put("MOD_TPLEPS", "LTE");
			put("MOD_EPSDATA", "LTE");
			put("MOD_EPS_CONTEXT", "LTE");
			put("LST_EPS", "");
			put("MOD_DIAMRRS", "LTE");
			put("LST_DIAMRRS", "");
			put("ADD_TPLIMSSUB", "OVERHEAD");
			put("MOD_TPLIMSSUB", "VoLTE");
			put("ADD_IMSSUB", "OVERHEAD");
			put("MOD_CAP", "VoLTE");
			put("LST_STNSR", "");
			put("ADD_SIFC", "VoLTE");
			put("RMV_SIFC", "VoLTE");
			put("LST_SIFC", "");
			put("ADD_IFC", "VoLTE");
			put("RMV_IFC", "VoLTE");
			put("LST_IFC", "");
			put("MOD_VOLTETAG", "VoLTE");
			put("LST_VOLTETAG", "");
			put("MOD_HBAR", "VoLTE");
			put("LST_CAP", "");
			put("LST_HBAR", "");
			put("MOD_CHARGID", "VoLTE");
			put("LST_CHARGID", "");
			put("MOD_VNTPLID", "VoLTE");
			put("LST_VNTPLID", "");
			put("MOD_MEDIAID", "VoLTE");
			put("LST_MEDIAID", "");
			put("MOD_STNSR", "VoLTE");
			put("LST_IMSSUB", "");
			put("RMV_IMSSUB", "OVERHEAD");
			put("MOD_LCK", "STOPRESET");
			put("LST_LCK", "");
			put("MOD_RR", "ROAMING");
			put("LST_RR", "");
			put("MOD_ODB", "VOICE");
			put("LST_ODBDAT", "");
			put("BAT_ADD_TPLSUB", "OVERHEAD");
			put("BAT_RMV_SUB", "OVERHEAD");
			put("BAT_ADD_TPLCSPSSUB", "OVERHEAD");
			put("BAT_RMV_CSPSSUB", "OVERHEAD");
			put("BAT_RMV_EPSDATA", "OVERHEAD");
			put("BAT_ADD_KI", "OVERHEAD");
			put("BAT_RMV_KI", "OVERHEAD");
			put("BAT_MOD_LCK", "STOPRESET");
			put("MOD_LCADDRESS", "GPRS_VOICE");
		}
	};

	public static String getCURR_SOAP_PATTERN() {
		return CURR_SOAP_PATTERN;
	}

	public static String getCURR_ERR_PATTERN() {
		return CURR_ERR_PATTERN;
	}

	private static Map<String, List<File>> BOSS_DIR = new HashMap<>();

	public static Map<String, List<File>> getBossDir() {
		return BOSS_DIR;
	}

	@SuppressWarnings("unused")
	private static Map<String, String> getBUSINESS_TYPE_MAP() {
		return BUSINESS_TYPE_MAP;
	}

	public static String getBusinessType(String operationName) {
		Map<String, String> m = BUSINESS_TYPE_MAP;
		if (m.containsKey(operationName)) {
			return m.get(operationName);
		} else {
			return "UNKNOWN";
		}
	}

	public static boolean isCMCC() {
		if (CURR_VERSION.equals(VERSION.CHINAMOBILE.name())) {
			return true;
		} else {
			return false;
		}
	}

	public static String getHLRSN_PATTERN() {
		return HLRSN_PATTERN;
	}

	public static String getHLRID_PATTERN() {
		return HLRID_PATTERN;
	}

	public static String getMSISDN_PATTERN() {
		return MSISDN_PATTERN;
	}

	public static String getIMSI_PATTERN() {
		return IMSI_PATTERN;
	}

	public static String getIMPU_PATTERN() {
		return IMPU_PATTERN;
	}

	public static String getOPERATION_PATTERN() {
		return OPERATION_PATTERN;
	}

	public static String getOPERATION_NAME_PATTERN() {
		return OPERATION_NAME_PATTERN;
	}

	public static String getTASK_ID_CUC() {
		return TASK_ID_CUC;
	}

	public static String getUSER_CUC() {
		return USER_CUC;
	}

	public static String getMML_CUC() {
		return MML_CUC;
	}

	public static String getMSISDN_PATTERN_CUC() {
		return MSISDN_PATTERN_CUC;
	}

}
