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

package com.github.sparqlanything.model;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class ResourceManager {

	public static final String tmpFolder = "tmp";
	private final static Logger logger = LoggerFactory.getLogger(ResourceManager.class);
	private static ResourceManager instance;

	private ResourceManager() {
		new File(tmpFolder).mkdir();
	}

	public static ResourceManager getInstance() {
		if (instance == null) {
			instance = new ResourceManager();
		}
		return instance;
	}

	public InputStream getInputStreamFromArchive(URL archiveLocation, String entryName, Charset charset) throws ArchiveException, IOException {

		logger.trace("Archive location {} entry {}", archiveLocation.toString(), entryName);

		String md5url = DigestUtils.md5Hex(archiveLocation.toString());

		String folder = tmpFolder + "/" + md5url;

		File fileToRead = new File(folder + "/" + entryName);

		// TODO wiping tmp folder

		if (!fileToRead.exists()) {

			logger.trace("Extracting content from {}", archiveLocation);
			// extract
			File destinationDir = new File(folder);
			new File(folder).mkdir();

			ArchiveInputStream i = new ArchiveStreamFactory().createArchiveInputStream(FilenameUtils.getExtension(archiveLocation.toString()), archiveLocation.openStream(), charset.toString());
			ArchiveEntry entry = null;
			while ((entry = i.getNextEntry()) != null) {

				if (!i.canReadEntryData(entry)) {
					continue;
				}

				File file = new File(destinationDir, entry.getName());

				if (!file.toPath().normalize().startsWith(destinationDir.toPath()))
					throw new IOException("Bad zip entry");

				if (entry.isDirectory()) {
					if (!file.isDirectory() && !file.mkdirs()) {
						throw new IOException("failed to create directory " + file);
					}
				} else {
					File parent = file.getParentFile();
					if (!parent.isDirectory() && !parent.mkdirs()) {
						throw new IOException("failed to create directory " + parent);
					}
					try (OutputStream o = Files.newOutputStream(file.toPath())) {
						IOUtils.copy(i, o);
					}
				}
			}
		}

//		if (fileToRead.isDirectory()){
//			logger.error(fileToRead.getAbsolutePath() + " does not exist!");
//			return new ByteArrayInputStream(new byte[]{});
//			// throw new RuntimeException(fileToRead.getAbsolutePath() + " does not exist!");
//		}

		return new FileInputStream(fileToRead);
	}

}
