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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {

	private static final Logger logger = LoggerFactory.getLogger(Utils.class);
	public static OS platform;

	;

	static {
		String operSys = System.getProperty("os.name").toLowerCase();
		if (operSys.contains("win")) {
			platform = OS.WINDOWS;
		} else if (operSys.contains("nix") || operSys.contains("nux")
				|| operSys.contains("aix")) {
			platform = OS.LINUX;
		} else if (operSys.contains("mac")) {
			platform = OS.MAC;
		} else if (operSys.contains("sunos")) {
			platform = OS.SOLARIS;
		}
		// logger.info("os.name is: " + operSys);
		// logger.info("OS is: " + platform);
	}

	public enum OS {
		WINDOWS, LINUX, MAC, SOLARIS
	}

}
