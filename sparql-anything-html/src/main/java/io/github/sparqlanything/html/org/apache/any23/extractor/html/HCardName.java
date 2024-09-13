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

package io.github.sparqlanything.html.org.apache.any23.extractor.html;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.sparqlanything.html.org.apache.any23.extractor.html.HTMLDocument.TextField;

/**
 * An HCard name, consisting of various parts. Handles computation of full names from first and last names, and similar
 * computations.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class HCardName {

    public static final String GIVEN_NAME = "given-name";
    public static final String FAMILY_NAME = "family-name";
    public static final String ADDITIONAL_NAME = "additional-name";
    public static final String NICKNAME = "nickname";
    public static final String HONORIFIC_PREFIX = "honorific-prefix";
    public static final String HONORIFIC_SUFFIX = "honorific-suffix";

    public static final String[] FIELDS = { GIVEN_NAME, FAMILY_NAME, ADDITIONAL_NAME, NICKNAME, HONORIFIC_PREFIX,
            HONORIFIC_SUFFIX };

    private static final String[] NAME_COMPONENTS = { HONORIFIC_PREFIX, GIVEN_NAME, ADDITIONAL_NAME, FAMILY_NAME,
            HONORIFIC_SUFFIX };

    private Map<String, FieldValue> fields = new HashMap<String, FieldValue>();
    private TextField[] fullName = null;
    private TextField organization = null;
    private TextField unit = null;

    private static TextField join(TextField[] sarray, String delimiter) {
        StringBuilder builder = new StringBuilder();
        final int sarrayLengthMin2 = sarray.length - 1;
        for (int i = 0; i < sarray.length; i++) {
            builder.append(sarray[i].value());
            if (i < sarrayLengthMin2) {
                builder.append(delimiter);
            }
        }
        return new TextField(builder.toString(), sarray[0].source());
    }

    /**
     * Resets the content of the HName fields.
     */
    public void reset() {
        fields.clear();
        fullName = null;
        organization = null;
        unit = null;
    }

    public void setField(String fieldName, TextField nd) {
        final String value = fixWhiteSpace(nd.value());
        if (value == null)
            return;
        FieldValue fieldValue = fields.get(fieldName);
        if (fieldValue == null) {
            fieldValue = new FieldValue();
            fields.put(fieldName, fieldValue);
        }
        fieldValue.addValue(new TextField(value, nd.source()));
    }

    public void setFullName(TextField nd) {
        final String value = fixWhiteSpace(nd.value());
        if (value == null)
            return;
        String[] split = value.split("\\s+");
        // Supporting case: ['King,', 'Ryan'] that is converted to ['Ryan', 'King'] .
        final String split0 = split[0];
        final int split0Length = split0.length();
        if (split.length > 1 && split0.charAt(split0Length - 1) == ',') {
            String swap = split[1];
            split[1] = split0.substring(0, split0Length - 1);
            split[0] = swap;
        }
        TextField[] splitFields = new TextField[split.length];
        for (int i = 0; i < split.length; i++) {
            splitFields[i] = new TextField(split[i], nd.source());
        }
        this.fullName = splitFields;
    }

    public void setOrganization(TextField nd) {
        final String value = fixWhiteSpace(nd.value());
        if (value == null)
            return;
        this.organization = new TextField(value, nd.source());
    }

    public boolean isMultiField(String fieldName) {
        FieldValue fieldValue = fields.get(fieldName);
        return fieldValue != null && fieldValue.isMultiField();
    }

    public boolean containsField(String fieldName) {
        return GIVEN_NAME.equals(fieldName) || FAMILY_NAME.equals(fieldName) || fields.containsKey(fieldName);
    }

    public TextField getField(String fieldName) {
        if (GIVEN_NAME.equals(fieldName)) {
            return getFullNamePart(GIVEN_NAME, 0);
        }
        if (FAMILY_NAME.equals(fieldName)) {
            return getFullNamePart(FAMILY_NAME, Integer.MAX_VALUE);
        }
        FieldValue v = fields.get(fieldName);
        return v == null ? null : v.getValue();
    }

    public Collection<TextField> getFields(String fieldName) {
        FieldValue v = fields.get(fieldName);
        return v == null ? Collections.<TextField> emptyList() : v.getValues();
    }

    private TextField getFullNamePart(String fieldName, int index) {
        if (fields.containsKey(fieldName)) {
            return fields.get(fieldName).getValue();
        }
        if (fullName == null)
            return null;
        // If org and fn are the same, the hCard is for an organization, and we do not split the fn
        if (organization != null && fullName[0].value().equals(organization.value())) {
            return null;
        }
        if (index != Integer.MAX_VALUE && fullName.length <= index)
            return null;
        return fullName[index == Integer.MAX_VALUE ? fullName.length - 1 : index];
    }

    public boolean hasField(String fieldName) {
        return getField(fieldName) != null;
    }

    public boolean hasAnyField() {
        for (String fieldName : FIELDS) {
            if (hasField(fieldName))
                return true;
        }
        return false;
    }

    public TextField getFullName() {
        if (fullName != null)
            return join(fullName, " ");
        StringBuffer s = new StringBuffer();
        boolean empty = true;
        Node first = null;
        TextField current;
        for (String fieldName : NAME_COMPONENTS) {
            if (!hasField(fieldName))
                continue;
            if (!empty) {
                s.append(' ');
            }
            current = getField(fieldName);
            if (first == null) {
                first = current.source();
            }
            s.append(current.value());
            empty = false;
        }
        if (empty)
            return null;
        return new TextField(s.toString(), first);
    }

    public TextField getOrganization() {
        return organization;
    }

    public void setOrganizationUnit(TextField nd) {
        final String value = fixWhiteSpace(nd.value());
        if (value == null)
            return;
        this.unit = new TextField(value, nd.source());
    }

    public TextField getOrganizationUnit() {
        return unit;
    }

    private String fixWhiteSpace(String s) {
        if (s == null)
            return null;
        s = s.trim().replaceAll("\\s+", " ");
        if ("".equals(s))
            return null;
        return s;
    }

    /**
     * Represents a possible field value.
     */
    private static class FieldValue {

        private TextField value;
        private List<TextField> multiValue = new ArrayList<TextField>();

        FieldValue() {
        }

        void addValue(TextField v) {
            if (value == null && multiValue == null) {
                value = v;
            } else if (multiValue == null) {
                multiValue = new ArrayList<TextField>();
                multiValue.add(value);
                value = null;
                multiValue.add(v);
            } else {
                multiValue.add(v);
            }
        }

        boolean isMultiField() {
            return value == null;
        }

        TextField getValue() {
            return value != null ? value : multiValue.get(0);
        }

        Collection<TextField> getValues() {
            return value != null ? Arrays.asList(value) : multiValue;
        }
    }

}
