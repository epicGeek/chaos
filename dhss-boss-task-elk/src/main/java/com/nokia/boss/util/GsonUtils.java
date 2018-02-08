package com.nokia.boss.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtils {
	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	public static String toJson(Object src) {
		return gson.toJson(src);
	}

}
