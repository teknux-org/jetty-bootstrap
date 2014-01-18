package org.teknux.jettybootstrap.utils;

import java.util.Properties;


/**
 * Utility class to easy {@link Properties} handling.
 * 
 * @author "Francois EYL"
 */
public class PropertiesUtils {

	private PropertiesUtils() {
	}

	/**
	 * @param p
	 * @param key
	 * @return the {@link Long} or <code>null</code>
	 */
	public static Long parseLong(Properties p, String key) {
		if (p == null || key == null) {
			return null;
		}

		String value = p.getProperty(key);
		if (value == null) {
			return null;
		}

		return Long.parseLong(value);
	}

	/**
	 * @param p
	 * @param key
	 * @return {@link Integer} or <code>null</code>
	 */
	public static Integer parseInt(Properties p, String key) {
		if (p == null || key == null) {
			return null;
		}

		String value = p.getProperty(key);
		if (value == null) {
			return null;
		}

		return Integer.parseInt(value);
	}

	/**
	 * @param p
	 * @param key
	 * @param separator
	 * @return an array of {@link String} or
	 *         <code>null</code>
	 */
	public static String[] parseArray(Properties p, String key, String separator) {
		if (p == null || key == null || separator == null || separator.isEmpty()) {
			return null;
		}

		String value = p.getProperty(key);
		if (value == null) {
			return null;
		}

		return value.contains(separator) ? value.split(separator) : new String[] { value };
	}

	/**
	 * @param p
	 * @param key
	 * @return {@link Boolean} or <code>null</code>
	 */
	public static Boolean parseBoolean(Properties p, String key) {
		if (p == null || key == null) {
			return null;
		}

		String value = p.getProperty(key);
		if (value == null) {
			return null;
		}

		return Boolean.parseBoolean(value);
	}
}
