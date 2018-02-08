package com.nokia.boss.service;

import java.io.IOException;
import java.text.ParseException;

import org.dom4j.DocumentException;

public interface TaskProcService {

	public void executeEntry() throws IOException, InterruptedException, DocumentException, ParseException;

}