/*
 * Copyright (c) 2025 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 */

package io.github.sparqlanything.model;

import org.apache.jena.query.ARQ;
import org.apache.jena.sparql.ARQConstants;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.util.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class Utils {

	private static final Logger log = LoggerFactory.getLogger(Utils.class);
	public static OS platform;

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

	private static String resolveTilde(String urlLocation) {
		if (urlLocation.startsWith("~"))
			return urlLocation.replaceFirst("^~", System.getProperty("user.home"));
		return urlLocation;
	}

	public static URL instantiateURL(String urlLocation) throws MalformedURLException {
		log.trace("URL Location {}", urlLocation);
		String urlString = resolveTilde(urlLocation);
		URL url;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException u) {
			log.trace("Malformed url interpreting as file");
			url = new File(urlString).toURI().toURL();
		}
		log.trace("Result {}", url);
		return url;
	}

	public static void loadJARs(String[] paths) throws IOException {
		if (paths == null || paths.length == 0) return;

		ArrayList<String> classNames = new ArrayList<>();
		URL[] urls = new URL[paths.length];
		for (int i = 0; i < urls.length; i++) {
//			urls[i] = Path.of(paths[i]).toUri().toURL();
			urls[i] = Utils.instantiateURL(paths[i]);
			JarInputStream jis = new JarInputStream(urls[i].openStream());
			classNames.addAll(getClassNamesFromJar(jis));
		}

		try (URLClassLoader child = new URLClassLoader(urls, Utils.class.getClassLoader())) {
			for (String className : classNames) {
				try {
					Class<?> k = child.loadClass(className);
					if (Arrays.stream(k.getInterfaces()).anyMatch(iFaceClass -> iFaceClass == PluginInitializer.class)) {
						PluginInitializer p = (PluginInitializer) k.getConstructor().newInstance();
						p.run();
					}
				} catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
						 IllegalAccessException | NoSuchMethodException |
						 NoClassDefFoundError | IncompatibleClassChangeError e) {
					//throw new RuntimeException(e);
					System.err.println("Cannot load class ".concat(className));
				}
			}
		}

	}

	private static ArrayList<String> getClassNamesFromJar(JarInputStream jarFile) throws IOException {
		ArrayList<String> classNames = new ArrayList<>();

		JarEntry jar;

		//Iterate through the contents of the jar file
		while (true) {
			jar = jarFile.getNextJarEntry();
			if (jar == null) {
				break;
			}
			//Pick file that has the extension of .class
			if ((jar.getName().endsWith(".class"))) {
				String className = jar.getName().replaceAll("/", "\\.");
				String myClass = className.substring(0, className.lastIndexOf('.'));
				classNames.add(myClass);
			}
		}
		return classNames;
	}

	public enum OS {
		WINDOWS, LINUX, MAC, SOLARIS
	}

	public static void profile(SPARQLAnythingConstants.PROFILE_EVENT event) {
		long t = System.currentTimeMillis();
		Map<SPARQLAnythingConstants.PROFILE_EVENT, Long> profile = SPARQLAnythingContext.getInstance().get(SPARQLAnythingConstants.PROFILE);
		profile.put(event, t);
	}

	public static void printProfile(String outfile) throws FileNotFoundException {
		FileOutputStream profileOut = new FileOutputStream(outfile);
		PrintWriter pw = new PrintWriter(profileOut);
		Map<SPARQLAnythingConstants.PROFILE_EVENT, Long> profile = SPARQLAnythingContext.getInstance().get(SPARQLAnythingConstants.PROFILE);
		final long processStarts = profile.get(SPARQLAnythingConstants.PROFILE_EVENT.LOAD_MAIN_CLASS);

		profile.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(e -> {
			pw.println(e.getKey() + "\t" + e.getValue() + "\t" + (e.getValue() - processStarts));
			if (log.isTraceEnabled()) {
				log.trace("[time] {}: {}", e.getKey(), e.getValue());
			}
		});
		pw.flush();
		pw.close();
	}

}
