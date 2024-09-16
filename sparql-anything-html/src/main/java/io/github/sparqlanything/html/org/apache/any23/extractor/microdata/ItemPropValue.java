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

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import io.github.sparqlanything.html.org.apache.any23.util.StringUtils;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.vocabulary.XSD;

/**
 * Describes a possible value for a <b>Microdata item property</b>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ItemPropValue {

    /**
     * Internal content value.
     */
    private final Object content;

    /**
     * Content type.
     */
    private final Type type;

    private static final ThreadLocal<SimpleDateFormat> sdf = new ThreadLocal<>();

    /**
     * Supported types.
     */
    public enum Type {
        Plain(String.class), Link(String.class), Date(Date.class), Nested(ItemScope.class);

        Type(Class<?> contentClass) {
            this.contentClass = contentClass;
        }

        private final Class<?> contentClass;

        private Object checkClass(Object content) {
            Objects.requireNonNull(content, "content cannot be null");
            if (!contentClass.isInstance(content)) {
                throw new IllegalArgumentException(
                        "content must be a " + contentClass.getName() + " when type is " + this);
            }
            return content;
        }
    }

    public static Date parseDateTime(String dateStr) throws ParseException {
        return getSdf().parse(dateStr);
    }

    public static String formatDateTime(Date in) {
        return getSdf().format(in);
    }

    private static SimpleDateFormat getSdf() {
        SimpleDateFormat simpleDateFormat = sdf.get();
        if (simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
            sdf.set(simpleDateFormat);
        }
        return simpleDateFormat;
    }

    /**
     * Constructor.
     *
     * @param content
     *            content object.
     * @param type
     *            content type.
     */
    public ItemPropValue(Object content, Type type) {
        this.type = Objects.requireNonNull(type, "type cannot be null");
        this.content = type.checkClass(content);
        this.literal = null;
    }

    ItemPropValue(Literal literal) {
        this.literal = literal;

        Type type;
        Object content;

        // for backwards compatibility:
        if (XSD.DATE.equals(literal.getDatatype()) || XSD.DATETIME.equals(literal.getDatatype())) {
            try {
                content = parseDateTime(literal.getLabel());
                type = Type.Date;
            } catch (Exception e) {
                content = literal.getLabel();
                type = Type.Plain;
            }
        } else {
            content = literal.getLabel();
            type = Type.Plain;
        }
        this.type = type;
        this.content = content;
    }

    final Literal literal;

    /**
     * @return the content object.
     */
    public Object getContent() {
        return content;
    }

    /**
     * @return the content type.
     */
    public Type getType() {
        return type;
    }

    /**
     * @return <code>true</code> if type is plain text.
     */
    public boolean isPlain() {
        return type == Type.Plain;
    }

    /**
     * @return <code>true</code> if type is a link.
     */
    public boolean isLink() {
        return type == Type.Link;
    }

    /**
     * @return <code>true</code> if type is a date.
     */
    public boolean isDate() {
        return type == Type.Date;
    }

    /**
     * @return <code>true</code> if type is a nested {@link ItemScope}.
     */
    public boolean isNested() {
        return type == Type.Nested;
    }

    /**
     * @return <code>true</code> if type is an integer.
     */
    public boolean isInteger() {
        if (type != Type.Plain)
            return false;
        try {
            Integer.parseInt((String) content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @return <code>true</code> if type is a float.
     */
    public boolean isFloat() {
        if (type != Type.Plain)
            return false;
        try {
            Float.parseFloat((String) content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @return <code>true</code> if type is a number.
     */
    public boolean isNumber() {
        return isInteger() || isFloat();
    }

    /**
     * @return the content value as integer, or raises an exception.
     *
     * @throws NumberFormatException
     *             if the content is not an integer.
     * @throws ClassCastException
     *             if content is not plain.
     */
    public int getAsInteger() {
        return Integer.parseInt((String) content);
    }

    /**
     * @return the content value as float, or raises an exception.
     *
     * @throws NumberFormatException
     *             if the content is not an float.
     * @throws ClassCastException
     *             if content is not plain.
     */
    public float getAsFloat() {
        return Float.parseFloat((String) content);
    }

    /**
     * @return the content as {@link Date} if <code>type == Type.DateTime</code>,
     *
     * @throws ClassCastException
     *             if content is not a valid date.
     */
    public Date getAsDate() {
        return (Date) content;
    }

    /**
     * @return the content value as URL, or raises an exception.
     */
    public URL getAsLink() {
        try {
            return new URL((String) content);
        } catch (MalformedURLException murle) {
            throw new IllegalStateException("Error while parsing IRI.", murle);
        }
    }

    /**
     * @return the content value as {@link ItemScope}.
     */
    public ItemScope getAsNested() {
        return (ItemScope) content;
    }

    public String toJSON() {
        String contentStr;
        if (content instanceof String) {
            contentStr = "\"" + StringUtils.escapeAsJSONString((String) content) + "\"";
        } else if (content instanceof Date) {
            contentStr = "\"" + getSdf().format((Date) content) + "\"";
        } else {
            contentStr = content.toString();
        }

        return String.format(Locale.ROOT, "{ \"content\" : %s, \"type\" : \"%s\" }", contentStr, type);
    }

    @Override
    public String toString() {
        return toJSON();
    }

    @Override
    public int hashCode() {
        return content.hashCode() * type.hashCode() * 2;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof ItemPropValue) {
            final ItemPropValue other = (ItemPropValue) obj;
            return content.equals(other.content) && type.equals(other.type);
        }
        return false;
    }
}
