package io.github.sparqlanything.model;

import org.apache.jena.graph.Node;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import java.util.Properties;

public class FacadeXAbstractNodeBuilder implements FacadeXNodeBuilder {
	protected final Properties properties;
	protected final boolean p_blank_nodes;
	protected final String p_namespace;
	protected final String p_root;
	protected final boolean p_trim_strings;
	protected final String p_null_string;
	protected final boolean p_use_rdfs_member;
	protected final boolean p_reify_slot_statements;
	protected final boolean p_generate_predicate_labels;

	public FacadeXAbstractNodeBuilder(Properties properties) {
		this.properties = properties;
		this.p_blank_nodes = PropertyUtils.getBooleanProperty(properties, IRIArgument.BLANK_NODES);
		this.p_namespace = PropertyUtils.getStringProperty(properties, IRIArgument.NAMESPACE);
		this.p_root = Triplifier.getRootArgument(properties);
		this.p_trim_strings = PropertyUtils.getBooleanProperty(properties, IRIArgument.TRIM_STRINGS);
		this.p_null_string = PropertyUtils.getStringProperty(properties, IRIArgument.NULL_STRING);
		this.p_use_rdfs_member = PropertyUtils.getBooleanProperty(properties, IRIArgument.USE_RDFS_MEMBER);
		this.p_reify_slot_statements = PropertyUtils.getBooleanProperty(properties, IRIArgument.ANNOTATE_TRIPLES_WITH_SLOT_KEYS);
		this.p_generate_predicate_labels = PropertyUtils.getBooleanProperty(properties, IRIArgument.GENERATE_PREDICATE_LABELS);
	}


	public Node container2node(String containerId, String dataSourceId) {
		if (p_blank_nodes) {
			return container2BlankNode(containerId);
		} else {
			return container2URI(containerId, dataSourceId);
		}
	}
	public String getRootURI(String dataSourceId) {
		return p_root.concat(dataSourceId);
	}

	public String getNamespace() {
		return p_namespace;
	}

	public Node value2node(Object value) {
		// trims_strings == true and if object is string, trim it
		if (p_trim_strings && value instanceof String) {
			value = ((String) value).trim();
		}
		return FacadeXNodeBuilder.super.value2node(value);
	}

	public Node createIntegerKeyNode(int slotKey){
		return p_use_rdfs_member ? RDFS.member.asNode() : RDF.li(slotKey).asNode();
	}

	public Node createObjectNode(String dataSourceId, Object object, boolean isObjectContainer){
		return isObjectContainer ? container2node(object.toString(), dataSourceId) : value2node(object);
	}

}
