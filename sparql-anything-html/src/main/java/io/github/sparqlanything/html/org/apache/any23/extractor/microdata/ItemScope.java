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

package io.github.sparqlanything.html.org.apache.any23.extractor.microdata;

import io.github.sparqlanything.html.org.apache.any23.extractor.microdata.Item;
import io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemProp;
import io.github.sparqlanything.html.org.apache.any23.rdf.RDFUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.rdf4j.common.net.ParsedIRI;
import org.eclipse.rdf4j.model.IRI;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * This class describes a <b>Microdata <i>itemscope</i></b>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Hans Brende (hansbrende@apache.org)
 */
public class ItemScope extends Item {

    /**
     * Map of properties and multi values.
     */
    private final Map<String, List<io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemProp>> properties;

    /**
     * <i>itemscope</i> DOM identifier in container document.
     */
    private final String id;

    /**
     * <i>itemscope</i> references.
     */
    private final String[] refs;

    /**
     * <i>itemscope</i> type.
     */
    private final List<IRI> type;

    /**
     * <i>itemscope</i> external identifier.
     */
    private final String itemId;

    /**
     * Constructor.
     *
     * @param xpath
     *            location of this <i>itemscope</i> within the container document.
     * @param itemProps
     *            list of properties bound to this <i>itemscope</i>.
     * @param id
     *            DOM identifier for this <i>itemscope</i>. Can be <code>null</code>.
     * @param refs
     *            list of item prop references connected to this <i>itemscope</i>. Can be <code>null</code>.
     * @param type
     *            <i>itemscope</i> type. Can be <code>null</code>.
     * @param itemId
     *            <i>itemscope</i> id. Can be <code>null</code>.
     */
    public ItemScope(String xpath, io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemProp[] itemProps, String id, String[] refs, String type, String itemId) {
        this(xpath, itemProps, id, refs, stringToSingletonIRI(type), itemId);
    }

    private static final Pattern looksLikeStartsWithHost = Pattern.compile("[^:/.]+(\\.[^:/.]+)+(:\\d+)?([/#?].*)?");

    static List<IRI> stringToSingletonIRI(String type) {
        if (StringUtils.isNotBlank(type)) {
            ParsedIRI iri = ParsedIRI.create(type.trim());
            if (StringUtils.isBlank(iri.getScheme())) {
                String host = iri.getHost();
                if (StringUtils.isNotBlank(host)) {
                    iri = new ParsedIRI("http", iri.getUserInfo(), host, iri.getPort(), iri.getPath(), iri.getQuery(),
                            iri.getFragment());
                } else {
                    String path = iri.getPath();
                    if (path != null && looksLikeStartsWithHost.matcher(path).matches()) {
                        iri = ParsedIRI.create("http://" + iri.toString());
                    }
                }
            }
            return Collections.singletonList(RDFUtils.iri(iri.toString()));
        } else {
            return Collections.emptyList();
        }
    }

    ItemScope(String xpath, io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemProp[] itemProps, String id, String[] refs, List<IRI> types, String itemId) {
        super(xpath);

        if (itemProps == null) {
            throw new NullPointerException("itemProps list cannot be null.");
        }

        this.type = types;
        this.id = id;
        this.refs = refs;
        this.itemId = itemId;

        final Map<String, List<io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemProp>> tmpProperties = new HashMap<>();
        for (io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemProp itemProp : itemProps) {
            final String propName = itemProp.getName();
            List<io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemProp> propList = tmpProperties.get(propName);
            if (propList == null) {
                propList = new ArrayList<>();
                tmpProperties.put(propName, propList);
            }
            propList.add(itemProp);
        }
        final Map<String, List<io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemProp>> properties = new HashMap<>();
        for (Map.Entry<String, List<io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemProp>> propertiesEntry : tmpProperties.entrySet()) {
            properties.put(propertiesEntry.getKey(),
                    // Collections.unmodifiableList( propertiesEntry.getValue() )
                    propertiesEntry.getValue());
        }
        // this.properties = Collections.unmodifiableMap(properties);
        this.properties = properties;
    }

