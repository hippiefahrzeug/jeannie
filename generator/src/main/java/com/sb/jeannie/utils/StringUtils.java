package com.sb.jeannie.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StringUtils {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	public static String toString(Object obj) {
		return GSON.toJson(obj);
	}
}
