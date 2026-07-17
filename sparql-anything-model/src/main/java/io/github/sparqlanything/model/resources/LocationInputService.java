package io.github.sparqlanything.model.resources;

import io.github.sparqlanything.model.*;
import io.github.sparqlanything.model.resources.annotations.TargetOption;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Properties;

@TargetOption(IRIArgument.LOCATION_NAME)
public class LocationInputService implements ResourceService {

	private static final Logger log = LoggerFactory.getLogger(LocationInputService.class);
	public static final String tmpFolder = "tmp";

//	public LocationInputService() {
//		new File(tmpFolder).mkdir();
//	}

	@Override
	public InputStream getInputStream(Properties properties) throws IOException, TriplifierHTTPException {

		if (!properties.containsKey(IRIArgument.FROM_ARCHIVE.toString())) {
			URL url = Triplifier.getLocation(properties);
			// If local throw exception
			if (url.getProtocol().equals("file")) {
				log.debug("Getting input stream from file");
				return url.openStream();
			} else {

				// If HTTP
				if (url.getProtocol().equals("http") || url.getProtocol().equals("https")) {
					CloseableHttpResponse response = HTTPHelper.getInputStream(url, properties);
					if (!HTTPHelper.isSuccessful(response)) {
						log.trace("Request unsuccesful: {}", response.getStatusLine().toString());
						log.trace("Response: {}", response);
						log.trace("Response body: {}", IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset()));
						throw new TriplifierHTTPException(url, response);
					}
					return response.getEntity().getContent();
				}
			}

			// If other protocol, try URL and Connection
			log.debug("Other protocol: {}", url.getProtocol());
			return url.openStream();
		}

		// Handle archives differently
		Charset charset = Triplifier.getCharsetArgument(properties);
		String location = properties.getProperty(IRIArgument.LOCATION.toString());
		String format = properties.getProperty(IRIArgument.ARCHIVE_FORMAT.toString());
		URL urlArchive = Utils.instantiateURL(properties.getProperty(IRIArgument.FROM_ARCHIVE.toString()));
		try {
			return getInputStreamFromArchive(urlArchive, location, charset, format);
		} catch (ArchiveException e) {
			throw new IOException(e); // TODO i think we should throw a TriplifierHTTPException instead
			// to allow the silent keyword to be respected
		}

	}

	private String getArchiverNameFromArchiverFormat(String location, String archiverFormat) {
		String extension = FilenameUtils.getExtension(location);

		if (extension != null && !extension.isEmpty())
			return extension;


		return archiverFormat;
	}


	public InputStream getInputStreamFromArchive(URL archiveLocation, String entryName, Charset charset, String archiverFormat) throws IOException {

		log.trace("Archive location {} entry {}", archiveLocation.toString(), entryName);

		String md5url = DigestUtils.md5Hex(archiveLocation.toString());

		String folder = tmpFolder + "/" + md5url;

		File fileToRead = new File(folder + "/" + entryName);

		// TODO wiping tmp folder

		if (!fileToRead.exists()) {

			log.trace("File to read doesn't exist, extracting it from {}", archiveLocation);
			// extract
			File destinationDir = new File(folder);
			new File(folder).mkdirs();


			String archiverName = getArchiverNameFromArchiverFormat(archiveLocation.toString(), archiverFormat);
			ArchiveInputStream i;
			if (archiverName == null || archiverName.isEmpty()) {
				i = new ArchiveStreamFactory().createArchiveInputStream(archiveLocation.openStream());
			} else {
				i = new ArchiveStreamFactory().createArchiveInputStream(archiverName, archiveLocation.openStream(), charset.toString());
			}

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

		log.trace("Creating input stream of {}", fileToRead.getAbsolutePath());
		return new FileInputStream(fileToRead);
	}
}
