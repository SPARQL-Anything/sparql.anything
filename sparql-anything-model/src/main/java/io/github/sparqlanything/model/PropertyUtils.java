/*
 * Copyright (c) 2023 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.sparqlanything.model;

import java.util.Properties;

public class PropertyUtils {
	public static boolean getBooleanProperty(Properties p, String key, boolean defaultValue) {
		if (p.containsKey(key)) {
			return Boolean.parseBoolean(p.getProperty(key));
		}
		return defaultValue;
	}

	public static boolean getBooleanProperty(Properties p, IRIArgument key, boolean defaultValue) {
		return getBooleanProperty(p, key.toString(), defaultValue);
	}

	public static boolean getBooleanProperty(Properties p, IRIArgument key) {
		return getBooleanProperty(p, key.toString(), Boolean.parseBoolean(key.getDefaultValue()));
	}

	public static String getStringProperty(Properties p, String key, String defaultValue) {
		return p.getProperty(key, defaultValue);
	}

	public static String getStringProperty(Properties p, IRIArgument key, String defaultValue) {
		return getStringProperty(p, key.toString(), defaultValue);
	}

	public static String getStringProperty(Properties p, IRIArgument key) {
		if (key.getDefaultValue() != null) {
			return getStringProperty(p, key, key.getDefaultValue());
		}
		return getStringProperty(p, key, null);
	}


	public static Integer getIntegerProperty(Properties p, String key, Integer defaultValue) {
		if (p.containsKey(key)) {
			return Integer.parseInt(p.getProperty(key));
		}
		return defaultValue;
	}

	public static Integer getIntegerProperty(Properties p, IRIArgument key, Integer defaultValue) {
		return getIntegerProperty(p, key.toString(), defaultValue);
	}

	public static Integer getIntegerProperty(Properties p, IRIArgument key) {
		if (key.getDefaultValue() != null) {
			return getIntegerProperty(p, key, Integer.parseInt(key.getDefaultValue()));
		}
		return getIntegerProperty(p, key, null);
	}
}
