package com.github.spiceh2020.sparql.anything.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceManager {

	private Logger logger = LoggerFactory.getLogger(ResourceManager.class);

	private static ResourceManager instance;

	public static final String tmpFolder = "tmp";

	private ResourceManager() {
		new File(tmpFolder).mkdir();
	}

	public static ResourceManager getInstance() {
		if (instance == null) {
			instance = new ResourceManager();
		}
		return instance;
	}

	public InputStream getInputStreamFromArchive(URL archiveLocation, String entryName, Charset charset)
			throws ArchiveException, IOException {

		logger.trace("Archive location {} entry {}", archiveLocation.toString(), entryName);

		String md5url = DigestUtils.md5Hex(archiveLocation.toString());

		String folder = tmpFolder + "/" + md5url;

		File fileToRead = new File(folder + "/" + entryName);

		// TODO wiping tmp folder

		if (!fileToRead.exists()) {

			logger.trace("Extracting content from {}", archiveLocation.toString());
			// extract
			new File(folder).mkdir();

			ArchiveInputStream i = new ArchiveStreamFactory().createArchiveInputStream(
					FilenameUtils.getExtension(archiveLocation.toString()), archiveLocation.openStream(),
					charset.toString());
			ArchiveEntry entry = null;
			while ((entry = i.getNextEntry()) != null) {

				if (!i.canReadEntryData(entry)) {
					continue;
				}

				String filenameout = folder + "/" + entry.getName();

				File file = new File(filenameout);

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

		return new FileInputStream(new File(folder + "/" + entryName));
	}

}
