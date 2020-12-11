package com.github.spiceh2020.sparql.anything.metadata;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;
import java.util.Set;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.github.spiceh2020.sparql.anything.model.IRIArgument;
import com.github.spiceh2020.sparql.anything.model.Triplifier;

public class MetadataTriplifier implements Triplifier {

	private static Logger logger = LoggerFactory.getLogger(MetadataTriplifier.class);

	@Override
	public DatasetGraph triplify(URL url, Properties properties) throws IOException {
		DatasetGraph dg = DatasetGraphFactory.create();
		Graph g = GraphFactory.createGraphMem();

		String defaultNamespace = url.toString() + "/";
		String namespace = null;

		if (properties.contains(IRIArgument.NAMESPACE.toString())) {
			namespace = properties.getProperty(IRIArgument.NAMESPACE.toString());
			if (namespace.trim().length() == 0) {
				logger.warn("Unsupported parameter value for 'namespace', using default (no location+/).");
				namespace = defaultNamespace;
			}
		} else {
			namespace = defaultNamespace;
		}
		Node n = NodeFactory.createURI(url.toString());
		File f = new File(FilenameUtils.getName(url.getFile()));
		FileUtils.copyURLToFile(url, f);

		readBasicAttributes(f.toPath(), g, n, namespace);
		try {
			readMetadata(f, g, n, namespace);
		} catch (ImageProcessingException | IOException e) {
			e.printStackTrace();
		}

		dg.setDefaultGraph(g);

		f.delete();

		return dg;
	}

	private void readBasicAttributes(Path p, Graph g, Node n, String namespace) throws IOException {
		BasicFileAttributes attr = Files.readAttributes(p, BasicFileAttributes.class);

		g.add(new Triple(n, NodeFactory.createURI(namespace + "size"),
				NodeFactory.createLiteral(attr.size() + "", XSDDatatype.XSDinteger)));
	}

	private void readMetadata(File f, Graph g, Node n, String namespace) throws IOException, ImageProcessingException {

		Metadata metadata = ImageMetadataReader.readMetadata(f);

		for (Directory directory : metadata.getDirectories()) {
			for (Tag tag : directory.getTags()) {
				g.add(new Triple(n, NodeFactory.createURI(namespace + tag.getTagName()),
						NodeFactory.createLiteral(tag.getDescription())));
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
