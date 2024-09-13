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

/**
 * Base class for <b>Microdata</b> items.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public abstract class Item {

    /**
     * Xpath item location in container document.
     */
    private final String xpath;

    /**
     * Constructor.
     *
     * @param xpath
     *            xpath to this item in container document.
     */
    public Item(String xpath) {
        if (xpath == null) {
            throw new NullPointerException("xpath cannot be null.");
        }
        this.xpath = xpath;
    }

    /**
     * @return the <b>JSON</b> representation for this item.
     */
    public abstract String toJSON();

    /**
     * @return the item location in container document.
     */
    public String getXpath() {
        return xpath;
    }

    @Override
    public int hashCode() {
        return xpath.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof Item) {
            final Item other = (Item) obj;
            return xpath.equals(other.xpath);
        }
        return false;
    }
}
