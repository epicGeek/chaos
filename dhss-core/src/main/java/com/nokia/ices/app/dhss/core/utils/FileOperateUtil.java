package com.nokia.ices.app.dhss.core.utils;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileOperateUtil {
	private static final String REALNAME = "realName";
	private static final String STORENAME = "storeName";
	private static final String SIZE = "size";
	private static final String SUFFIX = "suffix";
	private static final String CONTENTTYPE = "contentType";
	private static final String CREATETIME = "createTime";
	private static final String UPLOADDIR = "uploadDir/";
	public static final String ROOTPATH = "";

	public static String getRealname() {
		return REALNAME;
	}

	public static String getStorename() {
		return STORENAME;
	}

	public static String getSize() {
		return SIZE;
	}

	public static String getSuffix() {
		return SUFFIX;
	}

	public static String getContenttype() {
		return CONTENTTYPE;
	}

	public static String getCreatetime() {
		return CREATETIME;
	}

	public static String getUploaddir() {
		return UPLOADDIR;
	}

	public static String getRootpath() {
		return ROOTPATH;
	}

	/**
	 * <p>
	 * 读取文件返回文件内容
	 * 
	 * @param fileName
	 * @return
	 */
	public static String readFile(String fileName) {
		BufferedReader reader = null;
		StringBuffer lines = new StringBuffer();
		try {
			reader = new BufferedReader(new FileReader(new File(fileName)));

			String line = null;
			while ((line = reader.readLine()) != null) {
				lines.append(line + "\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return lines.toString();
	}


	public static String replaceAllStr(String checkName) {
		if (checkName.indexOf("/") != -1) {
			checkName = checkName.replaceAll("/", "");
		}
		if (checkName.indexOf("\\") != -1) {
			checkName = checkName.replaceAll("\\\\", "");
		}
		return checkName;
	}

	/**
	 * 压缩后的文件名
	 * 
	 */
	@SuppressWarnings("unused")
	private static String zipName(String name) {
		String prefix = "";
		if (name.indexOf(".") != -1) {
			prefix = name.substring(0, name.lastIndexOf("."));
		} else {
			prefix = name;
		}
		return prefix + ".zip";
	}

//	/**
//	 * 上传文件
//	 *
//	 */
//	public static List<Map<String, Object>> upload(
//			Map<String, MultipartFile> fileMap, String[] params,
//			Map<String, Object[]> values) throws Exception {
//
//		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
//
//		// MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest)
//		// request;
//		// Map<String, MultipartFile> fileMap = mRequest.getFileMap();
//
//		String uploadDir = FileOperateUtil.UPLOADDIR;
//		File file = new File(uploadDir);
//
//		if (!file.exists()) {
//			file.mkdir();
//		}


    /**
     * 将上传的文件进行重命名
     */
    @SuppressWarnings("unused")
	private static String rename(String name) {

        Long now = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        Long random = (long) (Math.random() * now);
        String fileName = now + "" + random;

        if (name.indexOf(".") != -1) {
            fileName += name.substring(name.lastIndexOf("."));
        }

        return fileName;
    }



    /**
     * 上传文件
     *
     */
    // public static List<Map<String, Object>> upload(
    // Map<String, MultipartFile> fileMap, String[] params,
    // Map<String, Object[]> values) throws Exception {
    //
    // List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
    //
    // // MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest)
    // // request;
    // // Map<String, MultipartFile> fileMap = mRequest.getFileMap();
    //
    // String uploadDir = FileOperateUtil.UPLOADDIR;
    // File file = new File(uploadDir);
    //
    // if (!file.exists()) {
    // file.mkdir();
    // }
    //
    // String fileName = null;
    // int i = 0;
    // for (Iterator<Map.Entry<String, MultipartFile>> it = fileMap.entrySet()
    // .iterator(); it.hasNext(); i++) {
    //
    // Map.Entry<String, MultipartFile> entry = it.next();
    // MultipartFile mFile = entry.getValue();
    //
    // fileName = mFile.getOriginalFilename();
    //
    // String storeName = rename(fileName);
    //
    // String noZipName = uploadDir + storeName;
    // String zipName = zipName(noZipName);
    //
    // // 上传成为压缩文件
    // ZipOutputStream outputStream = new ZipOutputStream(
    // new BufferedOutputStream(new FileOutputStream(zipName)));
    // outputStream.putNextEntry(new ZipEntry(fileName));
    // // outputStream.setEncoding("GBK");
    //
    // FileCopyUtils.copy(mFile.getInputStream(), outputStream);
    // outputStream.close();
    // Map<String, Object> map = new HashMap<String, Object>();
    // // 固定参数值对
    // map.put(FileOperateUtil.REALNAME, zipName(fileName));
    // map.put(FileOperateUtil.STORENAME, zipName(storeName));
    // map.put(FileOperateUtil.SIZE, new File(zipName).length());
    // map.put(FileOperateUtil.SUFFIX, "zip");
    // map.put(FileOperateUtil.CONTENTTYPE, "application/octet-stream");
    // map.put(FileOperateUtil.CREATETIME, new Date());
    //
    // // 自定义参数值对
    // for (String param : params) {
    // map.put(param, values.get(param)[i]);
    // }
    //
    // result.add(map);
    // }
    // return result;
    // }
 
    /**
     * 下载
     * 
     * /** 文件下载
     * 
     * @param request
     * @param response
     * @param downLoadPath
     * @param contentType
     * @param realName
     * @throws Exception
     */
    public static void download(Object request, Object response, String downLoadPath, String contentType,
            String realName) throws Exception {
        // request.setCharacterEncoding("UTF-8");
        // BufferedInputStream bis = null;
        // BufferedOutputStream bos = null;
        //
        // File file = new File(downLoadPath);
        // response.setContentType(contentType);
        // response.setHeader("Content-disposition", "attachment; filename="
        // + new String(realName.getBytes("utf-8"), "ISO8859-1"));
        // response.setHeader("Content-Length", String.valueOf(file.length()));
        // bis = new BufferedInputStream(new FileInputStream(file));
        // bos = new BufferedOutputStream(response.getOutputStream());
        // byte[] buff = new byte[2048];
        // int bytesRead;
        // while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
        // bos.write(buff, 0, bytesRead);
        // }
        // bis.close();
        // bos.close();
    }

    public static void saveAs(String appendContent, String direcotry, String fileName) throws IOException {
        File sessionDirFile = new File(direcotry);
        if (!sessionDirFile.exists()) {
            sessionDirFile.mkdir();
        }
        File sessionFile = new File(sessionDirFile + fileName);
        if (!sessionFile.exists()) {
            sessionFile.createNewFile();
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter(sessionFile, true));
        bw.append(appendContent);
        bw.flush();
        bw.close();
    }

    public static void saveAs(String appendContent, String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
        bw.append(appendContent);
        bw.flush();
        bw.close();
    }

    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

//    public static String getRootPath(String path) {
//
//        String rootPaht = path.replaceAll("/", "\\\\");
//        int len = rootPaht.lastIndexOf("WEB-INF");
//        rootPaht = rootPaht.substring(1, len);
//        return rootPaht;
//    }

//    public static String getPropertieValue(String key) {
//        Resource resource = null;
//        Properties props = null;
//        String filePath = null;
//        try {
//            resource = new ClassPathResource("/application.properties");
//            props = PropertiesLoaderUtils.loadProperties(resource);
//            filePath = (String) props.get(key);
//        } catch (Exception e) {
//
//        }
//        return filePath;
//    }

//    public static Set<String> loadAllValues(String resourceName, ClassLoader classLoader) throws IOException {
//        Assert.notNull(resourceName, "Resource name must not be null");
//        ClassLoader classLoaderToUse = classLoader;
//        if (classLoaderToUse == null) {
//            classLoaderToUse = ClassUtils.getDefaultClassLoader();
//        }
//        Enumeration<URL> urls = (classLoaderToUse != null ? classLoaderToUse.getResources(resourceName)
//                : ClassLoader.getSystemResources(resourceName));
//        Set<String> resultSet = new HashSet<String>();
//        while (urls.hasMoreElements()) {
//            URL url = urls.nextElement();
//            URLConnection con = url.openConnection();
//            ResourceUtils.useCachesIfNecessary(con);
//            InputStream is = con.getInputStream();
//            try {
//                LineReader lr = new LineReader(is);
//                int limit;
//                int keyLen;
//                char c;
//                boolean precedingBackslash;
//
//                while ((limit = lr.readLine()) >= 0) {
//                    keyLen = 0;
//                    precedingBackslash = false;
//                    while (keyLen < limit) {
//                        c = lr.lineBuf[keyLen];
//                        // check if escaped.
//                        if (c == '/') {
//                            precedingBackslash = true;
//                            break;
//                        } else {
//                            precedingBackslash = false;
//                        }
//                        keyLen++;
//                    }
//                    if (!precedingBackslash) {
//                        String value = new String(lr.lineBuf, 0, limit).trim();
//                        if (value.length() > 0) {
//                            resultSet.add(new String(lr.lineBuf, 0, limit));
//                        }
//                    }
//                }
//            } finally {
//                is.close();
//            }
//        }
//        return resultSet;
//    }

    static class LineReader {
        public LineReader(InputStream inStream) {
            this.inStream = inStream;
            inByteBuf = new byte[8192];
        }

        public LineReader(Reader reader) {
            this.reader = reader;
            inCharBuf = new char[8192];
        }

        byte[] inByteBuf;
        char[] inCharBuf;
        char[] lineBuf = new char[1024];
        int inLimit = 0;
        int inOff = 0;
        InputStream inStream;
        Reader reader;

        int readLine() throws IOException {
            int len = 0;
            char c = 0;

            boolean skipWhiteSpace = true;
            boolean isCommentLine = false;
            boolean isNewLine = true;
            boolean appendedLineBegin = false;
            boolean precedingBackslash = false;
            boolean skipLF = false;

            while (true) {
                if (inOff >= inLimit) {
                    inLimit = (inStream == null) ? reader.read(inCharBuf) : inStream.read(inByteBuf);
                    inOff = 0;
                    if (inLimit <= 0) {
                        if (len == 0 || isCommentLine) {
                            return -1;
                        }
                        return len;
                    }
                }
                if (inStream != null) {
                    // The line below is equivalent to calling a
                    // ISO8859-1 decoder.
                    c = (char) (0xff & inByteBuf[inOff++]);
                } else {
                    c = inCharBuf[inOff++];
                }
                if (skipLF) {
                    skipLF = false;
                    if (c == '\n') {
                        continue;
                    }
                }
                if (skipWhiteSpace) {
                    if (c == ' ' || c == '\t' || c == '\f') {
                        continue;
                    }
                    if (!appendedLineBegin && (c == '\r' || c == '\n')) {
                        continue;
                    }
                    skipWhiteSpace = false;
                    appendedLineBegin = false;
                }
                if (isNewLine) {
                    isNewLine = false;
                    if (c == '#' || c == '!') {
                        isCommentLine = true;
                        continue;
                    }
                }

                if (c != '\n' && c != '\r') {
                    lineBuf[len++] = c;
                    if (len == lineBuf.length) {
                        int newLength = lineBuf.length * 2;
                        if (newLength < 0) {
                            newLength = Integer.MAX_VALUE;
                        }
                        char[] buf = new char[newLength];
                        System.arraycopy(lineBuf, 0, buf, 0, lineBuf.length);
                        lineBuf = buf;
                    }
                    // flip the preceding backslash flag
                    if (c == '\\') {
                        precedingBackslash = !precedingBackslash;
                    } else {
                        precedingBackslash = false;
                    }
                } else {
                    // reached EOL
                    if (isCommentLine || len == 0) {
                        isCommentLine = false;
                        isNewLine = true;
                        skipWhiteSpace = true;
                        len = 0;
                        continue;
                    }
                    if (inOff >= inLimit) {
                        inLimit = (inStream == null) ? reader.read(inCharBuf) : inStream.read(inByteBuf);
                        inOff = 0;
                        if (inLimit <= 0) {
                            return len;
                        }
                    }
                    if (precedingBackslash) {
                        len -= 1;
                        // skip the leading whitespace characters in following
                        // line
                        skipWhiteSpace = true;
                        appendedLineBegin = true;
                        precedingBackslash = false;
                        if (c == '\r') {
                            skipLF = true;
                        }
                    } else {
                        return len;
                    }
                }
            }
        }
    }
}
