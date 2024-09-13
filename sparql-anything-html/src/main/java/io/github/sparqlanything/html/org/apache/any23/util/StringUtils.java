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
package io.github.sparqlanything.html.org.apache.any23.util;

import java.util.Locale;

/**
 * This class provides a set of string utility methods.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class StringUtils {

    /**
     * Joins the given input sting <code>data</code> list using the specified <code>delimiter</code>.
     *
     * @param delimiter
     *            string delimiter.
     * @param data
     *            list of data to be joined.
     *
     * @return the joined string.
     */
    public static String join(String delimiter, String... data) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            sb.append(data[i]);
            if (i >= data.length - 1) {
                break;
            }
            sb.append(delimiter);
        }
        return sb.toString();
    }

    /**
     * Counts how many times <code>content</code> appears within <code>container</code> without string overlapping.
     *
     * @param container
     *            container string.
     * @param content
     *            content string.
     *
     * @return occurrences count.
     */
    public static int countOccurrences(String container, String content) {
        int lastIndex, currIndex = 0, occurrences = 0;
        while (true) {
            lastIndex = container.indexOf(content, currIndex);
            if (lastIndex == -1) {
                break;
            }
            currIndex = lastIndex + content.length();
            occurrences++;
        }
        return occurrences;
    }

    /**
     * Counts the number of <code>NL</code> in the given <i>in</i> string.
     *
     * @param in
     *            input string.
     *
     * @return the number of new line chars.
     */
    public static int countNL(String in) {
        return countOccurrences(in, "\n");
    }

    /**
     * Check whether string <code>candidatePrefix</code> is prefix of string <code>container</code>.
     *
     * @param candidatePrefix
     *            prefix to check
     * @param container
     *            container to check against
     *
     * @return <code>true</code> if <code>candidatePrefix</code> is prefix of <code>container</code>, <code>false</code>
     *         otherwise.
     */
    public static boolean isPrefix(String candidatePrefix, String container) {
        if (candidatePrefix == null || container == null) {
            throw new NullPointerException("Arguments must be not null.");
        }
        if (candidatePrefix.length() > container.length()) {
            return false;
        }
        for (int i = 0; i < candidatePrefix.length(); i++) {
            if (candidatePrefix.charAt(i) != container.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check whether string <code>candidateSuffix</code> is suffix of string <code>container</code>.
     *
     * @param candidateSuffix
     *            suffix to check
     * @param container
     *            container to check against
     *
     * @return <code>true</code> if <code>candidateSuffix</code> is prefix of <code>container</code>, <code>false</code>
     *         otherwise.
     */
    public static boolean isSuffix(String candidateSuffix, String container) {
        if (candidateSuffix == null || container == null) {
            throw new NullPointerException("Arguments must be not null.");
        }
        if (candidateSuffix.length() > container.length()) {
            return false;
        }
        final int lenDiff = container.length() - candidateSuffix.length();
        for (int i = candidateSuffix.length() - 1; i >= 0; i--) {
            if (candidateSuffix.charAt(i) != container.charAt(i + lenDiff)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Escapes all the unescaped double quotes when needed.
     *
     * @param in
     *            input string.
     *
     * @return unescaped output.
     */
    public static String escapeDoubleQuotes(String in) {
        final StringBuilder out = new StringBuilder();
        boolean escaped = false;
        char current;
        for (int i = 0; i < in.length(); i++) {
            current = in.charAt(i);
            if (current == '\\') {
                escaped = !escaped;
            } else if (current == '"' && !escaped) {
                out.append('\\');
            }
            out.append(current);
        }
        return out.toString();
    }

    /**
     * Escapes the <code>in</code> string as <b>JSON</b> string to let it being embeddable within a string field.
     *
     * @param in
     *            string to be escaped.
     *
     * @return escaped string.
     */
    public static String escapeAsJSONString(String in) {
        return escapeDoubleQuotes(in.replaceAll("\n", "\\\\n"));
    }

    /**
     * Builds a string composed of the given char <code>c</code> <code>n</code> times.
     *
     * @param c
     *            char to be multiplied.
     * @param times
     *            number of times.
     *
     * @return the string containing the multiplied char.
     */
    public static String multiply(char c, int times) {
        if (times <= 0) {
            throw new IllegalArgumentException("Invalid number of times, must be > 0 .");
        }
        final char[] buffer = new char[times];
        for (int i = 0; i < times; i++) {
            buffer[i] = c;
        }
        return new String(buffer);
    }

    /**
     * Changes string with following convention:
     * <ul>
     * <li>Changes '-' -&gt; '_'
     * <li>remove space characters and make first letter word uppercase: 'some string' -&gt; 'someString'
     * </ul>
     * If input string does not contains a whitespace than return unchanged.
     *
     * @param in
     *            an input string to convert to Java code convention
     *
     * @return the correctly formatter string as per Java spec.
     */
    public static String implementJavaNaming(String in) {

        in = in.trim().replaceAll("-", "_");

        if (in.matches("\\S+")) {
            return org.apache.commons.lang3.StringUtils.uncapitalize(in);
        }

        in = in.toLowerCase(Locale.ROOT);
        String[] words = in.split("\\s+");
        StringBuilder sb = new StringBuilder(in.length());
        sb.append(words[0]);
        for (int i = 1; i < words.length; i++) {
            sb.append(org.apache.commons.lang3.StringUtils.capitalize(words[i]));
        }
        return sb.toString();
    }

    private StringUtils() {
    }
}
