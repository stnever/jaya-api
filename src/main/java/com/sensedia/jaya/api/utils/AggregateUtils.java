package com.sensedia.jaya.api.utils;

import java.util.Collection;

import org.apache.commons.beanutils.PropertyUtils;

public class AggregateUtils {

	public static Double sumDouble(Collection<?> c, String propName) {
		if (c == null || c.size() < 1)
			return 0.0;

		try {
			Double sum = 0.0;
			for (Object o : c) {
				Number v = (Number) PropertyUtils.getProperty(o, propName);
				if (v != null)
					sum += v.doubleValue();
			}
			return sum;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Integer sumInt(Collection<?> c, String propName) {
		return sumDouble(c, propName).intValue();
	}

	public static Double avg(Collection<?> c, String propName) {
		if (c == null || c.size() < 1)
			return 0.0;
		
		return sumDouble(c, propName) / c.size();
	}
}