    /**
     * @return map of declared properties, every property can have more than a value.
     */
    public Map<String, List<io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemProp>> getProperties() {
        return properties;
    }

    /**
     * @return the <i>itemscope</i>
     */
    public String getId() {
        return id;
    }

    /**
     * @return <i>itemscope</i> list of references to <i>itemprop</i>s.
     */
    public String[] getRefs() {
        return refs;
    }

    /**
     * @return <i>itemscope</i> type.
     */
    public URL getType() {
        // No longer using URL.
        // But for backwards compatibility:
        try {
            return type.isEmpty() ? null : new URL(type.get(0).stringValue());
        } catch (MalformedURLException e) {
            try {
                return new URL(ParsedIRI.create(type.get(0).stringValue()).toASCIIString());
            } catch (Exception e1) {
                return null;
            }
        }
    }

    List<IRI> getTypes() {
        return type;
    }

    /**
     * @return <i>itemscope</i> public identifier.
     */
    public String getItemId() {
        return itemId;
    }

    @Override
    public String toJSON() {
        StringBuilder sb = new StringBuilder();
        int i;
        int j;
        final Collection<List<io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemProp>> itemPropsList = properties.values();
        j = 0;
        for (List<io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemProp> itemProps : itemPropsList) {
            i = 0;
            for (io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemProp itemProp : itemProps) {
                sb.append(itemProp);
                if (i < itemProps.size() - 1) {
                    sb.append(", ");
                }
                i++;
            }
            if (j < itemPropsList.size() - 1) {
                sb.append(", ");
            }
            j++;
        }
        return String.format(Locale.ROOT, "{ "
                + "\"xpath\" : \"%s\", \"id\" : %s, \"refs\" : %s, \"type\" : %s, \"itemid\" : %s, \"properties\" : [ %s ]"
                + " }", getXpath(), id == null ? null : "\"" + id + "\"", refs == null ? null : toJSON(refs),
                type.isEmpty() ? null : "\"" + type.get(0) + "\"", itemId == null ? null : "\"" + itemId + "\"",
                sb.toString());
    }

    @Override
    public String toString() {
        return toJSON();
    }

    @Override
    public int hashCode() {
        int i = properties == null ? 0 : properties.hashCode();
        i += id == null ? 0 : id.hashCode();
        i += refs == null ? 0 : Arrays.hashCode(refs);
        i += type == null ? 0 : type.hashCode();
        i += itemId == null ? 0 : itemId.hashCode();
        return i;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof ItemScope) {
            final ItemScope other = (ItemScope) obj;
            return super.getXpath().equals(other.getXpath())
                    && (properties == null ? other.properties == null : properties.equals(other.properties))
                    && (id == null ? other.id == null : id.equals(other.id))
                    && (refs == null ? other.refs == null : Arrays.equals(refs, other.refs))
                    && (type == null ? other.type == null : type.equals(other.type))
                    && (itemId == null ? other.itemId == null : itemId.equals(other.itemId));
        }
        return false;
    }

    protected void acquireProperty(io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemProp itemProp) {
        List<io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemProp> itemProps = properties.computeIfAbsent(itemProp.getName(), k -> new ArrayList<>());
        if (!itemProps.contains(itemProp))
            itemProps.add(itemProp);
    }

    protected void disownProperty(io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemProp itemProp) {
        List<ItemProp> propList = properties.get(itemProp.getName());
        if (propList != null)
            propList.remove(itemProp);
    }

    private String toJSON(String[] in) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < in.length; i++) {
            sb.append("\"");
            sb.append(in[i]);
            sb.append("\"");
            if (i < in.length - 1) {
                sb.append(", ");
            }
        }
        sb.append(']');
        return sb.toString();
    }

}
