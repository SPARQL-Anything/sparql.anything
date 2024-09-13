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

package io.github.sparqlanything.html.org.apache.any23.vocab;

import org.eclipse.rdf4j.model.IRI;

import java.util.HashMap;
import java.util.Map;

/**
 * Vocabulary class for <a href="http://gmpg.org/xfn/11">XFN</a>, as per
 * <a href="http://vocab.sindice.com/xfn/guide.html">Expressing XFN in RDF</a>.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class XFN extends Vocabulary {

    public static final String NS = "http://vocab.sindice.com/xfn#";

    private static XFN instance;

    public static XFN getInstance() {
        if (instance == null) {
            instance = new XFN();
        }
        return instance;
    }

    public final IRI contact = createProperty("contact");
    public final IRI acquaintance = createProperty("acquaintance");
    public final IRI friend = createProperty("friend");
    public final IRI met = createProperty("met");
    public final IRI coWorker = createProperty("co-worker");
    public final IRI colleague = createProperty("colleague");
    public final IRI coResident = createProperty("co-resident");
    public final IRI neighbor = createProperty("neighbor");
    public final IRI child = createProperty("child");
    public final IRI parent = createProperty("parent");
    public final IRI spouse = createProperty("spouse");
    public final IRI kin = createProperty("kin");
    public final IRI muse = createProperty("muse");
    public final IRI crush = createProperty("crush");
    public final IRI date = createProperty("date");
    public final IRI sweetheart = createProperty("sweetheart");
    public final IRI me = createProperty("me");

    public final IRI mePage = createProperty(NS, "mePage");

    private Map<String, IRI> PeopleXFNProperties;

    private Map<String, IRI> HyperlinkXFNProperties;

    public IRI getPropertyByLocalName(String localName) {
        return PeopleXFNProperties.get(localName);
    }

    public IRI getExtendedProperty(String localName) {
        return HyperlinkXFNProperties.get(localName);
    }

    public boolean isXFNLocalName(String localName) {
        return PeopleXFNProperties.containsKey(localName);
    }

    public boolean isExtendedXFNLocalName(String localName) {
        return PeopleXFNProperties.containsKey(localName);
    }

    private IRI createProperty(String localName) {
        if (HyperlinkXFNProperties == null) {
            HyperlinkXFNProperties = new HashMap<String, IRI>();
        }
        if (PeopleXFNProperties == null) {
            PeopleXFNProperties = new HashMap<String, IRI>();
        }

        IRI result = createProperty(NS, localName + "-hyperlink");
        HyperlinkXFNProperties.put(localName, result);

        result = createProperty(NS, localName);
        PeopleXFNProperties.put(localName, result);
        return result;
    }

    private XFN() {
        super(NS);
    }

}
