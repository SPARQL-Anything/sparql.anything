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

package io.github.sparqlanything.html.org.apache.any23.rdf;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * A mapping from prefixes to namespace IRIs. Supports "volatile mappings", which will be overwritten without notice
 * when mappings are merged, while for normal mappings this causes an exception. This allows combining "hard" mappings
 * (which must be retained or something breaks) and "soft" mappings (which might be read from input RDF files and should
 * be retained only if they are not in conflict with the hard ones).
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class Prefixes {

    public static Prefixes create1(String prefix, String namespaceIRI) {
        Prefixes result = new Prefixes();
        result.add(prefix, namespaceIRI);
        return result;
    }

    public static Prefixes createFromMap(Map<String, String> prefixesToNamespaceIRIs, boolean areVolatile) {
        Prefixes result = new Prefixes();
        for (Entry<String, String> entry : prefixesToNamespaceIRIs.entrySet()) {
            if (areVolatile) {
                result.addVolatile(entry.getKey(), entry.getValue());
            } else {
                result.add(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    public static Prefixes EMPTY = new Prefixes(Collections.<String, String> emptyMap());

    private final Map<String, String> mappings;
    private final Set<String> volatilePrefixes = new HashSet<String>();

    public Prefixes() {
        this(new HashMap<String, String>());
    }

    public Prefixes(Prefixes initial) {
        this();
        add(initial);
    }

    private Prefixes(Map<String, String> mappings) {
        this.mappings = mappings;
    }

    public IRI expand(String curie) {
        String prefix = parsePrefix(curie);
        if (prefix == null || !hasPrefix(prefix)) {
            return null;
        }
        return SimpleValueFactory.getInstance().createIRI(getNamespaceIRIFor(prefix) + parseLocalName(curie));
    }

    public String abbreviate(String uri) {
        for (Entry<String, String> namespace : mappings.entrySet()) {
            if (uri.startsWith(namespace.getValue())) {
                return namespace.getKey() + ":" + uri.substring(namespace.getValue().length());
            }
        }
        return null;
    }

    public boolean canExpand(String curie) {
        String prefix = parsePrefix(curie);
        return prefix != null && hasPrefix(prefix);
    }

    public boolean canAbbreviate(String uri) {
        for (Entry<String, String> namespace : mappings.entrySet()) {
            if (uri.startsWith(namespace.getValue())) {
                return true;
            }
        }
        return false;
    }

    public String getNamespaceIRIFor(String prefix) {
        return mappings.get(prefix);
    }

    public boolean hasNamespaceIRI(String uri) {
        return mappings.containsValue(uri);
    }

    public boolean hasPrefix(String prefix) {
        return mappings.containsKey(prefix);
    }

    public Set<String> allPrefixes() {
        return mappings.keySet();
    }

    public boolean isEmpty() {
        return mappings.isEmpty();
    }

    public void add(String prefix, String namespaceIRI) {
        if (isVolatile(prefix)) {
            volatilePrefixes.remove(prefix);
        } else {
            if (hasPrefix(prefix)) {
                if (getNamespaceIRIFor(prefix).equals(namespaceIRI)) {
                    return; // re-assigned same prefix to same IRI, let's just ignore it
                }
                throw new IllegalStateException("Attempted to re-assign prefix '" + prefix + "'; clashing values '"
                        + getNamespaceIRIFor(prefix) + "' and '" + namespaceIRI);
            }
        }
        mappings.put(prefix, namespaceIRI);
    }

    public void add(Prefixes other) {
        for (String otherPrefix : other.allPrefixes()) {
            if (other.isVolatile(otherPrefix)) {
                addVolatile(otherPrefix, other.getNamespaceIRIFor(otherPrefix));
            } else {
                add(otherPrefix, other.getNamespaceIRIFor(otherPrefix));
            }
        }
    }

    public void removePrefix(String prefix) {
        mappings.remove(prefix);
        volatilePrefixes.remove(prefix);
    }

    public Prefixes createSubset(String... prefixes) {
        Prefixes result = new Prefixes();
        for (String prefix : prefixes) {
            if (!hasPrefix(prefix)) {
                throw new IllegalArgumentException("No namespace IRI declared for prefix " + prefix);
            }
            result.add(prefix, getNamespaceIRIFor(prefix));
        }
        return result;
    }

    public void addVolatile(String prefix, String namespaceIRI) {
        if (hasPrefix(prefix)) {
            return; // new prefix is volatile, so we don't overwrite the old one
        }
        mappings.put(prefix, namespaceIRI);
        volatilePrefixes.add(prefix);
    }

    public void addVolatile(Prefixes other) {
        for (String otherPrefix : other.allPrefixes()) {
            addVolatile(otherPrefix, other.getNamespaceIRIFor(otherPrefix));
        }
    }

    public boolean isVolatile(String prefix) {
        return volatilePrefixes.contains(prefix);
    }

    private Map<String, String> mapUnmodifiable = null;

    public Map<String, String> asMap() {
        // Optimization: Create the unmodifiable map only once, lazily
        if (mapUnmodifiable == null) {
            mapUnmodifiable = Collections.unmodifiableMap(mappings);
        }
        return mapUnmodifiable;
    }

    private String parsePrefix(String curie) {
        int index = curie.indexOf(':');
        if (index == -1) {
            throw new IllegalArgumentException("Not a CURIE: '" + curie + "'");
        }
        return curie.substring(0, index);
    }

    private String parseLocalName(String curie) {
        int index = curie.indexOf(':');
        if (index == -1) {
            throw new IllegalArgumentException("Not a CURIE: '" + curie + "'");
        }
        return curie.substring(index + 1);
    }

}
