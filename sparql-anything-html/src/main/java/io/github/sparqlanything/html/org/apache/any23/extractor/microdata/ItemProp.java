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
import io.github.sparqlanything.html.org.apache.any23.extractor.microdata.ItemPropValue;

import java.util.Locale;

/**
 * Describes a <b>Microdata item property</b>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ItemProp extends Item {

    /**
     * Property name.
     */
    private final String name;

    /**
     * Property value.
     */
    private final ItemPropValue value;

    /**
     * Constructor.
     *
     * @param xpath
     *            item location in container document.
     * @param name
     *            item property name.
     * @param value
     *            item property value.
     */
    public ItemProp(String xpath, String name, ItemPropValue value) {
        this(xpath, name, value, false);
    }

    final boolean reverse;

    ItemProp(String xpath, String name, ItemPropValue value, boolean reverse) {
        super(xpath);

        if (name == null) {
            throw new NullPointerException("name cannot be null.");
        }
        if (name.trim().length() == 0) {
            throw new IllegalArgumentException("invalid property name '" + name + "'");
        }
        if (value == null) {
            throw new NullPointerException("value cannot be null.");
        }
        this.name = name;
        this.value = value;
        this.reverse = reverse;
    }

    /**
     * @return the item property name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return the item property value.
     */
    public ItemPropValue getValue() {
        return value;
    }

    @Override
    public String toJSON() {
        return String.format(Locale.ROOT, "{ \"xpath\" : \"%s\", \"name\" : \"%s\", \"value\" : %s }", getXpath(), name,
                value.toJSON());
    }

    @Override
    public String toString() {
        return toJSON();
    }

    @Override
    public int hashCode() {
        return name.hashCode() * value.hashCode() * 3;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof ItemProp) {
            final ItemProp other = (ItemProp) obj;
            return name.equals(other.name) && value.equals(other.value);
        }
        return false;
    }
}
