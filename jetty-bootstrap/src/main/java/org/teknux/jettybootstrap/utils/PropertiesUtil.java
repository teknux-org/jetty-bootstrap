/*******************************************************************************
 * (C) Copyright 2014 Teknux.org (http://teknux.org/).
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *  
 * Contributors:
 *      "Pierre PINON"
 *      "Francois EYL"
 *      "Laurent MARCHAL"
 *  
 *******************************************************************************/
package org.teknux.jettybootstrap.utils;

import java.util.Properties;


/**
 * Utility class to easy {@link Properties} handling.
 * 
 * @author "Francois EYL"
 */
public class PropertiesUtil {

	private PropertiesUtil() {
	}

	/**
	 * @param p Properties
	 * @param key String
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
	 * @param p Properties
	 * @param key Key
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
	 * @param p Properties
	 * @param key String
	 * @param separator String
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
	 * @param p Properties
	 * @param key String
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
