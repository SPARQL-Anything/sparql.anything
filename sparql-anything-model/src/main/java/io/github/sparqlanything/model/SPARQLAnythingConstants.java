package io.github.sparqlanything.model;

import org.apache.jena.assembler.JA;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.vocabulary.RSS;
import org.apache.jena.vocabulary.VCARD;

public class SPARQLAnythingConstants {
	public static final PrefixMapping PREFIXES = PrefixMapping.Factory.create().setNsPrefixes(PrefixMapping.Extended).setNsPrefix("xhtml","http://www.w3.org/1999/xhtml#").setNsPrefix("whatwg", "https://html.spec.whatwg.org/#").setNsPrefix("rss", RSS.getURI()).setNsPrefix("vcard", VCARD.getURI()).setNsPrefix("ja", JA.getURI()).setNsPrefix("eg", "http://www.example.org/").lock();
}
