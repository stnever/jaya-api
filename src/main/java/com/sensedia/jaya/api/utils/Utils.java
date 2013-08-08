package com.sensedia.jaya.api.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class Utils {

	public static boolean isBlank(String s) {
		return s == null || s.trim().length() == 0;
	}

	public static Integer getInt(String[] pieces, int i) {
		if (i >= pieces.length)
			return null;
		if (isBlank(pieces[i]))
			return null;
		return Integer.valueOf(pieces[i]);
	}

	public static Long getLong(String[] pieces, int i) {
		if (i >= pieces.length)
			return null;
		if (isBlank(pieces[i]))
			return null;
		return Long.valueOf(pieces[i]);
	}

	public static String trimInitialSlash(String s) {
		if (s == null)
			return null;
		s = s.trim();
		if (s.startsWith("/"))
			return s.substring(1);
		else
			return s;
	}

	public static String getProperty(String name, String defValue) {
		String s = System.getProperty(name);
		if (isBlank(s))
			return defValue;
		return s;
	}

	public static SortedMap<String, String> getProperties(String... args) {
		SortedMap<String, String> r = new TreeMap<String, String>();
		for (Object key : System.getProperties().keySet()) {
			String k = key.toString();
			for (String prefix : args)
				if (k.startsWith(prefix))
					r.put(k, System.getProperty(k));
		}
		return r;
	}

	public static String toCamelCase(String s) {
		StringBuffer sb = new StringBuffer(s.toLowerCase());
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		return sb.toString();
	}

	public static String getStackTrace(Throwable t) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		t.printStackTrace(printWriter);

		return stringWriter.getBuffer().toString();
	}

	public static String ltrim(String str, char ch) {
		if (str == null)
			return null;

		int pos = 0;
		while (str.charAt(pos) == ch)
			pos++;

		return str.substring(pos);
	}

	public static int nthIndexOf(String s, int ch, int count) {
		int i = s.indexOf(ch);
		while (i >= 0 && count > 1) {
			i = s.indexOf('/', i + 1);
			count--;
		}

		return i;
	}

	public static Map<String, Object> makeMap(Object... args) {
		if (args.length % 2 != 0)
			throw new IllegalArgumentException("Numero de parametros deve ser par");

		Map<String, Object> result = new HashMap<String, Object>();
		for (int i = 0; i < args.length; i = i + 2) { // anda de 2 em 2
			if (args[i] == null)
				throw new IllegalArgumentException("Parametros com indice par nao podem ser null");
			String key = args[i].toString();
			Object value = args[i + 1];
			result.put(key, value);
		}

		return result;
	}
}
