/*
 * Copyright (c) 2021 SPARQL Anything Contributors @ http://github.com/sparql-anything
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
 */

package com.github.sparqlanything.metadata;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;
import java.util.Set;

import com.github.sparqlanything.model.FacadeXGraphBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.graph.GraphFactory;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.github.sparqlanything.model.Triplifier;

public class MetadataTriplifier implements Triplifier {


	@Override
	public DatasetGraph triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException {

		URL url = Triplifier.getLocation(properties);

		if (url == null)
			return DatasetGraphFactory.create();

		DatasetGraph dg = DatasetGraphFactory.create();

		String dataSourceId = Triplifier.getRootArgument(properties);
		String root = dataSourceId;
		File f = new File(FilenameUtils.getName(url.getFile()));
		FileUtils.copyURLToFile(url, f);
		readBasicAttributes(f.toPath(), dataSourceId, root, builder);
		try {
			readMetadata(f, dataSourceId, root, builder);
		} catch (ImageProcessingException | IOException e) {
			e.printStackTrace();
		}

		dg = builder.getDatasetGraph();

		f.delete();

		return dg;
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
