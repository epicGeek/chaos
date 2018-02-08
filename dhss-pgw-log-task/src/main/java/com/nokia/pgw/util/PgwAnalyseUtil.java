package com.nokia.pgw.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nokia.pgw.Entry;

public class PgwAnalyseUtil {
	private static final Logger LOGGER = LogManager.getLogger(PgwAnalyseUtil.class);
	private static final String FIELD_TERMINATOR = "~";
	private static final String LINE_TERMINATOR = ";;;";
	private static final String LOAD_DETAIL_DATA_SQL = ""; 
	private static final String LOAD_XML_LOG_DATA_SQL = "";

	/**
	 * @author Pei Nan
	 * @param orinalGzFile
	 * @param destinationPath
	 * @throws IOException 
	 * @see uncompress the orginal gz file to destination path.
	 */
	public static File uncompressGzFile(File orinalGzFile, String destinationPath) throws IOException {
		File destinationPathDir = new File(destinationPath);
		if (!destinationPathDir.exists()) {
			destinationPathDir.mkdirs();
		}
		if (!orinalGzFile.getName().contains(".gz")) {
			LOGGER.info(Entry.getLOGGER_HEAD() +"File extention is not gz:\n{" + orinalGzFile.getAbsolutePath()+"} moved to the uncompress dir.");
			FileUtils.copyFileToDirectory(orinalGzFile, destinationPathDir);
			if(!destinationPath.endsWith("/")){
				destinationPath+="/";
			}
			return new File(destinationPath+orinalGzFile.getName());
		}else{
			LOGGER.info(Entry.getLOGGER_HEAD() +"File extention is gz:\n{" + orinalGzFile.getAbsolutePath()+"} ungzipped to the uncompress dir.");
			GZIPInputStream in = null;
			File outPutUncompressedFile = null;
			FileOutputStream out = null;
			try {
				outPutUncompressedFile = new File(destinationPath + orinalGzFile.getName().replace(".gz", ""));
				in = new GZIPInputStream(new FileInputStream(orinalGzFile));
				out = new FileOutputStream(outPutUncompressedFile);
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
				LOGGER.info(Entry.getLOGGER_HEAD() +"Uncompressed a new file:" + outPutUncompressedFile.getAbsolutePath());
				return outPutUncompressedFile;
			} catch (Exception e) {
				LOGGER.info(Entry.getLOGGER_HEAD() +"Fail to uncomress gz file:" + orinalGzFile.getAbsolutePath());
				e.printStackTrace();
				return new File(destinationPath+"/"+orinalGzFile.getName());
			}
		}

	}

	public static File uncompressGzFile(String originalGzFileAbsPath, String destinationPath) throws IOException {
		File originalGzFile = new File(originalGzFileAbsPath);
		return uncompressGzFile(originalGzFile, destinationPath);
	}

	public static void compressFileToGz(String originalFileAbsPath) {
		File originalFile = new File(originalFileAbsPath);
		compressFileToGz(originalFile);
	}

	private static void compressFileToGz(File originalFile) {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String outFileName = originalFile.getAbsolutePath() +"_"+sdf.format(d)+ ".gz";
		FileInputStream in = null;
		try {
			in = new FileInputStream(originalFile);
		} catch (FileNotFoundException e) {
			System.out.println("Could not find the inFile:" + originalFile.getAbsolutePath());
		}

		GZIPOutputStream out = null;
		try {
			out = new GZIPOutputStream(new FileOutputStream(outFileName));
		} catch (IOException e) {
			System.out.println("Could not find the outFile:" + outFileName);

		}
		byte[] buf = new byte[10240];
		int len = 0;
		try {
			while (((in.available() > 10240) && (in.read(buf)) > 0)) {
				out.write(buf);
			}
			len = in.available();
			in.read(buf, 0, len);
			out.write(buf, 0, len);
			in.close();
			System.out.println("Completing the GZIP file:" + outFileName);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.getMessage();
		}
	}

	public static String getFieldTerminator() {
		return FIELD_TERMINATOR;
	}

	public static String getLineTerminator() {
		return LINE_TERMINATOR;
	}

	public static String getLoadDetailDataSQL() {
		return LOAD_DETAIL_DATA_SQL;
	}

	public static String getLoadXMLDataSQL() {
		return LOAD_XML_LOG_DATA_SQL;
	}

}
