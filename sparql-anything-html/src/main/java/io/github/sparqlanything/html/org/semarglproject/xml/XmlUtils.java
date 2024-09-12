/**
 * Copyright 2012-2013 the Semargl contributors. See AUTHORS for more details.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.sparqlanything.html.org.semarglproject.xml;

import org.xml.sax.Attributes;

import java.util.BitSet;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Lightweight XML utils for internal usage
 */
public final class XmlUtils {

    /**
     * XML identifier start char checker
     */
    public static final BitSet ID_START = new BitSet();

    /**
     * XML identifier char checker
     */
    public static final BitSet ID = new BitSet();

    /**
     * XML whitespace char checker
     */
    public static final BitSet WHITESPACE = new BitSet();

    /**
     * XML quote char checker
     */
    public static final BitSet QUOTE = new BitSet();

    /**
     * XML greater char checker
     */
    public static final BitSet GT = new BitSet();

    /**
     * XML right square bracket char checker
     */
    public static final BitSet RIGHT_SQ_BRACKET = new BitSet();

    /**
     * XML lang attribute name
     */
    public static final String XML_LANG = "xml:lang";

    /**
     * XML base attribute name
     */
    public static final String XML_BASE = "xml:base";

    /**
     * Lang attribute name
     */
    public static final String LANG = "lang";

    private static final String NC_NAME_START_CHAR = "A-Za-z_\u00C0-\u00D6\u00D8-\u00F6"
            + "\u00F8-\u02FF\u0370-\u037D\u037F-\u1FFF\u200C-\u200D\u2070-\u218F"
            + "\u2C00-\u2FEF\u3001-\uD7FF\uF900-\uFDCF\uFDF0-\uFFFD";
    // \u10000-\uEFFFF
    private static final String NC_NAME_CHAR = "-.0-9\u00B7\u0300-\u036F\u203F-\u2040";
    private static final Pattern XML_NAME_PATTERN = Pattern.compile("[" + NC_NAME_START_CHAR + "]"
            + "[" + NC_NAME_START_CHAR + NC_NAME_CHAR + "]*");

    private static final String ID_START_STR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz";
    private static final String ID_OTHER_STR = "-0123456789:";
    private static final String WHITESPACE_STR = " \t\r\n\f\u000B\u001C\u001D\u001E\u00A0\u2007\u202F";

    private static final BitSet ID_START_OR_GT = new BitSet();
    private static final BitSet ID_START_OR_EQUAL = new BitSet();
    private static final BitSet ID_OR_QUOTE_OR_APOS = new BitSet();
    private static final BitSet APOS = new BitSet();
    private static final BitSet LT = new BitSet();

    static {
        LT.set('<');
        GT.set('>');
        APOS.set('\'');
        QUOTE.set('\"');
        RIGHT_SQ_BRACKET.set(']');
        for (int i = 0; i < ID_START_STR.length(); i++) {
            char c = ID_START_STR.charAt(i);
            ID_START.set(c);
            ID_START_OR_EQUAL.set(c);
            ID_START_OR_GT.set(c);
            ID.set(c);
            ID_OR_QUOTE_OR_APOS.set(c);
        }
        for (int i = 0; i < ID_OTHER_STR.length(); i++) {
            char c = ID_OTHER_STR.charAt(i);
            ID.set(c);
            ID_OR_QUOTE_OR_APOS.set(c);
        }
        ID_START_OR_GT.set('>');
        ID_START_OR_EQUAL.set('=');
        ID_OR_QUOTE_OR_APOS.set('\'');
        ID_OR_QUOTE_OR_APOS.set('\"');
        for (int i = 0; i < WHITESPACE_STR.length(); i++) {
            char c = WHITESPACE_STR.charAt(i);
            WHITESPACE.set(c);
        }
    }

    private XmlUtils() {
    }


    /**
     * Checks if specified value is valid XML name
     * @param value value to check
     * @return true if value is valid XML name
     */
    public static boolean isValidNCName(String value) {
        return XML_NAME_PATTERN.matcher(value).matches();
    }

    /**
     * Serializes node open tag
     * @param nsUri node's NS URI
     * @param qname node's QName
     * @param nsMappings node's namespace mappings
     * @param attrs node's attributes
     * @param optimizeNs should unused namespaces be skipped
     * @return string representation of open tag
     */
    public static String serializeOpenTag(String nsUri, String qname, Map<String, String> nsMappings,
                                          Attributes attrs, boolean optimizeNs) {
        String result = "<" + qname;
        if (nsUri != null && nsUri.length() > 0) {
            int idx = Math.max(qname.indexOf(':'), 0);
            nsMappings.put(qname.substring(0, idx), nsUri);
        }
        for (int i = 0; i < attrs.getLength(); i++) {
            result += " " + attrs.getQName(i) + "=\"" + attrs.getValue(i) + "\"";
        }
        for (String key : nsMappings.keySet()) {
            if (optimizeNs && isPrefixIgnorable(key, qname, attrs)) {
                continue;
            }

            if (key.isEmpty()) {
                String value = nsMappings.get(key);
                result += " xmlns=\"" + value + "\"";
            } else {
                result += " xmlns:" + key + "=\"" + nsMappings.get(key) + "\"";
            }
        }
        result += ">";
        return result;
    }

    private static boolean isPrefixIgnorable(String key, String qname, Attributes attrs) {
        boolean usagesFound = key.isEmpty() && qname.indexOf(':') == -1 || key.length() > 0
                && qname.startsWith(key + ":");
        for (int i = 0; i < attrs.getLength(); i++) {
            String aqn = attrs.getQName(i);
            if (aqn.startsWith("xml")) {
                continue;
            }
            if (key.isEmpty() && aqn.indexOf(':') == -1 || key.length() > 0
                    && aqn.startsWith(key + ":")) {
                usagesFound = true;
                break;
            }
        }
        return !usagesFound;
    }

}
