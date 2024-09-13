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
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Base class for the definition of a vocabulary.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 *
 * @version $Id$
 */
public abstract class Vocabulary {

    /**
     * Allows to add comments to <code>namespaces</code>, <code>classes</code> and <code>properties</code>.
     */
    @Target({ FIELD })
    @Retention(RUNTIME)
    @interface Comment {
        String value();
    }

    /**
     * Vocabulary namespace.
     */
    private final IRI namespace;

    /**
     * Map of vocabulary resources.
     */
    private Map<String, IRI> classes;

    /**
     * Map of vocabulary properties.
     */
    private Map<String, IRI> properties;

    /**
     * Map any resource with the relative comment.
     */
    private Map<IRI, String> resourceToCommentMap;

    /**
     * Overloaded Constructor.
     *
     * @param namespace
     *            the namespace IRI prefix.
     */
    public Vocabulary(String namespace) {
        try {
            this.namespace = SimpleValueFactory.getInstance().createIRI(namespace);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid namespace '" + namespace + "'", e);
        }
    }

    /**
     * @return the namespace associated to this vocabulary.
     */
    public IRI getNamespace() {
        return this.namespace;
    }

    /**
     * Returns a class defined within this vocabulary.
     *
     * @param name
     *            class name.
     *
     * @return the IRI associated to such resource.
     */
    public IRI getClass(String name) {
        IRI res = this.classes.get(name);
        if (null == res) {
            throw new IllegalArgumentException("Unknown resource name '" + name + "'");
        }
        return res;
    }

    /**
     * Returns a property defined within this vocabulary.
     *
     * @param name
     *            property name.
     *
     * @return the IRI associated to such property.
     */
    public IRI getProperty(String name) {
        IRI prop = this.properties.get(name);
        if (null == prop) {
            throw new IllegalArgumentException("Unknown property name '" + name + "'");
        }
        return prop;
    }

    /**
     * Returns a property defined within this vocabulary, if not found the <code>defaultValue</code> will be returned.
     *
     * @param name
     *            property name.
     * @param defaultValue
     *            the default value if property name not found.
     *
     * @return the IRI associated to such property.
     */
    public IRI getProperty(String name, IRI defaultValue) {
        IRI prop = this.properties.get(name);
        if (null == prop) {
            return defaultValue;
        }
        return prop;
    }

    /**
     * Returns the property IRI for the specified property string. If the string contains a list of words separated by
     * blank chars, such words are merged and camel case separated.
     *
     * @param property
     *            property name.
     *
     * @return property IRI.
     */
    public IRI getPropertyCamelCase(String property) {
        String[] names = property.split("\\W");
        String camelCase = names[0];
        for (int i = 1; i < names.length; i++) {
            String tmp = names[i];
            camelCase += tmp.replaceFirst("(.)", tmp.substring(0, 1).toUpperCase(java.util.Locale.ROOT));
        }
        return getProperty(camelCase);
    }

    /**
     * @return the list of all defined classes.
     */
    public IRI[] getClasses() {
        if (this.classes == null) {
            return new IRI[0];
        }
        final Collection<IRI> iris = this.classes.values();
        return iris.toArray(new IRI[iris.size()]);
    }

    /**
     * @return the list of all defined properties.
     */
    public IRI[] getProperties() {
        if (this.properties == null) {
            return new IRI[0];
        }
        final Collection<IRI> iris = this.properties.values();
        return iris.toArray(new IRI[iris.size()]);
    }

    /**
     * Returns all the defined comments for resources.
     *
     * @return unmodifiable list of comments.
     */
    public Map<IRI, String> getComments() {
        fillResourceToCommentMap();
        return Collections.unmodifiableMap(this.resourceToCommentMap);
    }

    /**
     * Returns the comment for the given resource.
     *
     * @param resource
     *            input resource to have a comment.
     *
     * @return the human readable comment associated to the given resource.
     */
    public String getCommentFor(IRI resource) {
        fillResourceToCommentMap();
        return this.resourceToCommentMap.get(resource);
    }

    /**
     * Creates a IRI.
     *
     * @param iriStr
     *            the IRI string
     *
     * @return the IRI instance.
     */
    protected static IRI createIRI(String iriStr) {
        return SimpleValueFactory.getInstance().createIRI(iriStr);
    }

    /**
     * Creates a resource and register it to the {@link #classes} map.
     *
     * @param namespace
     *            vocabulary namespace.
     * @param resource
     *            name of the resource.
     *
     * @return the created resource IRI.
     */
    protected IRI createClass(String namespace, String resource) {
        IRI res = createIRI(namespace, resource);
        if (this.classes == null) {
            this.classes = new HashMap<>(10);
        }
        this.classes.put(resource, res);
        return res;
    }

    /**
     * Creates a property and register it to the {@link #properties} map.
     *
     * @param namespace
     *            vocabulary namespace.
     * @param property
     *            name of the property.
     *
     * @return the created property IRI.
     */
    protected IRI createProperty(String namespace, String property) {
        IRI res = createIRI(namespace, property);
        if (this.properties == null) {
            this.properties = new HashMap<>(10);
        }
        this.properties.put(property, res);
        return res;
    }

    /**
     * Creates a IRI.
     *
     * @param namespace
     * @param localName
     *
     * @return
     */
    private static IRI createIRI(String namespace, String localName) {
        return SimpleValueFactory.getInstance().createIRI(namespace, localName);
    }

    private void fillResourceToCommentMap() {
        if (this.resourceToCommentMap != null)
            return;
        final Map<IRI, String> newMap = new HashMap<>();
        for (Field field : this.getClass().getFields()) {
            try {
                final Object value = field.get(this);
                if (value instanceof IRI) {
                    final Comment comment = field.getAnnotation(Comment.class);
                    if (comment != null)
                        newMap.put((IRI) value, comment.value());
                }
            } catch (IllegalAccessException iae) {
                throw new RuntimeException("Error while creating resource to comment map.", iae);
            }
        }
        this.resourceToCommentMap = newMap;
    }

}
