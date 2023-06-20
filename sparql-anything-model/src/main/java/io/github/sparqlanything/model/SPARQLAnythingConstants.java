package io.github.sparqlanything.model;

import org.apache.jena.shared.PrefixMapping;

public class SPARQLAnythingConstants {
	public static final PrefixMapping PREFIXES = PrefixMapping.Factory.create().setNsPrefixes(PrefixMapping.Extended).setNsPrefix("xhtml","http://www.w3.org/1999/xhtml#").setNsPrefix("whatwg", "https://html.spec.whatwg.org/#").setNsPrefix("fx", Triplifier.FACADE_X_CONST_NAMESPACE_IRI).setNsPrefix("xyz", Triplifier.XYZ_NS).lock();
}
