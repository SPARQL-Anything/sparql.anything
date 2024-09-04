/*
 * Copyright (c) 2024 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package io.github.sparqlanything.metadata;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.google.common.collect.Sets;
import io.github.sparqlanything.model.FacadeXGraphBuilder;
import io.github.sparqlanything.model.SPARQLAnythingConstants;
import io.github.sparqlanything.model.Triplifier;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;
import java.util.Set;

@io.github.sparqlanything.model.annotations.Triplifier
public class MetadataTriplifier implements Triplifier {


	@Override
	public void triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException {

		URL url = Triplifier.getLocation(properties);

		if (url == null)
			return;

		String dataSourceId = SPARQLAnythingConstants.DATA_SOURCE_ID;
		String root = SPARQLAnythingConstants.ROOT_ID;
		File f = new File(FilenameUtils.getName(url.getFile()));
		FileUtils.copyURLToFile(url, f);
		readBasicAttributes(f.toPath(), dataSourceId, root, builder);
		try {
			readMetadata(f, dataSourceId, root, builder);
		} catch (ImageProcessingException | IOException e) {
			log.error(e.getMessage());
		}

		f.delete();
	}

	private void readBasicAttributes(Path p, String dataSourceId, String root, FacadeXGraphBuilder builder) throws IOException {
		BasicFileAttributes attr = Files.readAttributes(p, BasicFileAttributes.class);
		builder.addValue(dataSourceId, root, "size", attr.size());
	}

	private void readMetadata(File f, String dataSourceId, String root, FacadeXGraphBuilder builder) throws IOException, ImageProcessingException {

		Metadata metadata = ImageMetadataReader.readMetadata(f);

		for (Directory directory : metadata.getDirectories()) {
			for (Tag tag : directory.getTags()) {
				builder.addValue(dataSourceId, root, tag.getTagName(), tag.getDescription());
			}
		}

	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet();
	}
}
