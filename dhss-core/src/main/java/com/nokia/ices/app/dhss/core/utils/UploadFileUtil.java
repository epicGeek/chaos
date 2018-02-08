package com.nokia.ices.app.dhss.core.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

public class UploadFileUtil {
	public static File saveUploadFileToDest(MultipartFile multipartFile,String saveDir) throws IOException{
		File uploadFile = new File(saveDir+multipartFile.getOriginalFilename());
		FileUtils.writeByteArrayToFile(uploadFile, multipartFile.getBytes());
		return uploadFile;
	}
}
